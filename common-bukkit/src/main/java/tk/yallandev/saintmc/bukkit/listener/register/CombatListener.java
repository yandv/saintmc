package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;

public class CombatListener extends Listener {

	private Map<UUID, Long> lastTime = new HashMap<>();

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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getDamager();

		double damage = 1.0D;
		ItemStack itemStack = player.getItemInHand();

		if (System.currentTimeMillis()
				- lastTime.computeIfAbsent(player.getUniqueId(), v -> System.currentTimeMillis()) < 50) {
			Bukkit.broadcastMessage(
					"Dano duplicado : " + (System.currentTimeMillis() - lastTime.get(player.getUniqueId())) + " " + player.getName());
			lastTime.put(player.getUniqueId(), System.currentTimeMillis());
		}

		if (itemStack != null) {
			damage = getDamage(itemStack.getType());

			if (!itemStack.getEnchantments().isEmpty()) {
				if (itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS) && isArthropod(event.getEntityType()))
					damage += 1 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);

				if (itemStack.containsEnchantment(Enchantment.DAMAGE_UNDEAD) && isUndead(event.getEntityType()))
					damage += 1 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);

				if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL))
					damage += 0.5 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
			}
		}

		event.setDamage(damage);
	}

	private boolean isArthropod(EntityType type) {
		switch (type) {
		case CAVE_SPIDER:
			return true;
		case SPIDER:
			return true;
		case SILVERFISH:
			return true;
		default:
			return false;
		}
	}

	private boolean isUndead(EntityType type) {
		switch (type) {
		case SKELETON:
			return true;
		case ZOMBIE:
			return true;
		case WITHER_SKULL:
			return true;
		case PIG_ZOMBIE:
			return true;
		default:
			return false;
		}
	}

	private double getDamage(Material type) {
		double damage = 1.0D;

		if (type.toString().contains("DIAMOND_")) {
			damage = 6.0D;
		} else if (type.toString().contains("IRON_")) {
			damage = 5.0D;
		} else if (type.toString().contains("STONE_") || type.toString().contains("GOLD_")) {
			damage = 4.0D;
		} else if (type.toString().contains("WOOD_")) {
			damage = 3.0D;
		}

		if (!type.toString().contains("_SWORD")) {
			damage--;
			if (!type.toString().contains("_AXE")) {
				damage--;
				if (!type.toString().contains("_PICKAXE")) {
					damage--;
					if (!type.toString().contains("_SPADE"))
						damage = 1.0D;
				}
			}
		}
		return damage;
	}
}
