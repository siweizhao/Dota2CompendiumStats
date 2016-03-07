package compendium.stats;

public class PlayerStats {

	private String id;
	String name;
	
	int[] heroesPlayed;
	
	int numGamesPlayed;
	int numGamesWon;
	
	private int mostLH;
	String mostLH_MatchId;
	int totalLH;
	
	private int mostGPM;
	String mostGPM_MatchId;
	int totalGPM;
	
	private int mostKills;
	int totalKills;
	String mostKills_MatchId;
	
	private int mostAssists;
	int totalAssists;
	String mostAssists_MatchId;
	
	int leastDeaths;
	String leastDeaths_MatchId;
	int totalDeaths;

	public PlayerStats(String id){
		this.heroesPlayed = new int[Dota2Const.NUM_HEROES];
		this.id = id;
		this.leastDeaths = Integer.MAX_VALUE;
	}
	
	//to be continued
	public void UpdatePlayerStats(String matchId, int heroId, int kills, int assists, int deaths,int lh, int gpm){
		//Update total stats
		heroesPlayed[heroId] ++;
		numGamesPlayed ++;
		totalKills += kills;
		totalAssists += assists;
		totalDeaths += deaths;
		totalLH += lh;
		totalGPM += gpm;
		
		//Update records if broken
		if (kills > mostKills){
			mostKills = kills;
			mostKills_MatchId = matchId;
		}
		if (assists > mostAssists){
			mostAssists = assists;
			mostAssists_MatchId = matchId;
		}		
		if (deaths < leastDeaths){
			leastDeaths = deaths;
			leastDeaths_MatchId = matchId;
		}	
		if (lh > mostLH){
			mostLH = lh;
			mostLH_MatchId = matchId;
		}		
		if (gpm > mostGPM){
			mostGPM = gpm;
			mostGPM_MatchId = matchId;
		}
	}

	public double getAvgKills() {
		return (double)totalKills/numGamesPlayed;
	}

	public int getMostKills() {
		return mostKills;
	}

	public void setMostKills(int mostKills) {
		this.mostKills = mostKills;
	}

	public double getAvgDeaths() {
		return (double)totalDeaths/numGamesPlayed;
	}

	public double getAvgAssists() {
		return (double)totalAssists/numGamesPlayed;
	}

	public int getMostAssists() {
		return mostAssists;
	}

	public void setMostAssists(int mostAssists) {
		this.mostAssists = mostAssists;
	}

	public double getAvgLH() {
		return (double)totalLH/numGamesPlayed;
	}

	public int getMostLH() {
		return mostLH;
	}

	public int getMostGPM() {
		return mostGPM;
	}

	public double getAvgGPM() {
		return (double)totalGPM/numGamesPlayed;
	}

	public int getNumHeroesPlayed() {
		int numHeroesPlayed = 0;
		for (int i = 1; i < Dota2Const.NUM_HEROES; i++){
			if (heroesPlayed[i] > 0)
				numHeroesPlayed ++;
		}
		return numHeroesPlayed;
	}

	public String getId() {
		return id;
	}


	public String getMostKills_MatchId() {
		return mostKills_MatchId;
	}
	
	public String getMostAssists_MatchId() {
		return mostAssists_MatchId;
	}
	
	public String getMosGPM_MatchId() {
		return mostGPM_MatchId;
	}
	
	public String getMostLH_MatchId() {
		return mostLH_MatchId;
	}
}
