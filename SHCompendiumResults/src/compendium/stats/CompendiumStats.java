package compendium.stats;
import compendium.stats.*;

import java.util.ArrayList;

import com.google.gson.*;

public class CompendiumStats {
	
	HeroStats[] heroStatsList;
	ArrayList<PlayerStats> playerStatsList;
	ArrayList<TeamStats> teamStatsList;
	
	int numGames;
	int numMinimumGamesPlayed;
	ArrayList<String> heroesPicked;
	ArrayList<String> heroesBanned;
	
	int mostCombinedKills;
	String mostCombinedKills_MatchId;
	
	public CompendiumStats(String[] heroNameList, int numMinimumGamesPlayed){
		heroStatsList = new HeroStats[Dota2Const.NUM_HEROES];
		for (int i = 1; i < Dota2Const.NUM_HEROES; i ++){
			HeroStats hero = new HeroStats(i, heroNameList[i]);
			heroStatsList[i] = hero;
		}
		this.numMinimumGamesPlayed = numMinimumGamesPlayed;
		playerStatsList = new ArrayList<PlayerStats>();
		teamStatsList = new ArrayList<TeamStats>();
		
	}
	
	public void updateCompendiumStats(String matchId, JsonObject rootObj){
		numGames++;
		JsonObject resultObj = rootObj.getAsJsonObject("result");
		
		
		boolean radiantWin = resultObj.get("radiant_win").getAsBoolean();
		int duration = resultObj.get("duration").getAsInt();
		
		String radiantTeamId = resultObj.get("radiant_team_id").getAsString();
		String radiantTeamName = resultObj.get("radiant_name").getAsString();
		String direTeamId = resultObj.get("dire_team_id").getAsString();
		String direTeamName = resultObj.get("dire_name").getAsString();
		
		if (!hasTeam(radiantTeamId))
			addTeam(radiantTeamId, radiantTeamName);

		if (!hasTeam(direTeamId))
			addTeam(direTeamId, direTeamName);
		
		//Parse pick bans
		int[] radiantBans = new int[5];
		int[] radiantPicks = new int[5];
		int[] direBans = new int[5];
		int[] direPicks = new int[5];
		
		JsonArray pickBans = resultObj.getAsJsonArray("picks_bans");

		int radiantBanNum = 0;
		int direBanNum = 0;
		int radiantPickNum = 0;
		int direPickNum = 0;
		for (int i = 0; i < pickBans.size(); i++){
			JsonObject obj = pickBans.get(i).getAsJsonObject();
			
			if (obj.get("is_pick").getAsBoolean() == false){
				if (obj.get("team").getAsInt() == 0){
					radiantBans[radiantBanNum] = obj.get("hero_id").getAsInt();
					radiantBanNum ++;
					heroStatsList[obj.get("hero_id").getAsInt()].banned();
				} else {
					direBans[direBanNum] = obj.get("hero_id").getAsInt();
					direBanNum ++;
					heroStatsList[obj.get("hero_id").getAsInt()].banned();
				}	
			} else {
				if (obj.get("team").getAsInt() == 0){
					radiantPicks[radiantPickNum] = obj.get("hero_id").getAsInt();
					radiantPickNum ++;
				} else {
					direPicks[direPickNum] = obj.get("hero_id").getAsInt();
					direPickNum ++;
				}	
			}
		}
		

		JsonArray players = resultObj.getAsJsonArray("players");
		
		int totalKills = 0;
		int totalAssists = 0;
		int totalDeaths = 0;
		int radiantKills = 0;

		for (int i = 0; i < players.size(); i++){
			JsonObject obj = players.get(i).getAsJsonObject();
			String playerId = obj.get("account_id").getAsString();
			int heroId = obj.get("hero_id").getAsInt();
			int kills = obj.get("kills").getAsInt();
			int assists = obj.get("assists").getAsInt();
			int deaths = obj.get("deaths").getAsInt();
			int lh = obj.get("last_hits").getAsInt();
			int gpm = obj.get("gold_per_min").getAsInt();
			
			if (!hasPlayer(playerId))
				addPlayer(playerId);
			PlayerStats player = getPlayer(playerId);
			player.UpdatePlayerStats(matchId, heroId, kills, assists, deaths, lh, gpm);

			
			HeroStats hero = getHero(heroId);
			if (i >= 0 && i <= Dota2Const.LAST_RADIANT_PLAYER){
				hero.updateHeroStats(matchId, radiantWin, kills, assists, deaths, lh, gpm);
			} else {
				hero.updateHeroStats(matchId, !radiantWin, kills, assists, deaths, lh, gpm);
			}
			
			
			totalKills += kills;
			totalAssists += assists;
			totalDeaths += deaths;
			
			if (i == Dota2Const.LAST_RADIANT_PLAYER){
				getTeam(radiantTeamId).updateTeamStats(matchId, radiantWin,radiantBans, radiantPicks, totalKills, totalAssists, totalDeaths, duration);				
				radiantKills = totalKills;
				///Reset stats for dire
				totalKills = 0;
				totalAssists = 0;
				totalDeaths = 0;
			} else if(i == Dota2Const.LAST_DIRE_PLAYER){
				getTeam(direTeamId).updateTeamStats(matchId, !radiantWin,direBans, direPicks, totalKills, totalAssists, totalDeaths, duration);				
				int combinedKills = radiantKills + totalKills;
				if (mostCombinedKills < combinedKills){
					mostCombinedKills = combinedKills;
					mostCombinedKills_MatchId = matchId;
				}
			}
		}
		
	}

