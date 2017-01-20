package compendium.stats;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.*;
import com.google.gson.reflect.*;


public class dota2WebAPI {

	final static String DEFAULT_START_DATE = "20100101";
	
	String MATCH_HISTORY_REQ = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
	String MATCH_DETAILS_REQ = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=";
	String HERO_LIST_REQ = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?language=en_us&key=";	
	
	private String webAPIKey;
	Compendium compendium;
	
	public dota2WebAPI(String webAPIKey, int numResults, int minNumGames) throws Exception{
		this.webAPIKey = webAPIKey;
		this.compendium = new Compendium(createHeroList(), numResults, minNumGames);
	}
	
	public JsonObject getJsonObj(URL url) throws Exception{
		HttpURLConnection req = (HttpURLConnection) url.openConnection();
		req.connect();
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(new InputStreamReader((InputStream) req.getContent()));
		return root.getAsJsonObject();
	}
	
	public Map<Integer, String> createHeroList() throws Exception{
		JsonObject rootObj = getJsonObj(new URL(HERO_LIST_REQ + webAPIKey));   
		JsonArray heroes = rootObj.getAsJsonObject("result").getAsJsonArray("heroes");
		Map<Integer, String> heroNames = new HashMap<>();
		for (JsonElement e : heroes){
			JsonObject obj = e.getAsJsonObject();
			heroNames.put(JsonData.getInt(obj, "id"), JsonData.getString(obj, "localized_name"));
		}
		return heroNames;
	}
	
	
	public String createRequestURL(String leagueId, String startAtMatchId) throws Exception{
		return MATCH_HISTORY_REQ + this.webAPIKey + "&league_id=" + leagueId + "&start_at_match_id=" + startAtMatchId;
	}
	
	public void parseMatchDetails(String matchId) throws Exception{
		JsonObject rootObj  = getJsonObj(new URL(MATCH_DETAILS_REQ + this.webAPIKey + "&match_id=" + matchId)).getAsJsonObject("result");
		this.compendium.addMatches(rootObj);
	}
	
	public void printStats(){
		compendium.printStats();
	}
	
	private static long dateToEpoch (String startDate){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = df.parse(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime()/1000;
	}
	
	public void getMatchHistory(String leagueId, String startDate, String startAtMatchId) throws Exception{
		JsonObject rootObj = getJsonObj(new URL(createRequestURL(leagueId,startAtMatchId))).getAsJsonObject("result");
		int remainingMatches = rootObj.get("results_remaining").getAsInt();
		JsonArray matchList = rootObj.getAsJsonArray("matches");
		long startTime = dateToEpoch(startDate);
		
		String lastMatchId = "";
		for (JsonElement e : matchList){
			JsonObject obj = e.getAsJsonObject();
			long matchTime = JsonData.getLong(obj, "start_time");
			String match_id = JsonData.getString(obj, "match_id");
			if (matchTime > startTime && match_id != null && !match_id.isEmpty()){
				parseMatchDetails(match_id);
			}	
			lastMatchId = match_id;
		}
		
		if (remainingMatches > 0)
			getMatchHistory(leagueId, startDate, lastMatchId);
		
	}
	
	public static void main(String[] args) throws Exception{

		if (args.length < 4){
			System.err.println("Not enough arguments");
			System.exit(1);
		}

		String webAPIKey = args[0];
		String leagueId = args[1];
		int numResults = Integer.parseInt(args[2]);
		int numMinimumGamesPlayed = Integer.parseInt(args[3]);
		String startDate = DEFAULT_START_DATE;
		
		//Optional start date for matches
		if (args.length > 4){
			startDate = args[4];
		}
		
		dota2WebAPI webAPI = new dota2WebAPI(webAPIKey, numResults, numMinimumGamesPlayed);
		webAPI.getMatchHistory(leagueId, startDate, "");
		webAPI.printStats();
	}
}
