package fr.tonychouteau.murder.bukkit.game;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;

// Java Import
import java.util.ArrayList;

// Bukkit Import
import org.bukkit.Bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Player;

// Class Definition
public class Game {

	private static Game game = null;

	public static void setGame(Game newGame) {
		game = newGame;
	}

	public static Game getGame() {
		return game;
	}

	private ArrayList<Player> players;
	private Player murderer;
	private Player guardian;

	public Game() {
		this.players = Tool.getPlayersShuffled();
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
        this.players = new ArrayList<>(Bukkit.getOnlinePlayers());
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

	public void makeTheMurderer() {
		Material knife = Material.IRON_SWORD;
		ItemStack knifeStack = new ItemStack(knife);
		ItemMeta knifeMeta = knifeStack.getItemMeta();

		knifeMeta.setDisplayName("Knife");
		// knifeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new
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
		// knifeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new
		// AttributeModifier("Attack Damage", 20,
		// AttributeModifier.Operation.ADD_NUMBER));
		// knifeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new
		// AttributeModifier("Attack Speed", 20,
		// AttributeModifier.Operation.ADD_NUMBER));
		crossBowStack.setItemMeta(crossBowMeta);
		crossBowStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 100);

		Material arrow = Material.ARROW;
		ItemStack arrowStack = new ItemStack(arrow, 64);

		guardian.getInventory().setHeldItemSlot(1);
		guardian.getInventory().setItem(0, crossBowStack);
		guardian.getInventory().setItem(8, arrowStack);
	}

	public void runnersWin(){
		//TODO
	}

	public void badVictim(){
		//TODO
	}

	public void guardianKilled(){
		//TODO
	}
}