	public void printTournamentStats(){
		//Hero Stats
		System.out.println("Hero Stats (minimum " + numMinimumGamesPlayed + " games played)" );
		HeroStats mostPickedHero = getHeroMostPicked();
		HeroStats mostBannedHero = getHeroMostBanned();
		HeroStats highestWRHero = getHeroHighestWR();
		HeroStats highestKillAvgHero = getHeroHighestKillAvg();
		HeroStats highestAssistAvgHero = getHeroHighestAssistAvg();
		HeroStats leastDeathAvgHero = getHeroLeastDeathAvg();
		HeroStats highestLHAvgHero = getHeroHighestLHAvg();
		HeroStats highestGPMAvgHero = getHeroHighestGPMAvg();
		HeroStats mostKillsHero = getHeroMostKills();
		HeroStats mostLHHero = getHeroMostLH();
		
		System.out.printf("%-30s%-20s%10s%20s\n", "Category", "Hero", "Result", "Match Id");
		System.out.printf("%-30s%-20s%10d\n", "Most Picked:", mostPickedHero.getName(), mostPickedHero.getNumPicked());
		if (mostBannedHero != null)
			System.out.printf("%-30s%-20s%10d\n", "Most Banned:", mostBannedHero.getName(), mostBannedHero.getNumBanned());
		if (highestWRHero != null)
			System.out.printf("%-30s%-20s%10.2f\n", "Highest WR:", highestWRHero.getName(), highestWRHero.getWinrate());
		if (highestKillAvgHero != null)
			System.out.printf("%-30s%-20s%10.2f\n", "Highest Kill Avg:", highestKillAvgHero.getName(), highestKillAvgHero.getAvgKill());
		if (highestAssistAvgHero != null)
			System.out.printf("%-30s%-20s%10.2f\n", "Highest Assist Avg:", highestAssistAvgHero.getName(), highestAssistAvgHero.getAvgAssist());
		if (leastDeathAvgHero != null)
			System.out.printf("%-30s%-20s%10.2f\n", "Least Death Avg:", leastDeathAvgHero.getName(), leastDeathAvgHero.getAvgDeath());
		if (highestLHAvgHero != null)
			System.out.printf("%-30s%-20s%10.2f\n", "Highest LH Avg:", highestLHAvgHero.getName(), highestLHAvgHero.getAvgLH());
		if (highestLHAvgHero != null)
			System.out.printf("%-30s%-20s%10.2f\n", "Highest GPM Avg:", highestGPMAvgHero.getName(), highestGPMAvgHero.getAvgGPM());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most Kills:", mostKillsHero.getName(), mostKillsHero.getMostKills(),mostKillsHero.getMostKills_MatchId());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most LH:", mostLHHero.getName(), mostLHHero.getMostLH(), mostLHHero.getMostLH_MatchId());

		//Player Stats
		System.out.println("Player Stats");
		
