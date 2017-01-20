package compendium.stats;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MatchDetail {
	private String matchId;
	private String radiantTeam;
	private String direTeam;
	private boolean radiantWin;	
	
	private List<String> bans;
	private List<String> picks;
	
	private List<PlayerDetail> playerDetails; 
	private List<TeamDetail> teamDetails;
	
	public MatchDetail(JsonObject resultObj){
		this.matchId = JsonData.getString(resultObj, "match_id");
		this.radiantWin = JsonData.getBoolean(resultObj, "radiant_win");
		this.radiantTeam = JsonData.getString(resultObj, "radiant_name");
		this.direTeam = JsonData.getString(resultObj, "dire_name");

		this.bans = new ArrayList<>();
		this.picks = new ArrayList<>();
		JsonArray pickBans = resultObj.getAsJsonArray("picks_bans");
		for (JsonElement pb : pickBans){
			JsonObject pickBansObj = pb.getAsJsonObject();
			if (JsonData.getBoolean(pickBansObj, "is_pick"))
				this.picks.add(Compendium.heroNames.get(JsonData.getInt(pickBansObj, "hero_id")));
			else
				this.bans.add(Compendium.heroNames.get(JsonData.getInt(pickBansObj, "hero_id")));
		}
		
		this.teamDetails = new ArrayList<>();		
		this.teamDetails.add(new TeamDetail(resultObj, true));		
		this.teamDetails.add(new TeamDetail(resultObj, false));		

		this.playerDetails = new ArrayList<>();
		JsonArray players = resultObj.getAsJsonArray("players");
		for (JsonElement player : players){
			this.playerDetails.add(new PlayerDetail(player.getAsJsonObject(), this.matchId));
		}
	}

	public List<String> getPicks(){
		return picks;
	}

	public List<String> getBans(){
		return bans;
	}

	public List<PlayerDetail> getPlayerDetails(){
		return playerDetails;
	}
	
	public boolean getRadiantWin(){
		return radiantWin;
	}
	
	public String getRadiantTeam(){
		return radiantTeam;
	}
	
	public String getDireTeam(){
		return direTeam;
	}
	
	public List<TeamDetail> getTeamDetails(){
		return teamDetails;
	}
}
