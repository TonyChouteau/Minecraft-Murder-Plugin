package fr.tonychouteau.murder.bukkit;

// My Package
import fr.tonychouteau.murder.bukkit.listener.PlayerListener;
import fr.tonychouteau.murder.bukkit.commands.Commands;
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.util.MyRunnable;
import fr.tonychouteau.murder.bukkit.scoreboards.Stats;

// Java Import
import java.util.ArrayList;
import java.io.File;

// Bukkit Import
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;

// Main Class
public class MurderPlugin extends JavaPlugin {

	Stats stats = null;

	@Override
	public void onEnable() {

		Listener l = new PlayerListener();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(l, this);

		getLogger().info("Le plugin s'est bien chargé.");
		Tool.setPlugin(this);

		makePluginDirectory();

		handlePlayerOutsideWorld();

		stats = new Stats();
	}

	private void makePluginDirectory() {
		File file = new File("./plugins/MurderPlugin");
		file.mkdir();
	}

	private void handlePlayerOutsideWorld() {
		Tool.interval(2, -1, new MyRunnable() {
			@Override
			public void run() {
				ArrayList<Player> players = new ArrayList(Bukkit.getOnlinePlayers());
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