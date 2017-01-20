package compendium.stats;

import java.util.List;
import java.util.Map;

public class PrintData {

	static String PRINT_FORMAT_LONG = "%-20s%10d\n";
	static String PRINT_FORMAT_DOUBLE = "%-20s%10.2f\n";
	static String PRINT_FORMAT_LONG_MATCHID = "%-20s%10d%25s\n";
	static String PRINT_FORMAT_DOUBLE_MATCHID = "%-20s%10.2f%25s\n";

	public void printHeroKills(List<PlayerDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getHeroId(), e.getKills(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printHeroLH(List<PlayerDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getHeroId(), e.getLH(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}

	public void printTeamKills(List<TeamDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getTeamName(), e.getKills(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printStringDoubleMap(Map<String, Double> sortedMap, String title){
		System.out.println(title);
		sortedMap.entrySet().stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_DOUBLE, e.getKey(), e.getValue());
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printTeamAvgGameDuration(Map<String, Double> sortedMap, String title){
		System.out.println(title);
		sortedMap.entrySet().stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_DOUBLE, e.getKey(), (double)e.getValue()/60);
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printTeamGameDuration(List<TeamDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_DOUBLE_MATCHID, e.getTeamName(), (double)e.getGameDuration()/60, e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}

	public void printStringLongMap(Map<String, Long> sortedMap, String title) {
		System.out.println(title);
		sortedMap.entrySet().stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG, e.getKey(), e.getValue());
			});
		System.out.print(System.lineSeparator());
	}

	public void printPlayerKills(List<PlayerDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getPlayerId(), e.getKills(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printPlayerAssists(List<PlayerDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getPlayerId(), e.getAssists(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}

	public void printPlayerLH(List<PlayerDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getPlayerId(), e.getLH(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}

	public void printPlayerGPM(List<PlayerDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getPlayerId(), e.getGPM(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printSingleLine(String line, String title){
		System.out.println(title);
		System.out.println(line);
		System.out.print(System.lineSeparator());	
	}

	public void printTeamAssists(List<TeamDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getTeamName(), e.getAssists(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}
	
	public void printTeamDeaths(List<TeamDetail> sortedList, String title) {
		System.out.println(title);
		sortedList.stream()
			.forEachOrdered(e -> {
				System.out.printf(PRINT_FORMAT_LONG_MATCHID, e.getTeamName(), e.getDeaths(), e.getMatchId());
			});
		System.out.print(System.lineSeparator());
	}
}
