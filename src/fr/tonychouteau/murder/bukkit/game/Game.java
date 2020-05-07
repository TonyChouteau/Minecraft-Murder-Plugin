package fr.tonychouteau.murder.bukkit.game;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;

// Java Import
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import java.lang.InterruptedException;

// Bukkit Import
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.ChatColor;

import org.bukkit.potion.Potion;
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

// Class Definition
public class Game {

	private static Game game = null;
	private static Map<Integer, Location> spawnpoints = new HashMap<>(0);

	public static void setGame(Game newGame) {
		game = newGame;
	}

	public static Game getGame() {
		return game;
	}

	public static void setSpawnpoint(int id, Location location){
		spawnpoints.put(id, location);
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

	private ScoreboardManager manager = Bukkit.getScoreboardManager();
	private Scoreboard board = manager.getNewScoreboard();
	private Team team;

	public Game() {
		this.players = Tool.shuffleArray(new ArrayList<Player>(Bukkit.getOnlinePlayers()));
		this.playersAlive = Tool.shuffleArray(new ArrayList<Player>(players));
		this.deadPlayers = new ArrayList<>(0);
		if (!notEnoughtPlayer()) {
			this.murderer = players.get(0);
			this.guardian = players.get(1);
		}
		this.playersAlive.remove(this.murderer);
		this.initGame();

		team = board.registerNewTeam("murder-game");
		for (Player p : this.players) {
			team.addEntry(p.getName());
			p.setPlayerListName(ChatColor.WHITE + p.getName());
		}
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

		Game.setGame(this);
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
	
	public void spawnPlayers(){
		ArrayList<Location> spawnpointsLocation = Tool.shuffleArray(new ArrayList<>(spawnpoints.values()));
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

	public boolean notEnoughtPlayer() {
		if (players.size() < 2) {
			return true;
		} else {
			return false;
		}
	}

	public boolean notEnoughtSpawnPoint() {
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

	public void runnersWin(){
        Tool.pc("======    The Innocents Win    ======");
        Tool.pp("======    The Innocents Win    ======");

        this.teleportPlayersToSpawn();
		this.clearPlayers(); 
		this.playerInAdventureMode();

        Game.setGame(null);
	}

	public void badVictim(Player innocent){
		Tool.pp(innocent.getName()+" was innocent, U SON OF A BITCH !");
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
		killed.setPlayerListName(ChatColor.WHITE + killed.getName());
		killed.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 255, true, false));
		killed.sendTitle("You're dead",	  null, 1*20, 2*20, 1*20);

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

	public void playerKilled(Player killed){
		playerKilled(killed, null);
	}

	public void murdererWins(){
		Tool.pc("======    The Murderer Wins    ======");
		Tool.pp("======    The Murderer Wins    ======");

		this.teleportPlayersToSpawn();
		this.clearPlayers();
		this.playerInAdventureMode();

		Game.setGame(null);
	}
}