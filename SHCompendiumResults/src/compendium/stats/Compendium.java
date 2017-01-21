package compendium.stats;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;

public class Compendium {

	CollectData collector;
	ProcessData processor;
	PrintData printer;
	static Map<Integer, String> heroNames;
	
	public Compendium(Map<Integer, String> heroNames, int numResults, int minNumGames){
		this.collector = new CollectData(minNumGames);
		this.processor = new ProcessData(numResults);
		this.printer = new PrintData();
		Compendium.heroNames = Collections.unmodifiableMap(heroNames);
	}
		
	public void addMatches(JsonObject obj){
		this.collector.addMatches(obj);
	}
	
	private void printMostPicked(){
		Map<String, Long> map = collector.getHeroPicks();
		Map<String, Long> sortedMap = processor.processStringLongMap(map);
		printer.printStringLongMap(sortedMap, "Most Picked Heroes:");
	}
	
	private void printMostBanned(){
		Map<String, Long> map = collector.getHeroBans();
		Map<String, Long> sortedMap = processor.processStringLongMap(map);
		printer.printStringLongMap(sortedMap, "Most Banned Heroes:");
	}
	
	private void printHighestAvgKills(){
		Map<String, Double> map = collector.filterHeroes(collector.getHeroKills());
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average Kills:");
	}
	
	private void printHighestAvgAssists(){
		Map<String, Double> map = collector.filterHeroes(collector.getHeroAssists());
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average Assists:");
	}
	
	private void printLowestAvgDeaths(){
		Map<String, Double> map = collector.filterHeroes(collector.getHeroDeaths());
		Map<String, Double> sortedMap = processor.processReverseStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Lowest Average Deaths:");
	}
	
	private void printHighestAvgGPM(){
		Map<String, Double> map = collector.filterHeroes(collector.getHeroGPM());
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest GPM:");
	}
	
	private void printMostAvgLH(){
		Map<String, Double> map = collector.filterHeroes(collector.getHeroLH());
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Most Average Last Hits:");
	}
	
	private void printMostKills(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processKills(list);
		printer.printHeroKills(sortedList, "Most Kills in Game:");
	}
	
	private void printMostLH(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processLH(list);
		printer.printHeroLH(sortedList, "Most LH in Game:");
	}
	
	private void printHighestWinRate(){
		Map<String, Long> picks = collector.getHeroPicks();
		Map<String, Long> wins = collector.getHeroWins();
		Map<String, Double> winrate = collector.filterHeroes(processor.processHeroWinRate(picks, wins));
		Map<String, Double> sortedWinRate = processor.processStringDoubleMap(winrate);
		printer.printStringDoubleMap(sortedWinRate, "Winrate:");
	}

	private void printWinner(){
		printer.printSingleLine(collector.getWinner(), "Tournament Winner:");	
	}

	private void printTeamMostKills(){
		List<TeamDetail> list = collector.getTeamMostX();
		List<TeamDetail> sortedList = processor.processMostTeamKills(list);
		printer.printTeamKills(sortedList, "Most Kills:");
	}

	private void printTeamHighestAvgKills(){
		Map<String, Double> map = collector.getTeamKills();
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average Kills:");
	}
	
	private void printTeamMostAssists(){
		List<TeamDetail> list = collector.getTeamMostX();
		List<TeamDetail> sortedList = processor.processMostTeamAssists(list);
		printer.printTeamAssists(sortedList, "Most Assists:");
	}
	
	private void printTeamFewestDeaths(){
		List<TeamDetail> list = collector.getTeamMostX();
		List<TeamDetail> sortedList = processor.processFewestDeaths(list);
		printer.printTeamDeaths(sortedList, "Fewest Deaths:");
	}
	
	private void printTeamWinLongestGame(){
		List<TeamDetail> list = collector.getTeamWinGame();
		List<TeamDetail> sortedList = processor.processTeamWinLongestGame(list);
		printer.printTeamGameDuration(sortedList, "Longest Game Won:");
	}
	
	private void printTeamWinShortestGame(){
		List<TeamDetail> list = collector.getTeamWinGame();
		List<TeamDetail> sortedList = processor.processTeamWinShortestGame(list);
		printer.printTeamGameDuration(sortedList, "Shortest Game Won:");
	}
	
	private void printTeamHighestAvgGameLength(){
		Map<String, Double> map = collector.getTeamGameDuration();
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printTeamAvgGameDuration(sortedMap, "Highest Average Game Length:");
	}
	
	private void printTeamMostDiffHeroesPicked(){
		Map<String, Long> map = collector.getTeamNumDiffHeroesPicked();
		Map<String, Long> sortedMap =  processor.processStringLongMap(map);
		printer.printStringLongMap(sortedMap, "Most Different Heroes Picked");
	}
	
	private void printTeamLeastDiffHeroesPicked(){
		Map<String, Long> map = collector.getTeamNumDiffHeroesPicked();
		Map<String, Long> sortedMap =  processor.processReverseStringLongMap(map);
		printer.printStringLongMap(sortedMap, "Fewest Different Heroes Picked");
	}
	
	private void printPlayerHighestAvgKills(){
		Map<String, Double> map = collector.getPlayerKills();
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average Kills:");
	}
	
	private void printPlayerMostKills(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processKills(list);
		printer.printPlayerKills(sortedList, "Most Kills in Game:");
	}
	
