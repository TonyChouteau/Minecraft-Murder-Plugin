package fr.tonychouteau.murder.bukkit.statistics;

// Own import
import fr.tonychouteau.murder.bukkit.util.Tool;

// Java Import

// Bukkit Import

public class Statistics {

	private int gameCount = 0;

	// Getters
	public int getGameCount() {
		return this.gameCount;
	}

	// Setters
	public void incrementGameCount() {
		this.gameCount ++;
		this.savePluginStats();
	}

	// Constructor & Methods

	public Statistics() {
		loadPluginStats();
	}

	private void loadPluginStats() {
		String content = Tool.loadString("plugin.stats");
		if (content == null) {
			boolean result = savePluginStats();
			if (!result) {
				return;
			}
			loadPluginStats();
			return;
		}
		gameCount = Integer.parseInt(content);
	}

	private boolean savePluginStats() {
		return Tool.saveString("plugin.stats", "" + gameCount);
	}
}
