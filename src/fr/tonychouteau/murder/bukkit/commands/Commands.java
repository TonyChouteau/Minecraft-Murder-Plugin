package fr.tonychouteau.murder.bukkit.commands;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.game.Game;

// Java Import
import java.util.ArrayList;

// Bukkit Import
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.entity.Player;

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
			}
		}

		return false;
	}

	public static void help(CommandSender sender, Command cmd, String label) {
		try {
			Player player = (Player) sender;
			Tool.pp("/start <nombre_de_joueurs> \n    Lancer une partie\n/stop\n   Arrete la partie en cours\n/spawnpoint\n   Ajoute un spawnpoint. /spawnpoint <id> <x> <y> <z>",
					player);
		} catch (Exception e) {
			Tool.pc("/start <nombre_de_joueurs> \n    Lancer une partie\n/stop\n    Arrete la partie en cours");
		}
	}

	public static boolean startGame(CommandSender sender, Command cmd, String label) {

		Game game = Game.getGame();
		if (game != null) {
			Tool.pc("Error: A game is already started");
			Tool.pp("Error: A game is already started");
			return true;
		}

		game = new Game();

		if (game.notEnoughtPlayer()) {
			Tool.pc("====== The murder party can only start if there are at least 2 players ======");
			Tool.pp("====== The murder party can only start if there are at least 2 players ======");
			Game.setGame(null);
			return true;
		} else if (game.notEnoughtSpawnPoint()){
			Tool.pc("====== Please, set spawnpoint(s) using /m spawnpoint <id> <x> <y> <z> to start a game ======");
			Tool.pp("====== Please, set spawnpoint(s) using /m spawnpoint <id> <x> <y> <z> to start a game ======");
			Game.setGame(null);
			return true;
		}

		Tool.pc("====== The murder party starts ======");
		Tool.pp("====== The murder party starts ======");

		game.clearPlayers();
		game.playerInAdventureMode();

		// Murderer
		game.makeTheMurderer();

		// Guardian
		game.makeTheGuardian();

		game.spawnPlayers();

		return true;
	}

	public static boolean stopGame(CommandSender sender, Command cmd, String label) {

		Game game = Game.getGame();
		if (game == null) {
			Tool.pc("Error: No game started now");
			Tool.pp("Error: No game started now");
			return true;
		}

		Tool.pc("====== The murder party stops =======");
		Tool.pp("====== The murder party stops =======");

		game.teleportPlayersToSpawn();
		game.clearPlayers();

		Game.setGame(null);

		return true;
	}

	public static boolean setSpawnpoint(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 5 && Tool.isInteger(args[1]) && Tool.isInteger(args[2]) && Tool.isInteger(args[3]) && Tool.isInteger(args[4])) {

			Game.setSpawnpoint(Integer.parseInt(args[1]), new Location(null, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])));
			
		} else if (args.length == 3 && (args[2].equals("remove") || args[2].equals("rm"))) {

			if (!Game.deleteSpawnpoint(Integer.parseInt(args[1]))){
				Tool.pp("This spawnpoint doesn't exist");
				return false;
			}
		} else {
			return false;
		}
		return true;

	}
}