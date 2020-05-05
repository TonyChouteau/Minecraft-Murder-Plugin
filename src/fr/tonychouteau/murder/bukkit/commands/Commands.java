package fr.tonychouteau.murder.bukkit.commands;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.game.Game;

// Java Import
import java.util.ArrayList;

// Bukkit Import
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;


//Commands Class
public class Commands {
    
    public static boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if ((cmd.getName().equalsIgnoreCase("murder") || cmd.getName().equalsIgnoreCase("m")) && args.length == 1) {

            switch (args[0]) {
            case "help":
                help(sender, cmd, label);
                return true;
            case "start":
                return startGame(sender, cmd, label);
            case "stop":
                return stopGame(sender, cmd, label);
            }
        }

        return false;
    }

    public static void help(CommandSender sender, Command cmd, String label){
        try{
            Player player = (Player) sender;
            Tool.pp("/start <nombre_de_joueurs> \n    Lancer une partie\n/stop\n   Arrete la partie en cours", player);
        } catch (Exception e){
            Tool.pc("/start <nombre_de_joueurs> \n    Lancer une partie\n/stop\n    Arrete la partie en cours");
        }
    }

    public static boolean startGame(CommandSender sender, Command cmd, String label){

        Game game = Game.getGame();
        if (game!=null){
            Tool.pc("Error: A game is already started");
            Tool.pp("Error: A game is already started");
            return true;
        }
        
        game = new Game();

        Tool.pp(Boolean.toString(game.notEnoughtPlayer()));
        if (game.notEnoughtPlayer()){
            Tool.pc("====== The murder party can only start if there are at least 2 players ======");
            Tool.pp("====== The murder party can only start if there are at least 2 players ======");
            Game.setGame(null);
            return true;
        }

        Tool.pc("====== The murder party starts ======");
        Tool.pp("====== The murder party starts ======");


        game.clearPlayers();
        
        //Murderer
        game.makeTheMurderer();

        //Guardian
        game.makeTheGuardian();

        return true;
    }

    public static boolean stopGame(CommandSender sender, Command cmd, String label){

        Game game = Game.getGame();
        if (game==null){
            Tool.pc("Error: No game started now");
            Tool.pp("Error: No game started now");
            return true;
        }

        Tool.pc("====== The murder party stops ======");
        Tool.pp("====== The murder party stops ======");

        game.teleportPlayersToSpawn();

        Game.setGame(null);

        return true;
    }
}