package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KangarooKit extends Kit {

	private List<UUID> jumpList = new ArrayList<>();

	public KangarooKit() {
		super("Kangaroo", "Use o seu foguete para movimentar-se mais rapidamente pelo mapa", Material.FIREWORK,
				Arrays.asList(new ItemBuilder().name("§aKangaroo").type(Material.FIREWORK).build()));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.getAction() != Action.PHYSICAL && isAbilityItem(event.getItem())) {
			event.setCancelled(true);

			if (jumpList.contains(player.getUniqueId()))
				return;

			if (isCooldown(player))
				return;

			Vector vector = player.getEyeLocation().getDirection();
			if (player.isSneaking()) {
				vector = vector.multiply(1.8F).setY(0.5F);
			} else {
				vector = vector.multiply(0.5F).setY(1.0F);
			}
			player.setFallDistance(-1.0F);
			player.setVelocity(vector);
			jumpList.add(player.getUniqueId());
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (jumpList.contains(e.getPlayer().getUniqueId())
				&& (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR
						|| e.getPlayer().isOnGround()))
			jumpList.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(PlayerDamagePlayerEvent event) {
		if (hasAbility(event.getPlayer()))
			addCooldown(event.getPlayer(), 5l);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && hasAbility((Player) e.getEntity())
				&& e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getDamage() > 7.0D)
			e.setDamage(7.0D);
	}

}