package compendium.stats;


public class TeamStats {

	private String id;
	String name;
	
	int numGamesPlayed;
	int[] heroesPicked;
	
	private int mostKills;
	private String mostKills_MatchId;
	private int totalKills;
	
	private int mostAssists;
	private String mostAssists_MatchId;
	int totalAssists;
	
	private int longestGameWon;
	String longestGameWon_MatchId;
	
	private int shortestGameWon;
	String shortestGameWon_MatchId;
	
	private int leastDeaths;
	private String leastDeaths_MatchId;
	
	int totalGameLength;		
	
	public TeamStats(String id, String name){
		this.id = id;
		this.name = name;
		this.leastDeaths = Integer.MAX_VALUE; 
		this.shortestGameWon = Integer.MAX_VALUE; 
		this.heroesPicked = new int[Dota2Const.NUM_HEROES];
	}

	public void updateTeamStats(String matchId, boolean win, int[] bans, int[] picks, int kills, int assists, int deaths, int duration){
		//Update total stats
		numGamesPlayed ++;
		totalKills += kills;
		totalAssists += assists;
		totalGameLength += duration;
		
		//Update picks and bans
		for (int i = 0; i < bans.length; i++){
			heroesPicked[bans[i]]++;
		}		
		for (int i = 0; i < picks.length; i++){
				heroesPicked[picks[i]]++;
		}

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
		if (win){
			if (duration > longestGameWon){
				longestGameWon = duration;
				longestGameWon_MatchId = matchId;
			}
			if (duration < shortestGameWon){
				shortestGameWon = duration;
				shortestGameWon_MatchId = matchId;
			}		
		}
	}
	
	public int getMostKills() {
		return mostKills;
	}

	public double getAvgKills() {
		if (numGamesPlayed == 0)
			return 0;
		else
			return (double)totalKills/numGamesPlayed;
	}

	public int getLeastDeaths() {
		return leastDeaths;
	}

	public int getMostAssists() {
		return mostAssists;
	}

	public double getLongestGameWon() {
		return longestGameWon;
	}

	public double getShortestGameWon() {
		return shortestGameWon;
	}

	public double getAvgGameLength() {
		if (numGamesPlayed == 0)
			return 0;
		else
			return (double)totalGameLength/numGamesPlayed;
	}

	public int getNumHeroesPicked() {
		int numHeroesPicked = 0;
		for (int i = 0; i < heroesPicked.length; i++){
			if (heroesPicked[i] > 0){
				numHeroesPicked ++;
			}
		}
		return numHeroesPicked;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getMostKills_MatchId() {
		return mostKills_MatchId;
	}

	public String getMostAssists_MatchId() {
		return mostAssists_MatchId;
	}

	public String getLeastDeaths_MatchId() {
		return leastDeaths_MatchId;
	}

	public String getShortestGameWon_MatchId() {
		return shortestGameWon_MatchId;
	}
	
	public String getLongestGameWon_MatchId() {
		return longestGameWon_MatchId;
	}
	
	
	
}
