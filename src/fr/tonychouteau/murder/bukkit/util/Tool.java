package fr.tonychouteau.murder.bukkit.util;

// Java Import
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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

    public static int getRandomNumberInRange(int min, int max) {

        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
    
        Random r = new Random();
        return r.nextInt(max - min) + min;
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

    public static ArrayList shuffleArray(ArrayList array){
        Collections.shuffle(array);
        return array;
    }
}