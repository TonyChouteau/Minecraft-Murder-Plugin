package fr.tonychouteau.murder.bukkit.commands;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.util.MyRunnable;

import fr.tonychouteau.murder.bukkit.game.Game;

// Java Import
import java.util.ArrayList;

// Bukkit Import
import org.bukkit.Bukkit;
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
					return setSpawnpoint(sender, cmd, label, args);
				case "addSpawnpointHere":
					return addSpawnpointOnPlayer(sender, cmd, label);
			}
		}

		return false;
	}

	public static void help(CommandSender sender, Command cmd, String label) {
		try {
			Player player = (Player) sender;
			Tool.pp(ChatColor.GREEN
					+ "/start <nombre_de_joueurs> \n    Lancer une partie\n/stop\n   Arrete la partie en cours\n/spawnpoint\n   Ajoute un spawnpoint. /spawnpoint <id> <x> <y> <z>",
					player);
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

		if (game.notEnoughtPlayer()) {
			Tool.pc(ChatColor.RED + "Error: The murder game can only start if there are at least 2 players");
			Tool.pp(ChatColor.RED + "Error: The murder game can only start if there are at least 2 players");
			Game.setGame(null);
			return true;
		} else if (game.notEnoughtSpawnPoint()) {
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

		Tool.interval(30, 1000000, glowingRunnable);

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

		Game.setSpawnpoint(Game.getNextSpawnpointId(), newLocation);

		return true;
	}
}