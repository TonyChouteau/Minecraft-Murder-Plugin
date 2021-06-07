package fr.tonychouteau.murder.bukkit.game;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.MurderPlugin;
import fr.tonychouteau.murder.bukkit.util.MyRunnable;

// Java Import
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

// Bukkit Import
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Player;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

// Class Definition
public class Game {
	public static int INNOCENT_WINS = 0;
	public static int MURDERER_WINS = 1;

	public static float POINTS_MURDERER_KILL = 1; //
	public static float POINTS_MURDERER_KILL_GUARDIAN = 2; //
	public static float POINTS_MURDERER_WINS = 2; //

	public static float POINTS_GUARDIAN_KILL_MURDERER = 2; //
	public static float POINTS_INNOCENTS_WIN = 2; //
	public static float POINTS_PUNCH = 0.5f; //

	public static float POINTS_GUARDIAN_KILLS_INNOCENT = -1; //

	private static Game game = null;
	private static Map<Integer, Location> spawnpoints = new HashMap<>(0);
	private static ItemStack knifeStack = null; 
	private static MyRunnable glowingRunnable = null;

	public static void setGlowingRunnable(MyRunnable runnable) {
		glowingRunnable = runnable;
	}

	public static void stopGlowingRunnable() {
		glowingRunnable.stop();
	}

	public static void setGame(Game newGame) {
		game = newGame;
	}

	public static Game getGame() {
		return game;
	}

	public static ItemStack getKnifeStack() {
		return knifeStack;
	}

	public static void setSpawnpoint(int id, Location location){
		spawnpoints.put(id, location);
	}

	public static Map<Integer, Location> getSpawnPoints() {
		return spawnpoints;
	}

	public static int getNextSpawnpointId() {
		int unusedIndex = 0;
		while (spawnpoints.containsKey(unusedIndex)) {
			unusedIndex++;
		}
		return unusedIndex;
	}

	public static boolean deleteSpawnpoint(int id){
		if (!spawnpoints.containsKey(id)){
			return false;
		}
		spawnpoints.remove(id);
		return true;
	}

	private ArrayList<Player> players;
	private ArrayList<Player> playersAlive;
	private Player murderer;
	private Player guardian;
	private ArrayList<Player> deadPlayers;

	private Map<Player, Float> playerPoints;

	private ScoreboardManager manager = Bukkit.getScoreboardManager();
	private Scoreboard board = manager.getMainScoreboard();
	private Team team;

	private MurderPlugin plugin = null;

	@SuppressWarnings("unchecked")
	public Game() {

		this.players = Tool.shuffleArray(new ArrayList<Player>(Bukkit.getOnlinePlayers()));
		this.playersAlive = Tool.shuffleArray(new ArrayList<Player>(players));
		this.deadPlayers = new ArrayList<>(0);

		this.playerPoints = new HashMap<Player, Float>(0);
		for (Player p: this.players) {
			this.playerPoints.put(p, 0f);
		}

		this.players.get(0).getWorld().setDifficulty(Difficulty.PEACEFUL);

		if (!notEnoughPlayer()) {
			this.murderer = players.get(0);
			this.guardian = players.get(1);
		}
		this.playersAlive.remove(this.murderer);
		this.initGame();
		
		try {
			team = board.registerNewTeam("murder-game");
		} catch (IllegalArgumentException e){
			team = board.getTeam("murder-game");
		}

		for (Player p : this.players) {
			team.addEntry(p.getName());
			p.setPlayerListName(ChatColor.WHITE + "");
		}
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);

