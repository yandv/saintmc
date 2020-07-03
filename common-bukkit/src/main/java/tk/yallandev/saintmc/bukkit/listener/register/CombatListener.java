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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;

public class CombatListener extends Listener {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getCause() == DamageCause.FALL)
			if (player.hasMetadata("nofall")) {
				MetadataValue metadata = player.getMetadata("nofall").stream().findFirst().orElse(null);

				if (metadata.asLong() > System.currentTimeMillis())
					event.setCancelled(true);

				metadata.invalidate();
			}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

		if (!(damager instanceof Player))
			return;

		PlayerDamagePlayerEvent playerDamagePlayerEvent = new PlayerDamagePlayerEvent(player, damager,
				event.isCancelled(), event.getDamage(), event.getFinalDamage());

		Bukkit.getPluginManager().callEvent(playerDamagePlayerEvent);

		event.setCancelled(playerDamagePlayerEvent.isCancelled());
		event.setDamage(playerDamagePlayerEvent.getDamage());
	}

//	@EventHandler
//	public void onPlayerVelocity(PlayerVelocityEvent event) {
//		Player player = event.getPlayer();
//
//		if (player.hasMetadata("knockback"))
//			if (player.getMetadata("knockback").stream().findFirst().orElse(null).asLong() > System.currentTimeMillis())
//				event.setCancelled(true);
//	}
//
//	@SuppressWarnings("deprecation")
//	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//	public void onEntityDamageByEntitya(EntityDamageByEntityEvent event) {
//		if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
//			return;
//
//		Player damaged = (Player) event.getEntity();
//		Player damager = (Player) event.getDamager();
//
//		if (damaged.getNoDamageTicks() > damaged.getMaximumNoDamageTicks() / 2D)
//			return;
//
//		double horMultiplier = 1.0;
//		double verMultiplier = 1.0;
//		double sprintMultiplier = damager.isSprinting() ? damaged.isSprinting() ? 0.4D : 0.8D : 0.6D;
//		double kbMultiplier = damager.getItemInHand() == null ? 0
//				: damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2D;
//		double airMultiplier = damaged.isOnGround() ? 1 : 0.5;
//
//		Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
//		knockback.setX((knockback.getX() * sprintMultiplier + kbMultiplier) * horMultiplier);
//		knockback.setY(0.44d * airMultiplier);
//		knockback.setZ((knockback.getZ() * sprintMultiplier + kbMultiplier) * horMultiplier);
//
//		PlayerKnockbackEvent e = new PlayerKnockbackEvent(damaged, damager, horMultiplier, verMultiplier,
//				sprintMultiplier, kbMultiplier, airMultiplier, knockback);
//		Bukkit.getPluginManager().callEvent(e);
//
//		if (!e.isCancelled()) {
//			damaged.setMetadata("knockback",
//					new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + 100));
//			((CraftPlayer) damaged).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(
//					damaged.getEntityId(), e.getKnockback().getX(), e.getKnockback().getY(), e.getKnockback().getZ()));
//		}
//	}

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
					damage += 0.25 * (effect.getAmplifier() + 1);
					break;
				}

		if (!sword.getEnchantments().isEmpty())
			if (sword.containsEnchantment(Enchantment.DAMAGE_ALL))
				damage += 0.35 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);

		if (isCrital(p)) {
			damage = damage - (danoEspada / 2);
			damage += 0.40;
		}

		if (isMore)
			damage -= 3;

		event.setDamage(damage);
	}

	private boolean isCrital(Player p) {
		return p.getFallDistance() > 0 && !((Entity) p).isOnGround() && !p.hasPotionEffect(PotionEffectType.BLINDNESS);
	}

	private double getDamage(Material type) {
		double damage = 1.0;

		if (type.toString().contains("DIAMOND_")) {
			damage = 6.5;
		} else if (type.toString().contains("IRON_")) {
			damage = 5.5;
		} else if (type.toString().contains("GOLD_")) {
			damage = 5.0;
		} else if (type.toString().contains("STONE_")) {
			damage = 4.5;
		} else if (type.toString().contains("WOOD_")) {
			damage = 4.0;
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