	private void printPlayerLowestAvgDeaths(){
		Map<String, Double> map = collector.getPlayerDeaths();
		Map<String, Double> sortedMap = processor.processReverseStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Lowest Average Deaths:");
	}
	
	private void printPlayerHighestAvgAssists(){
		Map<String, Double> map = collector.getPlayerAssists();
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average Assists:");
	}
	
	private void printPlayerMostAssists(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processAssists(list);
		printer.printPlayerAssists(sortedList, "Most Assists in Game:");
	}
	
	private void printPlayerHighestAvgLH(){
		Map<String, Double> map = collector.getPlayerLH();
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average LH:");
	}

	private void printPlayerMostLH(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processLH(list);
		printer.printPlayerLH(sortedList, "Most LH in Game:");
	}
	
	private void printPlayerHighestAvgGPM(){
		Map<String, Double> map = collector.getPlayerGPM();
		Map<String, Double> sortedMap = processor.processStringDoubleMap(map);
		printer.printStringDoubleMap(sortedMap, "Highest Average GPM:");
	}

	private void printPlayerMostGPM(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processGPM(list);
		printer.printPlayerGPM(sortedList, "Most GPM in Game:");
	}
	
	private void printPlayerMostDiffHeroesPlayed(){
		Map<String, Long> map = collector.getPlayerNumDiffHeroesPlayed();
		Map<String, Long> sortedMap =  processor.processStringLongMap(map);
		printer.printStringLongMap(sortedMap, "Most Different Heroes Played");
	}
	
	private void printTournamentNumGamesPlayed(){
		printer.printSingleLine(String.valueOf(collector.getNumGamesPlayed()), "Number of Games Played:");	
	}
	
	private void printTournamentNumHeroesPicked(){
		printer.printSingleLine(String.valueOf(collector.getNumHeroesPicked()), "Number of Heroes Picked:");	
	}
	
	private void printTournamentNumHeroesBanned(){
		printer.printSingleLine(String.valueOf(collector.getNumHeroesBanned()), "Number of Heroes Banned:");	
	}
	
	private void printTournamentMostCombinedKills(){
		printer.printSingleLine(String.valueOf(collector.getMostCombinedKills()), "Most Combined Kills:");	
	}
	
	private void printTournamentLongestGameDuration(){
		List<TeamDetail> list = collector.getTeamWinGame();
		List<TeamDetail> sortedList = processor.processTeamWinLongestGame(list);
		printer.printSingleLine(String.valueOf(sortedList.get(0).getGameDuration()/60), "Longest Game Duration:");	
	}
	
	private void printTournamentShortestGameDuration(){
		List<TeamDetail> list = collector.getTeamWinGame();
		List<TeamDetail> sortedList = processor.processTeamWinShortestGame(list);
		printer.printSingleLine(String.valueOf(sortedList.get(0).getGameDuration()/60), "Shortest Game Duration:");	
	}
	
	private void printTournamentMostKills(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processKills(list);
		printer.printSingleLine(String.valueOf(sortedList.get(0).getKills()), "Most Kills:");
	}
	
	private void printTournamentMostDeaths(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processDeaths(list);
		printer.printSingleLine(String.valueOf(sortedList.get(0).getDeaths()), "Most Deaths:");
	}
	
	private void printTournamentMostAssists(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processAssists(list);
		printer.printSingleLine(String.valueOf(sortedList.get(0).getAssists()), "Most Assists:");
	}
	
	private void printTournamentMostGPM(){
		List<PlayerDetail> list = collector.getHeroMostX();
		List<PlayerDetail> sortedList = processor.processGPM(list);
		printer.printSingleLine(String.valueOf(sortedList.get(0).getGPM()), "Highest GPM:");
	}
	
	
	private void printHeroStats(){
		printMostPicked();
		printMostBanned();
		printHighestWinRate();
		printHighestAvgKills();
		printHighestAvgAssists();
		printLowestAvgDeaths();
		printMostAvgLH();
		printHighestAvgGPM();
		printMostKills();
		printMostLH();
	}
	
	private void printTeamStats(){
		printWinner();
		printTeamMostKills();
		printTeamHighestAvgKills();
		printTeamFewestDeaths();
		printTeamMostAssists();
		printTeamWinLongestGame();
		printTeamWinShortestGame();
		printTeamHighestAvgGameLength();
		printTeamMostDiffHeroesPicked();
		printTeamLeastDiffHeroesPicked();
	}
	
	private void printPlayerStats(){
		printPlayerHighestAvgKills();
		printPlayerMostKills();
		printPlayerLowestAvgDeaths();
		printPlayerHighestAvgAssists();
		printPlayerMostAssists();
		printPlayerHighestAvgLH();
		printPlayerMostLH();
		printPlayerHighestAvgGPM();
		printPlayerMostGPM();
		printPlayerMostDiffHeroesPlayed();
	}
	
	private void printTournamentStats(){
		printTournamentNumGamesPlayed();
		printTournamentNumHeroesPicked();
		printTournamentNumHeroesBanned();
		printTournamentMostCombinedKills();
		printTournamentLongestGameDuration();
		printTournamentShortestGameDuration();
		printTournamentMostKills();
		printTournamentMostDeaths();
		printTournamentMostAssists();
		printTournamentMostGPM();
	}
	
	public void printStats(){
		printHeroStats();
		printTeamStats();
		printPlayerStats();
		printTournamentStats();
	}
}
