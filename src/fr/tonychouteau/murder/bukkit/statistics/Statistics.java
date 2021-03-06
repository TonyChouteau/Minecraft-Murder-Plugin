package fr.tonychouteau.murder.bukkit.statistics;

// Own import
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.game.Game;

// Java Import
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
// Bukkit Import
import org.bukkit.entity.Player;

public class Statistics {

	private int gameCount = 0;
	private Map<String, PlayerStatistics> playerStats = new HashMap<String, PlayerStatistics>(0); // Pseudo -> Stats

	// Getters
	public int getGameCount() {
		return this.gameCount;
	}

	public PlayerStatistics getPlayerStats(Player p) {
		return playerStats.get(p.getName());
	}

	// Setters
	public void incrementGameCount() {
		this.gameCount++;
		this.savePluginStats();
	}

	public void addPlayer(String[] data) {
		playerStats.put(data[0], new PlayerStatistics(data));
	}

	public void addPlayerPoints(Player p, int points) {
		PlayerStatistics stats = playerStats.get(p.getName());
		stats.addPoints(points);
	}

	public void addPlayer(Player p) {
		String name = p.getName();
		if (!playerStats.containsKey(name)) {
			playerStats.put(name, new PlayerStatistics());
			savePlayersStats();
		}
	}

	public void endGame(ArrayList<Player> players, Player murderer, Player guardian, int winner) {
		for (Player p : players) {
			PlayerStatistics stats = playerStats.get(p.getName());

			stats.gameCount++;
			if (p == murderer) {
				stats.gameCountAsMurderer++;
				if (winner == Game.MURDERER_WINS) {
					stats.winAsMurderer++;
				}
			} else if (p == guardian) {
				if (winner == Game.INNOCENT_WINS) {
					stats.winAsGuardian++;
				}
			} else {
				if (winner == Game.INNOCENT_WINS) {
					stats.winAsInnocent++;
				}
			}
		}
		this.savePlayersStats();
	}

	// Constructor & Methods

	public Statistics() {
		loadPluginStats();
		loadPlayersStats();
	}

	private void loadPluginStats() {
		String content = Tool.loadString("plugin.stats");

		if (content == null || content.equals("")) {
			savePluginStats();
			return;
		}

		gameCount = Integer.parseInt(content);
	}

	private boolean savePluginStats() {
		return Tool.saveString("plugin.stats", "" + gameCount);
	}

	private void loadPlayersStats() {
		String content = Tool.loadString("players.stats");

		if (content == null || content.equals("")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				addPlayer(p);
			}
			return;
		}

		String[] playerDatas = content.split("-");
		for (String playerData : playerDatas) {
			String[] datas = playerData.split(":");
			addPlayer(datas);
		}
	}

	private boolean savePlayersStats() {

		String saveString = "";
		for (String name : playerStats.keySet()) {
			PlayerStatistics stats = playerStats.get(name);

			saveString += name + ":" + stats.getStringSave() + "-";
		}

		return Tool.saveString("players.stats", saveString);
	}

	public void clearCurrentPoints() {
		for (String name : playerStats.keySet()) {
			playerStats.get(name).clearPoints();
		}
		savePlayersStats();
	}

	public String getScoreboard() {

		Map<String, Integer> currentPoints = new HashMap<String, Integer>(0);
		Map<String, Integer> totalPoints = new HashMap<String, Integer>(0);
		for (String name : playerStats.keySet()) {
			currentPoints.put(name, playerStats.get(name).currentPoints);
			totalPoints.put(name, playerStats.get(name).totalPoints);
		}

		ArrayList<Entry<String, Integer>> sortedCurrentScore = Tool.sortByValue(currentPoints);
		ArrayList<Entry<String, Integer>> sortedTotalScore = Tool.sortByValue(totalPoints);

		String currentScoreboard = "\n=================================\n \n - Current Points -\n ";
		String totalScoreboard = "\n \n - Total Points -\n ";
		for (int i = 0; i < Math.min(5, sortedCurrentScore.size()); i++) {
			int index = i+1;
			Entry<String, Integer> entry = sortedCurrentScore.get(i);
			currentScoreboard += "\n" + index + Tool.order(index) + " - " + entry.getKey() + " : " + entry.getValue() + "pt"
					+ (entry.getValue() > 1 ? "s" : "");
			entry = sortedTotalScore.get(i);
			totalScoreboard += "\n" + index + Tool.order(index) + " - " + entry.getKey() + " : " + entry.getValue() + "pt"
					+ (entry.getValue() > 1 ? "s" : "");
		}
		totalScoreboard += "\n \n=================================";

		return currentScoreboard + totalScoreboard;
	}
}
