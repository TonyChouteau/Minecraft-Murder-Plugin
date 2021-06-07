package fr.tonychouteau.murder.bukkit.util;

// Own import

// Java Import
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Map;
import java.util.Map.Entry;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;

// Bukkit Import
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

public class Tool {

	private static Plugin murderPlugin;

	public static void setPlugin(Plugin plugin) {
		murderPlugin = plugin;
	}

	public static Plugin getPlugin() {
		return murderPlugin;
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
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

	public static void pc(String str) {
		Bukkit.getLogger().info(str);
	}

	public static void pp(String str) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(str);
		}
	}

	public static void pp(String str, Player player) {
		player.sendMessage(str);
	}

	public static void tp(ChatColor color, String str) {
		tp(color, str, 1, 2, 1);
	}

	public static void tp(ChatColor color, String str, Player player) {
		tp(color, str, player, 1, 2, 1);
	}

	public static void tp(ChatColor color, String str, ChatColor color2, String str2) {
		tp(color, str, color2, str2, 1, 2, 1);
	}

	public static void tp(ChatColor color, String str, ChatColor color2, String str2, Player player) {
		tp(color, str, color2, str2, player, 1, 2, 1);
	}

	public static void tp(ChatColor color, String str, int i, int d, int o) {
		tp(color, str, ChatColor.GOLD, "", i, d, o);
	}

	public static void tp(ChatColor color, String str, Player player, int i, int d, int o) {
		tp(color, str, ChatColor.GOLD, "", player, i, d, o);
	}

	public static void tp(ChatColor color, String str, ChatColor color2, String str2, int i, int d, int o) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(color + str, color2 + str2, i * 20, d * 20, o * 20);
		}
	}

	public static void tp(ChatColor color, String str, ChatColor color2, String str2, Player player, int i, int d,
			int o) {
		player.sendTitle(color + str, color2 + str2, i * 20, d * 20, o * 20);
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList shuffleArray(ArrayList array) {
		Collections.shuffle(array);
		return array;
	}

	public static void interval(int seconds, int times, MyRunnable runObject) {
		runObject.setTimes(times);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {

			@Override
			public void run() {
				if (!runObject.isStopped()) {
					runObject.run();
					if (times > 0) {
						interval(seconds, times - 1, runObject);
					} else if (times == -1) {
						interval(seconds, -1, runObject);
					}
				}
			}
		}, (seconds * 20));
	}

	public static void timeout(int seconds, Runnable runObject) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), runObject, (seconds * 20));
	}

	public static String loadString(String filename) {
		File file = new File("./plugins/MurderPlugin/" + filename);

		try (FileReader fr = new FileReader(file)) {
			char[] chars = new char[(int) file.length()];
			fr.read(chars);

			Tool.pc("Save done in " + filename);
			return new String(chars);
		} catch (IOException e) {
			Tool.pc("Error while saving statistics");
			return null;
		}
	}

	public static boolean saveString(String filename, String saveString) {
		try {
			File file = new File("./plugins/MurderPlugin/" + filename);
			file.createNewFile();

			FileWriter fileWriter = new FileWriter(file);

			fileWriter.write(saveString);
			fileWriter.close();

			Tool.pc("Save done in " + filename);
			return true;
		} catch (IOException e) {
			Tool.pc("Error while saving in " + filename);
			return false;
		}
	}

    public static <K, V extends Comparable<? super V>> ArrayList<Entry<K, V>> sortByValue(Map<K, V> map) {
        ArrayList<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
		Collections.reverse(list);

        return list;
    }

	public static String order(int i) {
		if (i == 1) {
			return "er";
		} else {
			return "e";
		}
	}
}