package compendium.stats;

public class HeroStats {
	
	int id;
	String name;
	
	private int numPicked;
	private int numBanned;
	int numGamesWon;
	
	private int mostGPM;
	String mostGPM_MatchId;
	int totalGPM;
	
	private int mostKills;
	private String mostKills_MatchId;
	int totalKills;
	
	private int mostAssists;
	String mostAssists_MatchId;
	int totalAssists;
	
	private int mostDeaths;
	String mostDeaths_MatchId;
	int totalDeaths;
	
	private int mostLH;
	String mostLH_MatchId;
	int totalLH;
	
	public HeroStats(int id, String name){
		this.id = id;
		this.name = name;
	}

	public void updateHeroStats(String matchId, boolean win, int kills, int assists, int deaths, int lh, int gpm){
		//Update total stats
		numPicked ++;
		totalKills += kills;
		totalAssists += assists;
		totalDeaths += deaths;
		totalLH += lh;
		totalGPM += gpm;
		
		if (win)
			numGamesWon ++;
		
		if (kills > mostKills){
			mostKills = kills;
			mostKills_MatchId = matchId;
		}
		if (assists > mostAssists){
			mostAssists = assists;
			mostAssists_MatchId = matchId;
		}
		if (deaths > mostDeaths){
			mostDeaths = deaths;
			mostDeaths_MatchId = matchId;
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
	
	public String getName(){
		return name;
	}
	
	public void banned(){
		numBanned ++;
	}
	public int getNumPicked() {
		return numPicked;
	}

	public int getNumBanned() {
		return numBanned;
	}

	public double getWinrate() {
		if (numPicked == 0)
			return 0;
		else 
			return (double)numGamesWon/numPicked;
	}

	public double getAvgKills() {
		if (numPicked == 0)
			return 0;
		else 
			return (double)totalKills/numPicked;
	}
	
	public double getAvgAssists() {
		if (numPicked == 0)
			return 0;
		else 
			return (double)totalAssists/numPicked;
	}

	public double getAvgDeaths() {
		if (numPicked == 0)
			return Integer.MAX_VALUE;
		else 
			return (double)totalDeaths/numPicked;
	}

	public double getAvgLH() {
		if (numPicked == 0)
			return 0;
		else 
			return (double)totalLH/numPicked;
	}

	public double getAvgGPM() {
		if (numPicked == 0)
			return 0;
		else 
			return (double)totalGPM/numPicked;
	}

	public int getMostKills() {
		return mostKills;
	}

	public int getMostLH() {
		return mostLH;
	}

	public int getMostDeaths() {
		return mostDeaths;
	}

	public int getMostAssists() {
		return mostAssists;
	}
	
	public int getMostGPM() {
		return mostGPM;
	}

	public String getMostKills_MatchId() {
		return mostKills_MatchId;
	}
	
	public String getMostAssists_MatchId() {
		return mostAssists_MatchId;
	}
	
	public String getMostDeaths_MatchId() {
		return mostDeaths_MatchId;
	}
	
	public String getMostLH_MatchId() {
		return mostLH_MatchId;
	}
	
	public String getMostGPM_MatchId() {
		return mostGPM_MatchId;
	}
}
