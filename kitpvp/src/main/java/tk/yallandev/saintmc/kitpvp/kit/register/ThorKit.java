package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class ThorKit extends Kit {

	private Map<UUID, Long> damageRaio;

	public ThorKit() {
		super("Thor", "Jogue raios em seus inimigos com seu machado", Material.WOOD_AXE, 10500,
				Arrays.asList(new ItemBuilder().name("§aThor").type(Material.WOOD_AXE).build()));
		damageRaio = new HashMap<>();
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();

		if ((event.getEntity() instanceof LightningStrike)) {
			if (damageRaio.containsKey(player.getUniqueId())
					&& damageRaio.get(player.getUniqueId()) < System.currentTimeMillis()) {
				event.setDamage(0.0D);
			} else {
				event.setDamage(6.0D);
				event.getEntity().setFireTicks(200);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (player.getItemInHand() == null)
			return;

		if (!isAbilityItem(player.getItemInHand()))
			return;

		if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR))
			return;

		if (hasAbility(player)) {
			if (isCooldown(player)) {
				return;
			}

			Location loc = player.getTargetBlock((Set<Material>) null, 20).getLocation();
			loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

			damageRaio.put(player.getUniqueId(), System.currentTimeMillis() + 4000l);
			player.getWorld().strikeLightning(loc);

			addCooldown(player, 8l);
		}
	}

}
