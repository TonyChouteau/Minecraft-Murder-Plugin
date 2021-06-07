package fr.tonychouteau.murder.bukkit.commands;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.util.MyRunnable;
import fr.tonychouteau.murder.bukkit.MurderPlugin;
import fr.tonychouteau.murder.bukkit.game.Game;
import fr.tonychouteau.murder.bukkit.statistics.PlayerStatistics;

// Java Import
import java.util.ArrayList;
import java.util.Map;

// Bukkit Import
import org.bukkit.ChatColor;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

//Commands Class
public class Commands {

	public static boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if ((cmd.getName().equalsIgnoreCase("murder") || cmd.getName().equalsIgnoreCase("m")) && args.length >= 1) {

			switch (args[0]) {
				case "help":
					help(sender, cmd, label);
					return true;
				case "start":
					return startGame(sender, cmd, label);
				case "stop":
					return stopGame(sender, cmd, label);
				case "spawnpoint":
				case "sp":
					return setSpawnpoint(sender, cmd, label, args);
				case "addSpawnpointHere":
				case "add":
					return addSpawnpointOnPlayer(sender, cmd, label);
				case "saveSpawnpoints":
				case "save":
					return saveSpawnpoints(sender, cmd, label);
				case "loadSpawnpoints":
				case "load":
					return loadSpawnpoints(sender, cmd, label);
				case "plugin":
				case "getPluginInfo":
					return getStats(sender, cmd, label);
				case "stats":
				case "getOwnStats":
					return getPlayerStats(sender, cmd, label);
				case "clear":
				case "clearPoints":
					return clearPoints(sender, cmd, label);
			}
		}

