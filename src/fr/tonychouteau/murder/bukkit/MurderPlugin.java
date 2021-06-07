package fr.tonychouteau.murder.bukkit;

// My Package
import fr.tonychouteau.murder.bukkit.listener.PlayerListener;
import fr.tonychouteau.murder.bukkit.statistics.Statistics;
import fr.tonychouteau.murder.bukkit.commands.Commands;
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.util.MyRunnable;

// Java Import
import java.util.ArrayList;
import java.io.File;

// Bukkit Import
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Difficulty;
import org.bukkit.plugin.PluginManager;

import org.bukkit.event.Listener;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;

// Main Class
public class MurderPlugin extends JavaPlugin {

	private Statistics statistics = null;

	// Getters & Setters

	public Statistics getStatistics() {
		return this.statistics;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	// Entry Point

	@Override
	public void onEnable() {
		Listener l = new PlayerListener();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(l, this);

		getLogger().info("Le plugin s'est bien chargé.");
		Tool.setPlugin(this);

		Bukkit.getWorlds().get(0).setDifficulty(Difficulty.PEACEFUL);

		makePluginDirectory();

		handlePlayerOutsideWorld();

		Commands.loadSpawnpoints(null, null, null);
		statistics = new Statistics();
	}

	private void makePluginDirectory() {
		File file = new File("./plugins/MurderPlugin");
		file.mkdir();
	}

	private void handlePlayerOutsideWorld() {
		Tool.interval(1, -1, new MyRunnable() {
			@Override
			public void run() {
				ArrayList<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
				for (Player p: players) {
					int y = p.getLocation().getBlockY();
					if (y < 0) {
						p.teleport(p.getWorld().getSpawnLocation());
					}
				}
			}
		});
	}

	@Override
	public void onDisable() {
		getLogger().info("Le plugin s'est bien arrêté.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return Commands.handleCommand(sender, cmd, label, args);
	}
}