		plugin = (MurderPlugin) Tool.getPlugin();
		Game.setGame(this);
	}

	public void addPointToPlayer(Player p, float points) {
		playerPoints.put(p, playerPoints.get(p)+points);
	}

	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public void initGame(){
		this.clearPlayers();
	}

    public void teleportPlayersToSpawn() {
        for (Player p : players){
            p.getInventory().clear();
            p.teleport(p.getWorld().getSpawnLocation());
        }
	}
	
	public void clearPlayers(){
		for (Player p : this.players){
			p.getInventory().clear();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void spawnPlayers(){
		ArrayList<Location> spawnpointsLocation = Tool.shuffleArray(new ArrayList<Location>(spawnpoints.values()));
		int i = 0;
		for (Player p : this.players){
			Location l = spawnpointsLocation.get(i%spawnpointsLocation.size());
			i++;
			Location spawnPoint = new Location(p.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
			p.teleport(spawnPoint);
		}
	}

	public void playerInAdventureMode(){
		for (Player p : this.players){
			p.setGameMode(GameMode.ADVENTURE);
		}
	}

	public Player getMurderer() {
		return murderer;
	}

	public Player getGuardian() {
		return guardian;
	}

	public boolean notEnoughPlayer() {
		if (players.size() < 2) {
			return true;
		} else {
			return false;
		}
	}

	public boolean notEnoughSpawnPoint() {
		ArrayList<Location> spawnpointsLocation = new ArrayList<>(spawnpoints.values());
		if (spawnpointsLocation.size() < 1) {
			return true;
		} else {
			return false;
		}
	}

	public void makeTheMurderer() {
		Material knife = Material.IRON_SWORD;
		ItemStack knifeStack = new ItemStack(knife);
		ItemMeta knifeMeta = knifeStack.getItemMeta();

		knifeMeta.setDisplayName("Knife");
		knifeMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(),"Speed", 0.2, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
		// AttributeModifier("Attack Damage", 20,
		// AttributeModifier.Operation.ADD_NUMBER));
		// knifeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new
		// AttributeModifier("Attack Speed", 20,
		// AttributeModifier.Operation.ADD_NUMBER));
		knifeStack.setItemMeta(knifeMeta);
		knifeStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 100);

		murderer.getInventory().setHeldItemSlot(1);
		murderer.getInventory().setItem(0, knifeStack);
		Game.knifeStack = knifeStack;
	}

	public void makeTheGuardian() {
		Material crossBow = Material.CROSSBOW;
		ItemStack crossBowStack = new ItemStack(crossBow);
		ItemMeta crossBowMeta = crossBowStack.getItemMeta();

		crossBowMeta.setDisplayName("Gun");
		//knifeMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
		// AttributeModifier("Attack Damage", 20,
		// AttributeModifier.Operation.ADD_NUMBER));
		// knifeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new
		// AttributeModifier("Attack Speed", 20,
		// AttributeModifier.Operation.ADD_NUMBER));
		crossBowStack.setItemMeta(crossBowMeta);
		crossBowStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 100);
		crossBowStack.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 2);

		Material arrow = Material.ARROW;
		ItemStack arrowStack = new ItemStack(arrow, 64);

		guardian.getInventory().setHeldItemSlot(1);
		guardian.getInventory().setItem(0, crossBowStack);
		guardian.getInventory().setItem(8, arrowStack);
	}

	public void badVictim(Player innocent){
		Tool.pp(ChatColor.BLUE + innocent.getName()+" was innocent");
		Tool.tp(ChatColor.WHITE, innocent.getName()+""+ChatColor.RED+" was innocent", this.guardian);

		addPointToPlayer(guardian, Game.POINTS_GUARDIAN_KILLS_INNOCENT);
		this.guardian.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 255, true, false));
	}

	public void changeGuardian(){
		this.guardian.getInventory().clear();
		Tool.timeout(5, new Runnable(){
			@Override
			public void run() {
				Game game = Game.getGame();
				ArrayList<Player> alive = new ArrayList<Player>(playersAlive);
				if (alive.size() >= 2){
					alive.remove(game.guardian);
				}
				game.guardian = alive.get(0);
				makeTheGuardian();
			}
		});
	}

	public void playerKilled(Player killed, Player killer){
		killed.setGameMode(GameMode.SPECTATOR);
		killed.setPlayerListName(ChatColor.WHITE + "");
		killed.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 255, true, false));
		Tool.tp(ChatColor.RED, "You're dead", ChatColor.WHITE, "You have been killed", killed);

		if (killer == this.murderer) {
			if (killed != this.guardian) {
				addPointToPlayer(this.murderer, Game.POINTS_MURDERER_KILL);
			} else if (this.guardian.getInventory().contains(Material.CROSSBOW)) {
				addPointToPlayer(this.murderer, Game.POINTS_MURDERER_KILL_GUARDIAN);
			}
		}

		if (!deadPlayers.contains(killed)){
			deadPlayers.add(killed);
			playersAlive.remove(killed);
		}
		if (deadPlayers.size() == players.size()-1) {
			this.murdererWins();
		} else if (killed == this.guardian || (killed != this.murderer && killer == this.guardian)) {
			this.changeGuardian();
		}
	}

	public void runnersWin(){
		Tool.tp(ChatColor.GREEN, "Innocents win !", ChatColor.WHITE, "The Murderer was "+ChatColor.RED+""+game.getMurderer().getName());

		addPointToPlayer(guardian, Game.POINTS_GUARDIAN_KILL_MURDERER);
		for (Player p: players) {
			if (p != murderer) {
				addPointToPlayer(p, Game.POINTS_INNOCENTS_WIN);
			}
		}
		plugin.getStatistics().endGame(players, murderer, guardian, Game.INNOCENT_WINS);
		plugin.getStatistics().incrementGameCount();
		for (Player p : players) {
			plugin.getStatistics().addPlayerPoints(p, playerPoints.get(p).intValue());
		}

        this.teleportPlayersToSpawn();
		this.clearPlayers(); 
		this.playerInAdventureMode();

		Game.stopGlowingRunnable();
        Game.setGame(null);
	}

	public void murdererWins(){
		Tool.tp(ChatColor.RED, "Murderer wins !", ChatColor.WHITE, "The Murderer was "+ChatColor.RED+""+game.getMurderer().getName());

		addPointToPlayer(murderer, Game.POINTS_MURDERER_WINS);
		plugin.getStatistics().endGame(players, murderer, guardian, Game.MURDERER_WINS);
		plugin.getStatistics().incrementGameCount();
		for (Player p : players) {
			plugin.getStatistics().addPlayerPoints(p, playerPoints.get(p).intValue());
		}

		this.teleportPlayersToSpawn();
		this.clearPlayers();
		this.playerInAdventureMode();

		Game.stopGlowingRunnable();
		Game.setGame(null);
	}
}