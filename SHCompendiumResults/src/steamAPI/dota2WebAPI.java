package steamAPI;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.*;

import compendium.stats.CompendiumStats;
import compendium.stats.Dota2Const;

public class dota2WebAPI {

	final static String DEFAULT_START_DATE = "20100101";
	
	String MATCH_HISTORY_REQ = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
	String MATCH_DETAILS_REQ = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=";
	String HERO_LIST_REQ = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?language=en_us&key=";
	
	String webAPIKey;
	CompendiumStats compendium;
	
	public dota2WebAPI(String webAPIKey, int numMinimumGamesPlayed) throws Exception{
		this.webAPIKey = webAPIKey;
		this.compendium = new CompendiumStats(createHeroList(), numMinimumGamesPlayed);
	}
	
	public JsonObject getJsonObj(URL url) throws Exception{
		HttpURLConnection req = (HttpURLConnection) url.openConnection();
		req.connect();
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(new InputStreamReader((InputStream) req.getContent()));
		return root.getAsJsonObject();
	}
	
	
	public String[] createHeroList() throws Exception{
		JsonObject rootObj = getJsonObj(new URL(HERO_LIST_REQ + webAPIKey));   
		JsonArray heroList = rootObj.getAsJsonObject("result").getAsJsonArray("heroes");
		
		String[] heroNameList = new String[Dota2Const.NUM_HEROES];
		for (int i = 0; i < heroList.size(); i++){
			int heroId = heroList.get(i).getAsJsonObject().get("id").getAsInt();
			String name = heroList.get(i).getAsJsonObject().get("localized_name").getAsString();
			heroNameList[heroId] = name;		
		}
		return heroNameList;
	}
	
	public String createRequestURL(String leagueId, String key) throws Exception{
		return MATCH_HISTORY_REQ + webAPIKey + "&league_id=" + leagueId;
	}
	
	public void parseMatchDetails(String matchId) throws Exception{
		JsonObject rootObj  = getJsonObj(new URL(MATCH_DETAILS_REQ + webAPIKey + "&match_id=" + matchId));
		this.compendium.updateCompendiumStats(matchId, rootObj);
	}
	
	
	public void getMatchHistory(String leagueId, String startDate) throws Exception{
		JsonObject rootObj = getJsonObj(new URL(createRequestURL(leagueId, webAPIKey)));
		JsonArray matchList = rootObj.getAsJsonObject("result").getAsJsonArray("matches");
		
		
		for (int i = 0; i < matchList.size(); i ++){
			//Get matches after certain date
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Date date = df.parse(startDate);
			long minDate = date.getTime()/1000;
			long matchTime = matchList.get(i).getAsJsonObject().get("start_time").getAsLong();
			if (matchTime > minDate){
				String match_id = matchList.get(i).getAsJsonObject().get("match_id").getAsString();
				if (match_id != null && !match_id.isEmpty())
					parseMatchDetails(match_id);
			}
		}
		compendium.printTournamentStats();
		
	}
	
	public static void main(String[] args) throws Exception{

		String webAPIKey = args[0];
		String leagueId = args[1];
		String startDate = DEFAULT_START_DATE;
		int numMinimumGamesPlayed = 1;
		//Optional start date for matches
		if (args.length >= 2){
			startDate = args[2];
		}
		if (args.length >= 3){
			numMinimumGamesPlayed = Integer.parseInt(args[3]);
		}

		dota2WebAPI webAPI = new dota2WebAPI(webAPIKey, numMinimumGamesPlayed);
		webAPI.getMatchHistory(leagueId, startDate);
	}
}
