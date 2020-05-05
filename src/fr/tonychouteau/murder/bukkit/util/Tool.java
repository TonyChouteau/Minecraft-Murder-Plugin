package fr.tonychouteau.murder.bukkit.util;

// Java Import
import java.util.ArrayList;
import java.util.Collections;

// Bukkit Import
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.Location;

public class Tool {
    
    public static boolean isInteger(String str){
        try {
            int x=Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static void pc(String str){
        Bukkit.getLogger().info(str);
    }

    public static void pp(String str){
        ArrayList<Player> listOfPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player p : listOfPlayers){
            p.sendMessage(str);
        }
    }

    public static void pp(String str, Player player){
        player.sendMessage(str);
    }

    public static ArrayList<Player> getPlayersShuffled(){
        ArrayList<Player> listOfPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(listOfPlayers);
        return listOfPlayers;
    }
}