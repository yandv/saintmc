package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;

public class CombatListener extends Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		Player damager = null;
		
		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			
			if (projectile.getShooter() instanceof Player) {
				damager = (Player) projectile.getShooter();
			}
		}
		
		PlayerDamagePlayerEvent playerDamagePlayerEvent = new PlayerDamagePlayerEvent(player, damager, event.isCancelled(), event.getDamage(), event.getFinalDamage());
		
		Bukkit.getPluginManager().callEvent(playerDamagePlayerEvent);
		
		event.setCancelled(playerDamagePlayerEvent.isCancelled());
		event.setDamage(playerDamagePlayerEvent.getDamage());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player p = (Player) event.getDamager();
		ItemStack sword = p.getItemInHand();
		double damage = event.getDamage();
		double danoEspada = getDamage(sword.getType());
		boolean isMore = false;
		
		if (damage > 1)
			isMore = true;
		
		if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			for (PotionEffect effect : p.getActivePotionEffects())
				if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
					double minus;
					
					if (isCrital(p))
						minus = (danoEspada + (danoEspada / 2)) * 1.2 * (effect.getAmplifier() + 1);
					else
						minus = danoEspada * 1.3 * (effect.getAmplifier() + 1);
					
					
					damage = damage - minus;
					damage += 2 * (effect.getAmplifier() + 1);
					break;
				}
		
		if (!sword.getEnchantments().isEmpty()) {
			if (sword.containsEnchantment(Enchantment.DAMAGE_ALL)) {
				damage += 0.5 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}
		
		if (isCrital(p)) {
			damage = damage - (danoEspada / 2);
			damage += 1;
		}
		
		if (isMore)
			damage -= 2;
		
		event.setDamage(damage);
	}

	private boolean isCrital(Player p) {
		return p.getFallDistance() > 0 && !((Entity)p).isOnGround() && !p.hasPotionEffect(PotionEffectType.BLINDNESS);
	}

	private double getDamage(Material type) {
		double damage = 1.0;
		
		if (type.toString().contains("DIAMOND_")) {
			damage = 6.0;
		} else if (type.toString().contains("IRON_")) {
			damage = 5.0;
		} else if (type.toString().contains("STONE_")) {
			damage = 3.0;
		} else if (type.toString().contains("WOOD_")) {
			damage = 2.5;
		} else if (type.toString().contains("GOLD_")) {
			damage = 2.5;
		}
		
		if (!type.toString().contains("_SWORD")) {
			damage--;
			if (!type.toString().contains("_AXE")) {
				damage--;
				if (!type.toString().contains("_PICKAXE")) {
					damage--;
					if (!type.toString().contains("_SPADE")) {
						damage = 1.0;
					}
				}
			}
		}
		
		return damage;
	}
}
