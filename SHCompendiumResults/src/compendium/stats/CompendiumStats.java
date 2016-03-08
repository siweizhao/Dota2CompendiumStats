package compendium.stats;

import java.util.ArrayList;

import com.google.gson.*;

public class CompendiumStats {
	
	HeroStats[] heroStatsList;
	ArrayList<PlayerStats> playerStatsList;
	ArrayList<TeamStats> teamStatsList;
	
	int numGames;
	ArrayList<String> heroesPicked;
	ArrayList<String> heroesBanned;
	
	int mostCombinedKills;
	String mostCombinedKills_MatchId;
	
	public CompendiumStats(String[] heroNameList){
		heroStatsList = new HeroStats[Dota2Const.NUM_HEROES];
		for (int i = 1; i < Dota2Const.NUM_HEROES; i ++){
			HeroStats hero = new HeroStats(i, heroNameList[i]);
			heroStatsList[i] = hero;
		}
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

	public void printTournamentStats_Top(int numMinimumGamesPlayed, int numResults){
		//Heroes
		ArrayList<HeroStats> topMostPickedHeroes = getTop_HeroMostPicked(numResults);
		ArrayList<HeroStats> topMostBannedHero = getTop_HeroMostBanned(numResults);
		ArrayList<HeroStats> topHighestWRHeroes = getTop_HeroHighestWR(numResults, numMinimumGamesPlayed);
		ArrayList<HeroStats> topHighestKillAvgHeroes = getTop_HeroHighestKillAvg(numResults, numMinimumGamesPlayed);
		ArrayList<HeroStats> topHighestAssistAvgHeroes = getTop_HeroHighestAssistAvg(numResults, numMinimumGamesPlayed);
		ArrayList<HeroStats> topLeastDeathAvgHeroes = getTop_HeroLeastDeathAvg(numResults, numMinimumGamesPlayed);
		ArrayList<HeroStats> topHighestLHAvgHeroes = getTop_HeroHighestLHAvg(numResults, numMinimumGamesPlayed);
		ArrayList<HeroStats> topHighestGPMAvgHeroes = getTop_HeroHighesGPMAvg(numResults, numMinimumGamesPlayed);
		ArrayList<HeroStats> topMostKillsHeroes = getTop_HeroMostKills(numResults);
		ArrayList<HeroStats> topMostLHHeroes = getTop_HeroMostLH(numResults);
		
		System.out.println("Hero Stats (minimum " + numMinimumGamesPlayed + " games played)" );
		System.out.printf("%-30s%-20s%10s%20s\n", "Category", "Hero", "Result", "Match Id");
		for (int i = 0; i < numResults; i ++){
			HeroStats mostPickedHero = topMostPickedHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d\n", "Most Picked:", mostPickedHero.getName(), mostPickedHero.getNumPicked());
			else 
				System.out.printf("%-30s%-20s%10d\n", "", mostPickedHero.getName(), mostPickedHero.getNumPicked());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats mostBannedHero = topMostBannedHero.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d\n", "Most Banned:", mostBannedHero.getName(), mostBannedHero.getNumBanned());
			else 
				System.out.printf("%-30s%-20s%10d\n", "", mostBannedHero.getName(), mostBannedHero.getNumBanned());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats highestWRHero = topHighestWRHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest WR:", highestWRHero.getName(), highestWRHero.getWinrate());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestWRHero.getName(), highestWRHero.getWinrate());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats highestKillAvgHero = topHighestKillAvgHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest Kill Avg:", highestKillAvgHero.getName(), highestKillAvgHero.getAvgKills());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestKillAvgHero.getName(), highestKillAvgHero.getAvgKills());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats highestAssistAvgHero = topHighestAssistAvgHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest Assist Avg:", highestAssistAvgHero.getName(), highestAssistAvgHero.getAvgAssists());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestAssistAvgHero.getName(), highestAssistAvgHero.getAvgAssists());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats leastDeathAvgHero = topLeastDeathAvgHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Least Death Avg:", leastDeathAvgHero.getName(), leastDeathAvgHero.getAvgDeaths());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", leastDeathAvgHero.getName(), leastDeathAvgHero.getAvgDeaths());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats highestLHAvgHero = topHighestLHAvgHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest LH Avg:", highestLHAvgHero.getName(), highestLHAvgHero.getAvgLH());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestLHAvgHero.getName(), highestLHAvgHero.getAvgLH());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats highestGPMAvgHero = topHighestGPMAvgHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest GPM Avg:", highestGPMAvgHero.getName(), highestGPMAvgHero.getAvgGPM());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestGPMAvgHero.getName(), highestGPMAvgHero.getAvgGPM());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats mostKillsHero = topMostKillsHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most Kills:", mostKillsHero.getName(), mostKillsHero.getMostKills(),mostKillsHero.getMostKills_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostKillsHero.getName(), mostKillsHero.getMostKills(),mostKillsHero.getMostKills_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			HeroStats mostLHHero = topMostLHHeroes.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most LH:", mostLHHero.getName(), mostLHHero.getMostLH(),mostLHHero.getMostLH_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostLHHero.getName(), mostLHHero.getMostLH(),mostLHHero.getMostLH_MatchId());
		}	
		
		//Players
		ArrayList<PlayerStats> topHighestKillAvgPlayers = getTop_PlayerHighestKillAvg(numResults);
		ArrayList<PlayerStats> topMostKillsPlayers = getTop_PlayerMostKills(numResults);
		ArrayList<PlayerStats> topLowestDeathsAvgPlayers = getTop_PlayerLowestDeathsAvg(numResults);
		ArrayList<PlayerStats> topHighestAssistsAvgPlayers = getTop_PlayerHighestAssistsAvg(numResults);
		ArrayList<PlayerStats> topMostAssistsPlayers = getTop_PlayerMostAssists(numResults);
		ArrayList<PlayerStats> topHighestLHAvgPlayers = getTop_PlayerHighestLHAvg(numResults);
		ArrayList<PlayerStats> topMostLHPlayers = getTop_PlayerMostLH(numResults);
		ArrayList<PlayerStats> topMostGPMAvgPlayers = getTop_PlayerMostGPMAvg(numResults);
		ArrayList<PlayerStats> topMostGPMPlayers = getTop_PlayerMostGPM(numResults);
		ArrayList<PlayerStats> topMostDiffHeroesPlayers = getTop_PlayerMostDiffHeroes(numResults);
		
		System.out.println();
		System.out.println("Player Stats");
		System.out.printf("%-30s%-20s%10s%20s\n", "Category", "Player", "Result", "Match Id");
		for (int i = 0; i < numResults; i ++){
			PlayerStats highestKillAvgPlayer = topHighestKillAvgPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest Kill Avg:", highestKillAvgPlayer.getId(), highestKillAvgPlayer.getAvgKills());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestKillAvgPlayer.getId(), highestKillAvgPlayer.getAvgKills());
		}	
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats mostKillsPlayer = topMostKillsPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most Kills:", mostKillsPlayer.getId(), mostKillsPlayer.getMostKills(), mostKillsPlayer.getMostKills_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostKillsPlayer.getId(), mostKillsPlayer.getMostKills(),mostKillsPlayer.getMostKills_MatchId());
		}	
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats lowestDeathsAvgPlayer = topLowestDeathsAvgPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Lowest Deaths:", lowestDeathsAvgPlayer.getId(), lowestDeathsAvgPlayer.getAvgDeaths());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", lowestDeathsAvgPlayer.getId(), lowestDeathsAvgPlayer.getAvgDeaths());
		}	
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats highestAssistsAvgPlayer = topHighestAssistsAvgPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest Assists Avg:", highestAssistsAvgPlayer.getId(), highestAssistsAvgPlayer.getAvgAssists());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestAssistsAvgPlayer.getId(), highestAssistsAvgPlayer.getAvgAssists());
		}	
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats mostAssistsPlayer = topMostAssistsPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Highest Assists:", mostAssistsPlayer.getId(), mostAssistsPlayer.getMostAssists(), mostAssistsPlayer.getMostAssists_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostAssistsPlayer.getId(), mostAssistsPlayer.getMostAssists(), mostAssistsPlayer.getMostAssists_MatchId());
		}	
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats highestLHAvgPlayer = topHighestLHAvgPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest LH Avg:", highestLHAvgPlayer.getId(), highestLHAvgPlayer.getAvgLH());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestLHAvgPlayer.getId(), highestLHAvgPlayer.getAvgLH());
		}	
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats mostLHPlayer = topMostLHPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most LH:", mostLHPlayer.getId(), mostLHPlayer.getMostLH(), mostLHPlayer.getMostLH_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostLHPlayer.getId(), mostLHPlayer.getMostLH(), mostLHPlayer.getMostLH_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats mostGPMAvgPlayer = topMostGPMAvgPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Most GPM Avg:", mostGPMAvgPlayer.getId(), mostGPMAvgPlayer.getAvgGPM());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", mostGPMAvgPlayer.getId(), mostGPMAvgPlayer.getAvgGPM());
		}
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats mostGPMPlayer = topMostGPMPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most GPM:", mostGPMPlayer.getId(), mostGPMPlayer.getMostGPM(), mostGPMPlayer.getMostGPM_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostGPMPlayer.getId(), mostGPMPlayer.getMostGPM(), mostGPMPlayer.getMostGPM_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			PlayerStats mostDiffHeroesPlayer = topMostDiffHeroesPlayers.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d\n", "Most Diff Heroes:", mostDiffHeroesPlayer.getId(), mostDiffHeroesPlayer.getNumHeroesPlayed());
			else 
				System.out.printf("%-30s%-20s%10d\n", "", mostDiffHeroesPlayer.getId(), mostDiffHeroesPlayer.getNumHeroesPlayed());
		}
		
		//Teams
		ArrayList<TeamStats> topMostKillsTeams = getTop_TeamMostKills(numResults);
		ArrayList<TeamStats> topHighestKillAvgTeams = getTop_TeamHighestKillAvg(numResults);
		ArrayList<TeamStats> topFewestDeathsTeams = getTop_TeamFewestDeaths(numResults);
		ArrayList<TeamStats> topMostAssistsTeams = getTop_TeamMostAssists(numResults);
		ArrayList<TeamStats> topWinLongestGameTeams = getTop_TeamWinLongestGame(numResults);
		ArrayList<TeamStats> topWinShortestGameTeams = getTop_TeamWinShortestGame(numResults);
		ArrayList<TeamStats> topHighestGameLengthAvgTeams = getTop_TeamHighestGameLengthAvg(numResults);
		ArrayList<TeamStats> topMostDiffHeroesTeams = getTop_TeamMostDiffHeroes(numResults);
		ArrayList<TeamStats> topLeastDiffHeroesTeams = getTop_TeamLeastDiffHeroes(numResults);
			
		System.out.println();
		System.out.println("Team Stats");
		System.out.printf("%-30s%-20s%10s%20s\n", "Category", "Team", "Result", "Match Id");
		for (int i = 0; i < numResults; i ++){
			TeamStats mostKillsTeam = topMostKillsTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most Kills:", mostKillsTeam.getName(), mostKillsTeam.getMostKills(),mostKillsTeam.getMostKills_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostKillsTeam.getName(), mostKillsTeam.getMostKills(),mostKillsTeam.getMostKills_MatchId());
		}	
		
		for (int i = 0; i < numResults; i ++){
			TeamStats highestKillAvgTeam = topHighestKillAvgTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest Kill Avg:", highestKillAvgTeam.getName(), highestKillAvgTeam.getAvgKills());
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestKillAvgTeam.getName(), highestKillAvgTeam.getAvgKills());
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats fewestDeathsTeam = topFewestDeathsTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Fewest Deaths:", fewestDeathsTeam.getName(), fewestDeathsTeam.getLeastDeaths(), fewestDeathsTeam.getLeastDeaths_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", fewestDeathsTeam.getName(), fewestDeathsTeam.getLeastDeaths(), fewestDeathsTeam.getLeastDeaths_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats mostAssistsTeam = topMostAssistsTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d%20s\n", "Most Assists:", mostAssistsTeam.getName(), mostAssistsTeam.getMostAssists(), mostAssistsTeam.getMostAssists_MatchId());
			else 
				System.out.printf("%-30s%-20s%10d%20s\n", "", mostAssistsTeam.getName(), mostAssistsTeam.getMostAssists(), mostAssistsTeam.getMostAssists_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats winLongestGameTeam = topWinLongestGameTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f%20s\n", "Win Longest Game:", winLongestGameTeam.getName(), winLongestGameTeam.getLongestGameWon()/60, winLongestGameTeam.getLongestGameWon_MatchId());
			else 
				System.out.printf("%-30s%-20s%10.2f%20s\n", "", winLongestGameTeam.getName(), winLongestGameTeam.getLongestGameWon()/60, winLongestGameTeam.getLongestGameWon_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats winShortestGameTeam = topWinShortestGameTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f%20s\n", "Win Shortest Game:", winShortestGameTeam.getName(), winShortestGameTeam.getShortestGameWon()/60, winShortestGameTeam.getShortestGameWon_MatchId());
			else 
				System.out.printf("%-30s%-20s%10.2f%20s\n", "", winShortestGameTeam.getName(), winShortestGameTeam.getShortestGameWon()/60, winShortestGameTeam.getShortestGameWon_MatchId());
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats highestGameLengthAvg = topHighestGameLengthAvgTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10.2f\n", "Highest Game Length Avg", highestGameLengthAvg.getName(), highestGameLengthAvg.getAvgGameLength()/60);
			else 
				System.out.printf("%-30s%-20s%10.2f\n", "", highestGameLengthAvg.getName(), highestGameLengthAvg.getAvgGameLength()/60);
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats mostDiffHeroesTeam = topMostDiffHeroesTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d\n", "Most Diff Heroes:", mostDiffHeroesTeam.getName(), mostDiffHeroesTeam.getNumHeroesPicked());
			else 
				System.out.printf("%-30s%-20s%10d\n", "", mostDiffHeroesTeam.getName(), mostDiffHeroesTeam.getNumHeroesPicked());
		}
		
		for (int i = 0; i < numResults; i ++){
			TeamStats leastDiffHeroesTeam = topLeastDiffHeroesTeams.get(i);
			if (i == 0)
				System.out.printf("%-30s%-20s%10d\n", "Least Diff Heroes:", leastDiffHeroesTeam.getName(), leastDiffHeroesTeam.getNumHeroesPicked());
			else 
				System.out.printf("%-30s%-20s%10d\n", "", leastDiffHeroesTeam.getName(), leastDiffHeroesTeam.getNumHeroesPicked());
		}
		
		//Tournament
		
		int totalGamesPlayed = getTotalGamesPlayed();
		int totalHeroesPicked = getNumHeroesPicked();
		int totalHeroesBanned = getNumHeroesBanned();
		int mostCombinedKills = getMostCombinedKills();
		double longestGameDuration = getLongestGameDuration();
		double shortestGameDuration = getShortestGameDuration();
		TeamStats winLongestGameTeam = topWinLongestGameTeams.get(0);
		TeamStats winShortestGameTeam = topWinShortestGameTeams.get(0);
		HeroStats mostKillsHero = topMostKillsHeroes.get(0);
		HeroStats mostDeathsHero = getHeroMostDeaths();
		HeroStats mostAssistsHero = getHeroMostAssists();
		HeroStats mostGPMHero = getHeroHighestGPM();
		
		System.out.println();
		System.out.println("Tournament Stats");
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
	public ArrayList<HeroStats> getTop_HeroMostPicked(int n){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			int mostPicked = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getNumPicked() >= mostPicked){
					mostPicked = heroStatsList[j].getNumPicked();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
	
	public ArrayList<HeroStats> getTop_HeroMostBanned(int n){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			int mostBanned = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getNumBanned() >= mostBanned){
					mostBanned = heroStatsList[j].getNumBanned();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
	
	
	
	public ArrayList<HeroStats> getTop_HeroHighestWR(int n, int numMinimumGamesPlayed){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			double highestWR = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getWinrate() >= highestWR && heroStatsList[j].getNumPicked() >= numMinimumGamesPlayed){
					highestWR = heroStatsList[j].getWinrate();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}

	public ArrayList<HeroStats> getTop_HeroHighestKillAvg(int n, int numMinimumGamesPlayed){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			double highestKillAvg = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getAvgKills() >= highestKillAvg && heroStatsList[j].getNumPicked() >= numMinimumGamesPlayed){
					highestKillAvg = heroStatsList[j].getAvgKills();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
	
	public ArrayList<HeroStats> getTop_HeroHighestAssistAvg(int n, int numMinimumGamesPlayed){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			double highestAssistAvg = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getAvgAssists() >= highestAssistAvg && heroStatsList[j].getNumPicked() >= numMinimumGamesPlayed){
					highestAssistAvg = heroStatsList[j].getAvgAssists();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
	
	public ArrayList<HeroStats> getTop_HeroLeastDeathAvg(int n, int numMinimumGamesPlayed){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			double lowestDeathAvg = Integer.MAX_VALUE;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getAvgDeaths() <= lowestDeathAvg && heroStatsList[j].getNumPicked() >= numMinimumGamesPlayed){
					lowestDeathAvg = heroStatsList[j].getAvgDeaths();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}

	public ArrayList<HeroStats> getTop_HeroHighestLHAvg(int n, int numMinimumGamesPlayed){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			double highestLHAvg = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getAvgLH() >= highestLHAvg && heroStatsList[j].getNumPicked() >= numMinimumGamesPlayed){
					highestLHAvg = heroStatsList[j].getAvgLH();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
		
	public ArrayList<HeroStats> getTop_HeroHighesGPMAvg(int n, int numMinimumGamesPlayed){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			double highestGPMAvg = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getAvgGPM() >= highestGPMAvg && heroStatsList[j].getNumPicked() >= numMinimumGamesPlayed){
					highestGPMAvg = heroStatsList[j].getAvgGPM();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}

	public ArrayList<HeroStats> getTop_HeroMostKills(int n){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			int mostKills = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getMostKills() >= mostKills){
					mostKills = heroStatsList[j].getMostKills();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
	
	public ArrayList<HeroStats> getTop_HeroMostLH(int n){
		ArrayList<HeroStats> result = new ArrayList<HeroStats>();
		HeroStats hero = null;
		for (int i = 0; i < 5; i++){
			int mostLH = 0;
			for (int j = 1; j < heroStatsList.length; j++){
				if (!result.contains(heroStatsList[j]) && heroStatsList[j].getMostLH() >= mostLH){
					mostLH = heroStatsList[j].getMostLH();
					hero = heroStatsList[j];
				}
			}
			if (hero != null)
				result.add(hero);
		}
		return result;
	}
	
	
	//Player
	public ArrayList<PlayerStats> getTop_PlayerHighestKillAvg(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			double highestKillAvg = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getAvgKills() >= highestKillAvg){
					highestKillAvg = playerStatsList.get(j).getAvgKills();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}
	
	public ArrayList<PlayerStats> getTop_PlayerMostKills(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			int mostKills = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getMostKills() >= mostKills){
					mostKills = playerStatsList.get(j).getMostKills();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}
	
	public ArrayList<PlayerStats> getTop_PlayerLowestDeathsAvg(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			double lowestDeathAvg = Double.MAX_VALUE;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getAvgDeaths() <= lowestDeathAvg){
					lowestDeathAvg = playerStatsList.get(j).getAvgDeaths();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}
	
	public ArrayList<PlayerStats> getTop_PlayerHighestAssistsAvg(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			double highestAssistAvg = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getAvgAssists() >= highestAssistAvg){
					highestAssistAvg = playerStatsList.get(j).getAvgAssists();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}

	public ArrayList<PlayerStats> getTop_PlayerMostAssists(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			int highestAssist = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getMostAssists() >= highestAssist){
					highestAssist = playerStatsList.get(j).getMostAssists();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}

	public ArrayList<PlayerStats> getTop_PlayerHighestLHAvg(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			double highestLHAvg = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getAvgLH() >= highestLHAvg){
					highestLHAvg = playerStatsList.get(j).getAvgLH();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}

	public ArrayList<PlayerStats> getTop_PlayerMostLH(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			int mostLH = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getMostLH() >= mostLH){
					mostLH = playerStatsList.get(j).getMostLH();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}

	public ArrayList<PlayerStats> getTop_PlayerMostGPM(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			int mostGPM = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getMostGPM() >= mostGPM){
					mostGPM = playerStatsList.get(j).getMostGPM();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}

	public ArrayList<PlayerStats> getTop_PlayerMostGPMAvg(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			double mostGPMAvg = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getAvgGPM() >= mostGPMAvg){
					mostGPMAvg = playerStatsList.get(j).getAvgGPM();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}
	
	public ArrayList<PlayerStats> getTop_PlayerMostDiffHeroes(int n){
		ArrayList<PlayerStats> result = new ArrayList<PlayerStats>();
		PlayerStats player = null;
		for (int i = 0; i < 5; i++){
			int mostDiffHeroes = 0;
			for (int j = 1; j < playerStatsList.size(); j++){
				if (!result.contains(playerStatsList.get(j)) && playerStatsList.get(j).getNumHeroesPlayed() >= mostDiffHeroes){
					mostDiffHeroes = playerStatsList.get(j).getNumHeroesPlayed();
					player = playerStatsList.get(j);
				}
			}
			if (player != null)
				result.add(player);
		}
		return result;
	}
	
	//Team
	public ArrayList<TeamStats> getTop_TeamMostKills(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			int mostKills = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getMostKills() >= mostKills){
					mostKills = teamStatsList.get(j).getMostKills();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}
		
	public ArrayList<TeamStats> getTop_TeamHighestKillsAvg(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			double highestKillAvg = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getAvgKills() >= highestKillAvg){
					highestKillAvg = teamStatsList.get(j).getAvgKills();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}
		
	public ArrayList<TeamStats> getTop_TeamHighestKillAvg(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			double highestKillAvg = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getAvgKills() >= highestKillAvg){
					highestKillAvg = teamStatsList.get(j).getAvgKills();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}
		
	public ArrayList<TeamStats> getTop_TeamFewestDeaths(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			int fewestDeath = Integer.MAX_VALUE;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getLeastDeaths() <= fewestDeath){
					fewestDeath = teamStatsList.get(j).getLeastDeaths();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}

	public ArrayList<TeamStats> getTop_TeamMostAssists(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			int mostAssists = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getMostAssists() >= mostAssists){
					mostAssists = teamStatsList.get(j).getMostAssists();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
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
	
	public ArrayList<TeamStats> getTop_TeamWinLongestGame(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			double winLongestGame = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getLongestGameWon() >= winLongestGame){
					winLongestGame = teamStatsList.get(j).getLongestGameWon();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}
	
	public TeamStats getTeamWinShortestGame(){
		TeamStats result = null;
		double winShortestGame = Double.MAX_VALUE;
		for (int i = 0; i < teamStatsList.size(); i++){
			if (teamStatsList.get(i).getShortestGameWon() < winShortestGame){
				winShortestGame = teamStatsList.get(i).getShortestGameWon();
				result = teamStatsList.get(i);
			}
		}
		return result;
	}
	
	public ArrayList<TeamStats> getTop_TeamWinShortestGame(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			double winShortestGame = Double.MAX_VALUE;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getShortestGameWon() <= winShortestGame){
					winShortestGame = teamStatsList.get(j).getShortestGameWon();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}

	public ArrayList<TeamStats> getTop_TeamHighestGameLengthAvg(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			double highestGameLengthAvg = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getAvgGameLength() >= highestGameLengthAvg){
					highestGameLengthAvg = teamStatsList.get(j).getAvgGameLength();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}

	public ArrayList<TeamStats> getTop_TeamMostDiffHeroes(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			int mostDiffHeroes = 0;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getNumHeroesPicked() >= mostDiffHeroes){
					mostDiffHeroes = teamStatsList.get(j).getNumHeroesPicked();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
		}
		return result;
	}

	public ArrayList<TeamStats> getTop_TeamLeastDiffHeroes(int n){
		ArrayList<TeamStats> result = new ArrayList<TeamStats>();
		TeamStats team = null;
		for (int i = 0; i < 5; i++){
			int leastDiffHeroes = Integer.MAX_VALUE;
			for (int j = 0; j < teamStatsList.size(); j++){
				if (!result.contains(teamStatsList.get(j)) && teamStatsList.get(j).getNumHeroesPicked() <= leastDiffHeroes){
					leastDiffHeroes = teamStatsList.get(j).getNumHeroesPicked();
					team = teamStatsList.get(j);
				}
			}
			if (team != null)
				result.add(team);
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
	
	public HeroStats getHeroHighestGPM(){
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