		PlayerStats highestKillAvgPlayer = getPlayerHighestKillAvg();
		PlayerStats mostKillsPlayer = getPlayerMostKills();
		PlayerStats lowestDeathsAvgPlayer = getPlayerLowestDeathsAvg();
		PlayerStats highestAssistsAvgPlayer = getPlayerHighestAssistAvg();
		PlayerStats mostAssistsPlayer = getPlayerMostAssists();
		PlayerStats highestLHAvgPlayer = getPlayerHighestLHAvg();
		PlayerStats mostLHPlayer = getPlayerMostLH();
		PlayerStats mostGPMAvgPlayer = getPlayerMostGPMAvg();
		PlayerStats mostGPMPlayer = getPlayerMostGPM();
		PlayerStats mostDiffHeroesPlayer = getPlayerMostDiffHeroes();
		
		System.out.printf("%-30s%-20s%10s%20s\n", "Category", "Player Id", "Result", "Match Id");
		System.out.printf("%-30s%-20s%10.2f\n", "Highest Kill Avg:", highestKillAvgPlayer.getId(), highestKillAvgPlayer.getAvgKills());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most Kills:", mostKillsPlayer.getId(), mostKillsPlayer.getMostKills(), mostKillsPlayer.getMostKills_MatchId());
		System.out.printf("%-30s%-20s%10.2f\n", "Lowest Deaths Avg:", lowestDeathsAvgPlayer.getId(), lowestDeathsAvgPlayer.getAvgDeaths());
		System.out.printf("%-30s%-20s%10.2f\n", "Highest Assists Avg:", highestAssistsAvgPlayer.getId(), highestAssistsAvgPlayer.getAvgAssists());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most Assists:", mostAssistsPlayer.getId(), mostAssistsPlayer.getMostAssists(), mostAssistsPlayer.getMostAssists_MatchId());
		System.out.printf("%-30s%-20s%10.2f\n", "Highest LH Avg:", highestLHAvgPlayer.getId(), highestLHAvgPlayer.getAvgLH());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most LH:", mostLHPlayer.getId(), mostLHPlayer.getMostLH(), mostLHPlayer.getMostLH_MatchId());
		System.out.printf("%-30s%-20s%10.2f\n", "Highest GPM Avg:", mostGPMAvgPlayer.getId(), mostGPMAvgPlayer.getAvgGPM());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most GPM:", mostGPMPlayer.getId(), mostGPMPlayer.getMostGPM(), mostGPMPlayer.getMosGPM_MatchId());
		System.out.printf("%-30s%-20s%10d\n", "Most Diff Heroes:", mostDiffHeroesPlayer.getId(), mostDiffHeroesPlayer.getNumHeroesPlayed());

		//Team Stats
		System.out.println();
		System.out.println("Team Stats");		
		
		TeamStats mostKillsTeam = getTeamMostKills();
		TeamStats highestKillAvgTeam = getTeamHighestKillAvg();
		TeamStats fewestDeathsTeam = getTeamFewestDeaths();
		TeamStats mostAssistsTeam = getTeamMostAssists();
		TeamStats winLongestGameTeam = getTeamWinLongestGame();
		TeamStats winShortestGameTeam = getTeamWinShortestGame();
		TeamStats highestGameLengthAvgTeam = getTeamHigestGameLengthAvg();
		TeamStats mostDiffHeroesTeam = getTeamMostDiffHeroes();
		TeamStats leastDiffHeroesTeam = getTeamLeastDiffHeroes();
		
