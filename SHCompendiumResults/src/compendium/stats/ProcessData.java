package compendium.stats;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessData {
	int numResults;
	
	public ProcessData(int numResults){
		this.numResults = numResults;
	}

	public List<PlayerDetail> processKills(List<PlayerDetail> list) {
		return list.stream()
			.sorted((e1, e2) -> e2.getKills() - e1.getKills())
			.limit(numResults)
			.collect(Collectors.toList());
	}
	
	public List<PlayerDetail> processLH(List<PlayerDetail> list) {
		return list.stream()
			.sorted((e1, e2) -> e2.getLH() - e1.getLH())
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
				.sorted((e1, e2) -> e2.getKills() - e1.getKills())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public Map<String, Double> processStringDoubleMap(Map<String, Double> map) {
		Map<String, Double> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}
	
	public Map<String, Long> processStringLongMap(Map<String, Long> map) {
		Map<String, Long> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}

	public Map<String, Double> processReverseStringDoubleMap(
			Map<String, Double> map) {
		Map<String, Double> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}

	public List<TeamDetail> processTeamWinLongestGame(List<TeamDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e2.getGameDuration() - e1.getGameDuration())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<TeamDetail> processTeamWinShortestGame(List<TeamDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e1.getGameDuration() - e2.getGameDuration())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public Map<String, Long> processReverseStringLongMap(Map<String, Long> map) {
		Map<String, Long> orderedMap = new LinkedHashMap<>();  
		map.entrySet().stream()
			.sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
			.limit(numResults)
			.forEachOrdered(e -> orderedMap.put(e.getKey(), e.getValue()));
		return orderedMap;
	}

	public List<TeamDetail> processMostTeamAssists(List<TeamDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e2.getAssists() - e1.getAssists())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<PlayerDetail> processAssists(List<PlayerDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e2.getAssists() - e1.getAssists())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<PlayerDetail> processGPM(List<PlayerDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e2.getGPM() - e1.getGPM())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<PlayerDetail> processDeaths(List<PlayerDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e2.getDeaths() - e1.getDeaths())
				.limit(numResults)
				.collect(Collectors.toList());
	}

	public List<TeamDetail> processFewestDeaths(List<TeamDetail> list) {
		return list.stream()
				.sorted((e1, e2) -> e1.getDeaths() - e2.getDeaths())
				.limit(numResults)
				.collect(Collectors.toList());
	}
}
