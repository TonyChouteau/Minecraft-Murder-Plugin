package fr.tonychouteau.murder.bukkit;

import fr.tonychouteau.murder.bukkit.listener.PlayerListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;

public class MurderPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		// Actions à effectuer au démarrage du plugin, c'est-à-dire :
		// - Au démarrage du serveur
		// - Après un /reload

		Listener l = new PlayerListener();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(l, this);

		getLogger().info("Le plugin s'est bien chargé.");
	}

	@Override
	public void onDisable() {
		// Actions à effectuer à la désactivation du plugin
		// - A l'extinction du serveur
		// - Pendant un /reload

		getLogger().info("Le plugin s'est bien arrêté.");
	}
}