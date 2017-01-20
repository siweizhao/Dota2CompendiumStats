package compendium.stats;

import com.google.gson.JsonObject;

public class PlayerDetail {
	private String matchId;
	private String playerId;
	private String heroId;
	private int kills;
	private int assists;
	private int deaths;
	private int lh;
	private int gpm;
	
	public PlayerDetail(JsonObject obj, String matchId){
		this.matchId = matchId;
		this.playerId = JsonData.getString(obj, "account_id");
		this.heroId = Compendium.heroNames.get(JsonData.getInt(obj, "hero_id"));
		this.kills = JsonData.getInt(obj, "kills");
		this.assists = JsonData.getInt(obj, "assists");
		this.deaths = JsonData.getInt(obj, "deaths");
		this.lh = JsonData.getInt(obj, "last_hits");
		this.gpm = JsonData.getInt(obj, "gold_per_min");
	}
	
	public String getMatchId(){
		return matchId;
	}
	
	public String getPlayerId(){
		return playerId;
	}
	
	public String getHeroId(){
		return heroId;
	}
	
	public int getKills(){
		return kills;
	}
	
	public int getAssists(){
		return assists;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public int getLH(){
		return lh;
	}
	
	public int getGPM(){
		return gpm;
	}
}
