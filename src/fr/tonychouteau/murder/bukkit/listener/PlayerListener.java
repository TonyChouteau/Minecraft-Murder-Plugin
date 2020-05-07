package fr.tonychouteau.murder.bukkit.listener;

// My Package
import fr.tonychouteau.murder.bukkit.util.Tool;
import fr.tonychouteau.murder.bukkit.game.Game;

// Bukkit Import
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import org.bukkit.entity.Projectile;

// Class Defintiion
public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.sendMessage("Bienvenue sur le serveur, " + e.getPlayer().getName() + " !");
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getName().equals("Arrow")) {

			try {
				Player killed = (Player) event.getHitEntity();
				// killed.damage(100);
				event.getEntity().remove();
				
				Game game = Game.getGame();
				if (killed.equals(game.getMurderer())) {
					game.runnersWin();
				} else {
					// BLINDNESS + DROP GUN
					game.badVictim(killed);
					game.playerKilled(killed, game.getGuardian());
				}
			} catch (Exception e) {
				// Tool.pp(e.getStackTrace().toString());
			}
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {

		if (event.getDamage() >= 20) {
			if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
				Player killed = (Player) event.getEntity();
				event.setDamage(0);

				Game game = Game.getGame();
				if (event.getEntity() != game.getMurderer()) {
					game.playerKilled(killed);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage("");
		/*Player killed = (Player) event.getEntity();
		Game game = Game.getGame();
		if (event.getEntity() != game.getMurderer()) {
			game.playerKilled(killed);
		}*/
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			event.setDamage(0);
		}
	}
}