package compendium.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

public class CollectData {
	private List<MatchDetail> matchDetails;
	private int minNumGames;
	public CollectData(int minNumGames){
		this.minNumGames = minNumGames;
		this.matchDetails = new ArrayList<>();
	}
	
	public void addMatches(JsonObject obj){
		this.matchDetails.add(new MatchDetail(obj));
	}
	
	public Map<String, Double> filterHeroes(Map<String, Double> map){
		Map<String, Long> numPicks = getHeroPicks();
		Map<String, Double> filteredHeroes = new HashMap<>();
		map.entrySet().stream()
			.forEach(e -> {
				if (numPicks.get(e.getKey()) >= minNumGames)
					filteredHeroes.put(e.getKey(), e.getValue());
			});
		return filteredHeroes;
	}
	
	
	public Map<String, Long> getHeroPicks(){
		return matchDetails.stream()
			.map(MatchDetail::getPicks)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
	
	public Map<String, Long> getHeroBans(){
		return matchDetails.stream()
			.map(MatchDetail::getBans)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
	
	public Map<String, Double> getHeroKills(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getHeroId, Collectors.averagingInt(PlayerDetail::getKills)));
	}
	
	public Map<String, Double> getHeroAssists(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getHeroId, Collectors.averagingInt(PlayerDetail::getAssists)));
	}
	
	public Map<String, Double> getHeroDeaths(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getHeroId, Collectors.averagingInt(PlayerDetail::getDeaths)));
	}
	
	public Map<String, Double> getHeroLH(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getHeroId, Collectors.averagingInt(PlayerDetail::getLH)));
	}
	
	public Map<String, Double> getHeroGPM(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getHeroId, Collectors.averagingInt(PlayerDetail::getGPM)));
	}
	
	public List<PlayerDetail> getHeroMostX(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}
	
	public Map<String, Long> getHeroWins(){
		return matchDetails.stream()
			.map(MatchDetail::getTeamDetails)
			.flatMap(Collection::stream)
			.filter(TeamDetail::getWin)
			.map(TeamDetail::getHeroesPicked)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	public List<TeamDetail> getTeamMostX(){
		return  matchDetails.stream()
			.map(MatchDetail::getTeamDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}

	public Map<String, Double> getTeamKills(){
		return matchDetails.stream()
			.map(MatchDetail::getTeamDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(TeamDetail::getTeamName, Collectors.averagingDouble(TeamDetail::getKills)));
	}
	
	public Map<String, Double> getTeamAssists(){
		return matchDetails.stream()
			.map(MatchDetail::getTeamDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(TeamDetail::getTeamName, Collectors.averagingDouble(TeamDetail::getAssists)));
	}

	public List<TeamDetail> getTeamWinGame() {
		return matchDetails.stream()
			.map(MatchDetail::getTeamDetails)
			.flatMap(Collection::stream)
			.filter(TeamDetail::getWin)
			.collect(Collectors.toList());
	}

	public Map<String, Double> getTeamGameDuration() {
		return matchDetails.stream()
				.map(MatchDetail::getTeamDetails)
				.flatMap(Collection::stream)
				.collect(Collectors.groupingBy(TeamDetail::getTeamName, Collectors.averagingDouble(TeamDetail::getGameDuration)));
	}

	public Map<String, Long> getTeamNumDiffHeroesPicked() {
		Map<String, Long> numDiffHeroesPicked = new HashMap<>();
		matchDetails.stream()
				.map(MatchDetail::getTeamDetails)
				.flatMap(Collection::stream)
				.collect(Collectors.collectingAndThen(
					Collectors.groupingBy(TeamDetail::getTeamName, 
					Collectors.mapping(TeamDetail::getHeroesPicked, Collectors.toList())), 
					result -> {
						result.entrySet().stream()
							.forEach(x-> {
								numDiffHeroesPicked.put(x.getKey(), 
									x.getValue().stream()
									.flatMap(Collection::stream)
									.distinct()
									.collect(Collectors.counting()));
							});
						return result;
					}));
		return numDiffHeroesPicked;
	}
	
	public String getWinner(){
		MatchDetail lastGame = matchDetails.get(0);
		return lastGame.getRadiantWin() ? lastGame.getRadiantTeam() : lastGame.getDireTeam();
	}
	
	public Map<String, Double> getPlayerKills(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getPlayerId, Collectors.averagingInt(PlayerDetail::getKills)));
	}

	public Map<String, Double> getPlayerAssists(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getPlayerId, Collectors.averagingInt(PlayerDetail::getAssists)));
	}
	
	public Map<String, Double> getPlayerLH(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getPlayerId, Collectors.averagingInt(PlayerDetail::getLH)));
	}
	
	public Map<String, Double> getPlayerGPM(){
		return matchDetails.stream()
			.map(MatchDetail::getPlayerDetails)
			.flatMap(Collection::stream)
			.collect(Collectors.groupingBy(PlayerDetail::getPlayerId, Collectors.averagingInt(PlayerDetail::getGPM)));
	}

	public Map<String, Double> getPlayerDeaths() {
		return matchDetails.stream()
				.map(MatchDetail::getPlayerDetails)
				.flatMap(Collection::stream)
				.collect(Collectors.groupingBy(PlayerDetail::getPlayerId, Collectors.averagingInt(PlayerDetail::getDeaths)));
	}
	
	public Map<String, Long> getPlayerNumDiffHeroesPlayed() {
		Map<String, Long> numDiffHeroesPlayed = new HashMap<>();
		matchDetails.stream()
				.map(MatchDetail::getPlayerDetails)
				.flatMap(Collection::stream)
				.collect(Collectors.collectingAndThen(
					Collectors.groupingBy(PlayerDetail::getPlayerId, 
					Collectors.mapping(PlayerDetail::getHeroId, Collectors.toList())), 
					result -> {
						result.entrySet().stream()
							.forEach(x-> {
								numDiffHeroesPlayed.put(x.getKey(), 
								x.getValue().stream()
									.distinct()
									.collect(Collectors.counting()));
							});
						return result;
					}));
		return numDiffHeroesPlayed;
	}
	
	public int getNumGamesPlayed(){
		return matchDetails.size();
	}
	
	public Long getNumHeroesPicked(){
		return matchDetails.stream()
			.map(MatchDetail::getPicks)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.counting());
	}
	
	public Long getNumHeroesBanned(){
		return matchDetails.stream()
				.map(MatchDetail::getBans)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.counting());
	}
	
	public int getMostCombinedKills(){
		OptionalInt combinedKills = matchDetails.stream()
			.map(MatchDetail::getTeamDetails)
			.mapToInt(m -> m.get(0).getKills() + m.get(1).getKills())
			.max();
		if (combinedKills.isPresent())
			return combinedKills.getAsInt();
		else 
			return 0;
	}
}
