package fr.tonychouteau.murder.bukkit.game;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;

// Java Import
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

// Bukkit Import
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;

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
	private Player murderer;
	private Player guardian;
	private int guardianId = 1;
	private ArrayList<Player> deadPlayers;

	public Game() {
		this.players = Tool.shuffleArray(new ArrayList<Player>(Bukkit.getOnlinePlayers()));
		this.deadPlayers = new ArrayList<>(0);
		if (!notEnoughtPlayer()) {
			this.murderer = players.get(0);
			this.guardian = players.get(1);
		}
		this.initGame();

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
		Game game = Game.getGame();
		
        Tool.pc("======    The Innocents Win    ======");
        Tool.pp("======    The Innocents Win    ======");

        game.teleportPlayersToSpawn();
		game.clearPlayers();

        Game.setGame(null);
	}

	public void badVictim(Player innocent){
		Tool.pp(innocent.getName()+" était Innocent");
		guardian.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 255, true, false));
	}

	public void guardianKilled(){
		guardian.getInventory().clear();
		guardianId++;
		guardian = players.get(guardianId);
		makeTheGuardian();
	}

	public void playerKilled(Player killed){
		if (!deadPlayers.contains(killed)){
			deadPlayers.add(killed);
		}
		if (deadPlayers.size() == players.size()-1) {
			Tool.pc("======    The Murderer Wins    ======");
			Tool.pp("======    The Murderer Wins    ======");

			game.teleportPlayersToSpawn();
			game.clearPlayers();
	
			Game.setGame(null);
		} else {
			guardianKilled();
		}
	}

	public void murdererWins(){
		//TODO
	}
}