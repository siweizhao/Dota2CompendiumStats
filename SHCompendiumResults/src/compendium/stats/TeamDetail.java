package compendium.stats;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TeamDetail {
	private String matchId;
	private String teamName;
	private int kills;
	private int deaths;
	private int assists;
	private int game_duration_seconds;
	private boolean win;
	private List<String> heroesPicked;
	
	
	public TeamDetail(JsonObject resultObj, boolean radiantSide){
		this.matchId = JsonData.getString(resultObj, "match_id");
		this.win = !(radiantSide ^ JsonData.getBoolean(resultObj, "radiant_win"));
		this.teamName = radiantSide ? JsonData.getString(resultObj, "radiant_name") : JsonData.getString(resultObj, "dire_name");
		this.game_duration_seconds = JsonData.getInt(resultObj, "duration");
		this.heroesPicked = new ArrayList<>();
		JsonArray players = resultObj.getAsJsonArray("players");

		int counter = 0;
		for (JsonElement player : players){
			if (radiantSide){
				if (++counter > 5) break;
			} else {
				if (++counter < 6) continue;
			}
			JsonObject playerObj = player.getAsJsonObject();
			this.kills += JsonData.getInt(playerObj, "kills");
			this.deaths += JsonData.getInt(playerObj, "deaths");
			this.assists += JsonData.getInt(playerObj, "assists");
			this.heroesPicked.add(Compendium.heroNames.get(JsonData.getInt(playerObj, "hero_id")));
		}
		
	}
	
	public String getMatchId(){
		return matchId;
	}
	
	public int getKills(){
		return kills;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public int getAssists(){
		return assists;
	}
	
	public int getGameDuration(){
		return game_duration_seconds;
	}
	
	public String getTeamName(){
		return teamName;
	}
	
	public boolean getWin(){
		return win;
	}
	
	public List<String> getHeroesPicked(){
		return heroesPicked;
	}
}