		return false;
	}

	public static void help(CommandSender sender, Command cmd, String label) {
		try {
			Player player = (Player) sender;
			Tool.pp(ChatColor.GREEN + "/m start <nombre_de_joueurs>\n" + "   Lancer une partie\n" + "/m stop\n"
					+ "   Arrete la partie en cours\n" + "/m spawnpoint | sp\n"
					+ "   Ajoute un spawnpoint. /m spawnpoint|sp <id> <x> <y> <z>\n" + "/m addSpawnpointHere | add\n"
					+ "   Ajoute un spawnpoint a l'endroit ou se trouve le joueur\n" + "/m saveSpawnpoints | save\n"
					+ "   Save spawnpoints in a file\n" + "/m loadSpawnpoints | load\n"
					+ "   Load spawnpoints from the save file\n", player);
		} catch (Exception e) {
			Tool.pc(ChatColor.GREEN
					+ "/start <nombre_de_joueurs> \n    Lancer une partie\n/stop\n    Arrete la partie en cours\n/spawnpoint\n   Ajoute un spawnpoint. /spawnpoint <id> <x> <y> <z>");
		}
	}

	public static boolean startGame(CommandSender sender, Command cmd, String label) {

		Game game = Game.getGame();
		if (game != null) {
			Tool.pc(ChatColor.RED + "Error: A game is already started");
			Tool.pp(ChatColor.RED + "Error: A game is already started");
			return true;
		}

		game = new Game();

		if (game.notEnoughPlayer()) {
			Tool.pc(ChatColor.RED + "Error: The murder game can only start if there are at least 2 players");
			Tool.pp(ChatColor.RED + "Error: The murder game can only start if there are at least 2 players");
			Game.setGame(null);
			return true;
		} else if (game.notEnoughSpawnPoint()) {
			Tool.pc(ChatColor.RED
					+ "Error: Please, set spawnpoint(s) using /m spawnpoint <id> <x> <y> <z> to start a game");
			Tool.pp(ChatColor.RED
					+ "Error: Please, set spawnpoint(s) using /m spawnpoint <id> <x> <y> <z> to start a game");
			Game.setGame(null);
			return true;
		}

		Tool.tp(ChatColor.BLUE, "Game sarting in 5s", ChatColor.WHITE, "You are a simple innocent", 1, 1, 0);
		Tool.tp(ChatColor.BLUE, "Game starting in 5s", ChatColor.RED, "You are the Murderer", game.getMurderer(), 1, 1,
				0);
		Tool.tp(ChatColor.BLUE, "Game starting in 5s", ChatColor.GREEN, "You are the Guardian", game.getGuardian(), 1,
				1, 0);

		game.spawnPlayers();
		game.clearPlayers();
		game.playerInAdventureMode();

		MyRunnable myRunnable = new MyRunnable() {

			@Override
			public void run() {
				Tool.tp(ChatColor.BLUE, "Game starting in " + times + "s", ChatColor.WHITE, "You are a simple innocent",
						0, 1, 1);
				Tool.tp(ChatColor.BLUE, "Game starting in " + times + "s", ChatColor.RED, "You are the Murderer",
						game.getMurderer(), 0, 1, 1);
				Tool.tp(ChatColor.BLUE, "Game starting in " + times + "s", ChatColor.GREEN, "You are the Guardian",
						game.getGuardian(), 0, 1, 1);
			}
		};
		myRunnable.setGame(game);

		Tool.interval(1, 4, myRunnable);

		MyRunnable glowingRunnable = new MyRunnable() {

			@Override
			public void run() {
				ArrayList<Player> players = Game.getGame().getPlayers();
				for (Player player : players) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 50, 1));
				}
			}
		};

		Game.setGlowingRunnable(glowingRunnable);

		Tool.interval(3 * 60, -1, glowingRunnable);

		Tool.timeout(5, new Runnable() {
			@Override
			public void run() {
				Game game = Game.getGame();
				game.makeTheMurderer();
				game.makeTheGuardian();
			}
		});

		return true;
	}

	public static boolean stopGame(CommandSender sender, Command cmd, String label) {

		Game game = Game.getGame();
		if (game == null) {
			Tool.pc(ChatColor.RED + "Error: No game started now");
			return true;
		}

		Tool.tp(ChatColor.BLUE, "The murder game stops");

		game.teleportPlayersToSpawn();
		game.clearPlayers();
		game.playerInAdventureMode();

		Game.stopGlowingRunnable();
		Game.setGame(null);

		return true;
	}

	public static boolean setSpawnpoint(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 5 && Tool.isInteger(args[1]) && Tool.isInteger(args[2]) && Tool.isInteger(args[3])
				&& Tool.isInteger(args[4])) {

			int id = Integer.parseInt(args[1]);
			Game.setSpawnpoint(id, new Location(null, Integer.parseInt(args[2]), Integer.parseInt(args[3]),
					Integer.parseInt(args[4])));

			Tool.pp(ChatColor.GREEN + "Spawnpoint " + id + " set");

		} else if (args.length == 3 && (args[2].equals("remove") || args[2].equals("rm"))) {

			int id = Integer.parseInt(args[1]);
			if (!Game.deleteSpawnpoint(id)) {
				Tool.pp(ChatColor.RED + "This spawnpoint doesn't exist");
				return false;
			}
			Tool.pp(ChatColor.GREEN + "Spawnpoint " + id + " deleted");
		} else {
			return false;
		}
		return true;

	}

	public static boolean addSpawnpointOnPlayer(CommandSender sender, Command cmd, String label) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;
		Location newLocation = player.getLocation();
		newLocation.add(0, 1, 0);

		int id = Game.getNextSpawnpointId();
		Tool.pp(ChatColor.GREEN + "Spawnpoint " + id + " created");

		Game.setSpawnpoint(id, newLocation);

		return true;
	}

	public static boolean saveSpawnpoints(CommandSender sender, Command cmd, String label) {
		Map<Integer, Location> spawnpoints = Game.getSpawnPoints();

		String saveString = "";
		for (int id : spawnpoints.keySet()) {
			Location location = spawnpoints.get(id);
			saveString += location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + "-";
		}

		boolean result = Tool.saveString("murder_plugin.save", saveString);
		if (result) {
			Tool.pp(ChatColor.GREEN + "Spawnpoints saved successfully");
			return true;
		} else {
			Tool.pp(ChatColor.RED + "Spawnpoints can't be saved");
			return false;
		}
	}

	public static boolean loadSpawnpoints(CommandSender sender, Command cmd, String label) {
		String fileContent = Tool.loadString("murder_plugin.save");

		if (fileContent == null || fileContent.equals("")) {
			Tool.pp(ChatColor.RED + "An error occurred while loading the backup.");
			return false;
		}

		String[] spawnpointsData = fileContent.split("-");
		if (spawnpointsData.length >= 1) {
			Game.getSpawnPoints().clear();
		}
		int count = 0;
		for (String spawnpointData : spawnpointsData) {
			String[] data = spawnpointData.split(":");
			Location location = new Location(null, Integer.parseInt(data[0]), Integer.parseInt(data[1]),
					Integer.parseInt(data[2]));
			int id = Game.getNextSpawnpointId();

			Game.setSpawnpoint(id, location);
			count++;
		}
		Tool.pp(ChatColor.GREEN + "" + count + " spawnpoints loaded");

		return true;
	}

	public static boolean getStats(CommandSender sender, Command cmd, String label) {

		MurderPlugin plugin = (MurderPlugin) Tool.getPlugin();
		int gameCount = plugin.getStatistics().getGameCount();
		Tool.pp("MurderPlugin " + plugin.version + " | Game count: " + gameCount);

		return true;
	}

	public static boolean getPlayerStats(CommandSender sender, Command cmd, String label) {

		if (sender instanceof Player) {
			Player player = (Player) sender;

			MurderPlugin plugin = (MurderPlugin) Tool.getPlugin();
			PlayerStatistics playerStats = plugin.getStatistics().getPlayerStats(player);

			Tool.pp(playerStats.getDisplay(), player);
		}
		
		return true;
	}

	public static boolean clearPoints(CommandSender sender, Command cmd, String label) {
		
		if (sender instanceof Player) {
			Player player = (Player) sender;

			MurderPlugin plugin = (MurderPlugin) Tool.getPlugin();
			PlayerStatistics playerStats = plugin.getStatistics().getPlayerStats(player);
			playerStats.clearPoints();

			Tool.pp("Current Point cleared", player);
		}
		
		return true;
	}
}