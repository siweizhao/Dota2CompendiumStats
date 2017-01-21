package compendium.stats;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ProcessData {
	int numResults;
	
	public ProcessData(int numResults){
		this.numResults = numResults;
	}

	public List<PlayerDetail> processKills(List<PlayerDetail> list) {
		return list.stream()
			.sorted(Comparator.comparing(PlayerDetail::getKills).reversed())
			.limit(numResults)
			.collect(Collectors.toList());
	}
	
	public List<PlayerDetail> processLH(List<PlayerDetail> list) {
		return list.stream()
			.sorted(Comparator.comparing(PlayerDetail::getLH).reversed())
			.limit(numResults)
			.collect(Collectors.toList());
	}

	public Map<String, Double> processHeroWinRate(Map<String, Long> picks,
			Map<String, Long> wins) {
		Map<String, Double> winrate = new HashMap<>();
		wins.entrySet().stream()
			.forEach(e -> winrate.put(e.getKey(), (double) e.getValue()/picks.get(e.getKey())));
		return winrate;
	}

	public List<TeamDetail> processMostTeamKills(List<TeamDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(TeamDetail::getKills).reversed())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public Map<String, Double> processStringDoubleMap(Map<String, Double> map) {
		Map<String, Double> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}
	
	public Map<String, Long> processStringLongMap(Map<String, Long> map) {
		Map<String, Long> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}

	public Map<String, Double> processReverseStringDoubleMap(
			Map<String, Double> map) {
		Map<String, Double> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted(Entry.comparingByValue())
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}

	public List<TeamDetail> processTeamWinLongestGame(List<TeamDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(TeamDetail::getGameDuration).reversed())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<TeamDetail> processTeamWinShortestGame(List<TeamDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(TeamDetail::getGameDuration))
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public Map<String, Long> processReverseStringLongMap(Map<String, Long> map) {
		Map<String, Long> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted(Comparator.comparing(Entry::getValue))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}

	public List<TeamDetail> processMostTeamAssists(List<TeamDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(TeamDetail::getAssists).reversed())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<PlayerDetail> processAssists(List<PlayerDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(PlayerDetail::getAssists).reversed())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<PlayerDetail> processGPM(List<PlayerDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(PlayerDetail::getGPM).reversed())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<PlayerDetail> processDeaths(List<PlayerDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(PlayerDetail::getDeaths).reversed())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<TeamDetail> processFewestDeaths(List<TeamDetail> list) {
		return list.stream()
				.sorted(Comparator.comparing(TeamDetail::getDeaths))
				.limit(numResults)
				.collect(Collectors.toList());
	}
}