		System.out.printf("%-30s%-20s%10s%20s\n", "Category", "Team", "Result", "Match Id");
		System.out.printf("%-30s%-20s%10d%20s\n", "Most Kills:", mostKillsTeam.getName(), mostKillsTeam.getMostKills(), mostKillsTeam.getMostKills_MatchId());
		System.out.printf("%-30s%-20s%10.2f\n", "Highest Kill Avg:", highestKillAvgTeam.getName(), highestKillAvgTeam.getAvgKills());
		System.out.printf("%-30s%-20s%10d%20s\n", "Fewest Deaths:", fewestDeathsTeam.getName(), fewestDeathsTeam.getLeastDeaths(), fewestDeathsTeam.getLeastDeaths_MatchId());
		System.out.printf("%-30s%-20s%10d%20s\n", "Most Assists:", mostAssistsTeam.getName(), mostAssistsTeam.getMostAssists(), mostAssistsTeam.getMostAssists_MatchId());
		System.out.printf("%-30s%-20s%10.2f%20s\n", "Win Longest Game:", winLongestGameTeam.getName(), winLongestGameTeam.getLongestGameWon()/60, winLongestGameTeam.getLongestGameWon_MatchId());
		System.out.printf("%-30s%-20s%10.2f%20s\n", "Win Shortest game", winShortestGameTeam.getName(), winShortestGameTeam.getShortestGameWon()/60, winShortestGameTeam.getShortestGameWon_MatchId());
		System.out.printf("%-30s%-20s%10.2f\n", "Highest Game Length Avg:", highestGameLengthAvgTeam.getName(), highestGameLengthAvgTeam.getAvgGameLength()/60);
		System.out.printf("%-30s%-20s%10d\n", "Most Diff Heroes:", mostDiffHeroesTeam.getName(), mostDiffHeroesTeam.getNumHeroesPicked());
		System.out.printf("%-30s%-20s%10d\n", "Least Diff Heroes:", leastDiffHeroesTeam.getName(), leastDiffHeroesTeam.getNumHeroesPicked());
                             
		//Tournament Stats
		System.out.println();
		System.out.println("Tournament Stats");
		
		int totalGamesPlayed = getTotalGamesPlayed();
		int totalHeroesPicked = getNumHeroesPicked();
		int totalHeroesBanned = getNumHeroesBanned();
		int mostCombinedKills = getMostCombinedKills();
		double longestGameDuration = getLongestGameDuration();
		double shortestGameDuration = getShortestGameDuration();
		HeroStats mostDeathsHero = getHeroMostDeaths();
		HeroStats mostAssistsHero = getHeroMostAssists();
		HeroStats mostGPMHero = getHeroHigestGPM();
		
		System.out.printf("%-30s%10s%20s\n", "Category", "Result", "Match Id");
		System.out.printf("%-30s%10d\n", "Total Games Played:", totalGamesPlayed);
		System.out.printf("%-30s%10d\n", "Number of Heros Picked:", totalHeroesPicked);
		System.out.printf("%-30s%10d\n", "Number of Heros Banned:", totalHeroesBanned);
		System.out.printf("%-30s%10d%20s\n", "Most Combined Kills:", mostCombinedKills,mostCombinedKills_MatchId );
		System.out.printf("%-30s%10.2f%20s\n", "Longest Game Duration", longestGameDuration/60, winLongestGameTeam.getLongestGameWon_MatchId());
		System.out.printf("%-30s%10.2f%20s\n", "Shortest Game Duration", shortestGameDuration/60, winShortestGameTeam.getLongestGameWon_MatchId());
		System.out.printf("%-30s%10d%20s\n", "Most Kills by Hero:", mostKillsHero.getMostKills(), mostKillsHero.getMostKills_MatchId());
		System.out.printf("%-30s%10d%20s\n", "Most Deaths by Hero:", mostDeathsHero.getMostDeaths(), mostDeathsHero.getMostDeaths_MatchId());
		System.out.printf("%-30s%10d%20s\n", "Most Assists by Hero:", mostAssistsHero.getMostAssists(), mostAssistsHero.getMostAssists_MatchId());
		System.out.printf("%-30s%10d%20s\n", "Most GPM by Hero:", mostGPMHero.getMostGPM(), mostGPMHero.getMostGPM_MatchId());

	}
	
	public boolean hasPlayer(String id){
		if (playerStatsList == null)
			return false;
		
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getId().equals(id)){
				return true;
			}
		}		
		return false;	
	}
	
	public void addPlayer(String id){
		PlayerStats player = new PlayerStats(id);
		playerStatsList.add(player);
	}
	
	public PlayerStats getPlayer(String id){
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getId().equals(id))
				return playerStatsList.get(i);
		}
		return null;
	}
	
	public boolean hasTeam(String id){
		if (teamStatsList == null)
			return false;
		
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getId().equals(id)){
				return true;
			}
		}		
		return false;	
	}
	
	public void addTeam(String id, String name){
		TeamStats team = new TeamStats(id, name);
		teamStatsList.add(team);
	}
	
	
	public TeamStats getTeam(String id){
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getId().equals(id))
				return teamStatsList.get(i);
		}
		return null;
	}
	
	public HeroStats getHero(int heroId){
		return heroStatsList[heroId];
	}
	
	//Heroes
	public HeroStats getHeroMostPicked(){
		HeroStats result = null;
		int mostPicked = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getNumPicked() > mostPicked){
				mostPicked = heroStatsList[i].getNumPicked();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroMostBanned(){
		HeroStats result = null;
		int mostBanned = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getNumBanned() > mostBanned){
				mostBanned = heroStatsList[i].getNumBanned();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroHighestWR(){
		HeroStats result = null;
		double highestWR = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getWinrate() > highestWR && heroStatsList[i].getNumPicked() >= numMinimumGamesPlayed){
				highestWR = heroStatsList[i].getWinrate();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroHighestKillAvg(){
		HeroStats result = null;
		double highestKillAvg = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getAvgKill() > highestKillAvg && heroStatsList[i].getNumPicked() >= numMinimumGamesPlayed){
				highestKillAvg = heroStatsList[i].getAvgKill();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroHighestAssistAvg(){
		HeroStats result = null;
		double highestAssistAvg = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getAvgAssist() > highestAssistAvg && heroStatsList[i].getNumPicked() >= numMinimumGamesPlayed){
				highestAssistAvg = heroStatsList[i].getAvgAssist();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroLeastDeathAvg(){
		HeroStats result = null;
		double lowestDeathAvg = Integer.MAX_VALUE;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getAvgDeath() < lowestDeathAvg && heroStatsList[i].getNumPicked() >= numMinimumGamesPlayed){
				lowestDeathAvg = heroStatsList[i].getAvgDeath();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroHighestLHAvg(){
		HeroStats result = null;
		double highestLHAvg = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getAvgLH() > highestLHAvg && heroStatsList[i].getNumPicked() >= numMinimumGamesPlayed){
				highestLHAvg = heroStatsList[i].getAvgLH();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroHighestGPMAvg(){
		HeroStats result = null;
		double highestGPMAvg = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getAvgGPM() > highestGPMAvg && heroStatsList[i].getNumPicked() >= numMinimumGamesPlayed){
				highestGPMAvg = heroStatsList[i].getAvgGPM();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroMostKills(){
		HeroStats result = null;
		int mostKills = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getMostKills() > mostKills){
				mostKills = heroStatsList[i].getMostKills();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroMostLH(){
		HeroStats result = null;
		int mostLH = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getMostLH() > mostLH){
				mostLH = heroStatsList[i].getMostLH();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	//Team
	public String getTeamWinner(){
		return null;
	}
	
	public TeamStats getTeamMostKills(){
		TeamStats result = null;
		int mostKills = 0;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getMostKills() > mostKills){
				mostKills = teamStatsList.get(i).getMostKills();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamHighestKillAvg(){
		TeamStats result = null;
		double highestKillAvg = 0;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getAvgKills() > highestKillAvg){
				highestKillAvg = teamStatsList.get(i).getAvgKills();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamFewestDeaths(){
		TeamStats result = null;
		int fewestDeath = Integer.MAX_VALUE;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getLeastDeaths() < fewestDeath){
				fewestDeath = teamStatsList.get(i).getLeastDeaths();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamMostAssists(){
		TeamStats result = null;
		int mostAssists = 0;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getMostAssists() > mostAssists){
				mostAssists = teamStatsList.get(i).getMostAssists();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamWinLongestGame(){
		TeamStats result = null;
		double winLongestGame = 0;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getLongestGameWon() > winLongestGame){
				winLongestGame = teamStatsList.get(i).getLongestGameWon();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamWinShortestGame(){
		TeamStats result = null;
		double winShortestGame = 9999;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getShortestGameWon() < winShortestGame){
				winShortestGame = teamStatsList.get(i).getShortestGameWon();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamHigestGameLengthAvg(){
		TeamStats result = null;
		double highestGameLengthAvg = 0;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getAvgGameLength() > highestGameLengthAvg){
				highestGameLengthAvg = teamStatsList.get(i).getAvgGameLength();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamMostDiffHeroes(){
		TeamStats result = null;
		int mostDiffHeroes = 0;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getNumHeroesPicked() > mostDiffHeroes){
				mostDiffHeroes = teamStatsList.get(i).getNumHeroesPicked();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public TeamStats getTeamLeastDiffHeroes(){
		TeamStats result = null;
		int leastDiffHeroes = Integer.MAX_VALUE;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getNumHeroesPicked() < leastDiffHeroes){
				leastDiffHeroes = teamStatsList.get(i).getNumHeroesPicked();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	//Player
	public PlayerStats getPlayerHighestKillAvg(){
		PlayerStats result = null;
		double highestKillAvg = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getAvgKills() > highestKillAvg){
				highestKillAvg = playerStatsList.get(i).getAvgKills();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerMostKills(){
		PlayerStats result = null;
		int mostKills = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getMostKills() > mostKills){
				mostKills = playerStatsList.get(i).getMostKills();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerLowestDeathsAvg(){
		PlayerStats result = null;
		double lowestDeathAvg = Double.MAX_VALUE;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getAvgDeaths() < lowestDeathAvg){
				lowestDeathAvg = playerStatsList.get(i).getAvgDeaths();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerHighestAssistAvg(){
		PlayerStats result = null;
		double highestAssistAvg = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getAvgAssists() > highestAssistAvg){
				highestAssistAvg = playerStatsList.get(i).getAvgAssists();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerMostAssists(){
		PlayerStats result = null;
		int highestAssist = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getMostAssists() > highestAssist){
				highestAssist = playerStatsList.get(i).getMostAssists();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerHighestLHAvg(){
		PlayerStats result = null;
		double highestLHAvg = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getAvgLH() > highestLHAvg){
				highestLHAvg = playerStatsList.get(i).getAvgLH();
				result = playerStatsList.get(i);
			}
		}
		return result;	
	}
	
	public PlayerStats getPlayerMostLH(){
		PlayerStats result = null;
		int mostLH = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getMostLH() > mostLH){
				mostLH = playerStatsList.get(i).getMostLH();
				result = playerStatsList.get(i);
			}
		}
		return result;	
	}
	
	public PlayerStats getPlayerMostGPM(){
		PlayerStats result = null;
		int mostGPM = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getMostGPM() > mostGPM){
				mostGPM = playerStatsList.get(i).getMostGPM();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerMostGPMAvg(){
		PlayerStats result = null;
		double mostGPMAvg = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getAvgGPM() > mostGPMAvg){
				mostGPMAvg = playerStatsList.get(i).getAvgGPM();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	public PlayerStats getPlayerMostDiffHeroes(){
		PlayerStats result = null;
		int mostDiffHeroes = 0;
		for (int i = 0; i < playerStatsList.size(); i++){
			if (playerStatsList.get(i).getNumHeroesPlayed() > mostDiffHeroes){
				mostDiffHeroes = playerStatsList.get(i).getNumHeroesPlayed();
				result = playerStatsList.get(i);
			}
		}
		return result;
	}
	
	//Tournament
	public int getTotalGamesPlayed(){
		return numGames;
	}
	
	public int getNumHeroesPicked(){
		int numHeroesPicked = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getNumPicked() > 0)
				numHeroesPicked ++;
		}			
		return numHeroesPicked;
	}
	
	public int getNumHeroesBanned(){
		int numHeroesBanned = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getNumBanned() > 0)
				numHeroesBanned ++;
		}
		return numHeroesBanned;
	}
	
	public int getMostCombinedKills(){
		return mostCombinedKills;
	}
	

	public double getLongestGameDuration(){
		return getTeamWinLongestGame().getLongestGameWon();
	}
	
	public double getShortestGameDuration(){
		return getTeamWinShortestGame().getShortestGameWon();
	}
	
	//get hero with most kills, return most kills
	
	public HeroStats getHeroMostDeaths(){
		HeroStats result = null;
		int mostDeaths = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getMostDeaths() > mostDeaths){
				mostDeaths = heroStatsList[i].getMostDeaths();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroMostAssists(){
		HeroStats result = null;
		int mostAssist = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getMostAssists() > mostAssist){
				mostAssist = heroStatsList[i].getMostAssists();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
	public HeroStats getHeroHigestGPM(){
		HeroStats result = null;
		int mostGPM = 0;
		for (int i = 1; i < heroStatsList.length; i++){
			if (heroStatsList[i].getMostGPM() > mostGPM){
				mostGPM = heroStatsList[i].getMostGPM();
				result = heroStatsList[i];
			}
		}
		return result;
	}
	
}
