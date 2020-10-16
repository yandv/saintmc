package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class SwitcherKit extends Kit {

	public SwitcherKit() {
		super("Switcher", "Troque de lugar com seus inimigos com sua bola de neve", Material.SNOW_BALL, 12500,
				Arrays.asList(new ItemBuilder().name("§aSwitcher").type(Material.SNOW_BALL).amount(16).build()));
	}

	@EventHandler
	public void onProjectileLaunch(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (hasAbility(player) && isAbilityItem(player.getItemInHand())) {
				event.setCancelled(true);
				player.updateInventory();

				if (isCooldown(player))
					return;

				Snowball ball = player.launchProjectile(Snowball.class);
				ball.setShooter(player);
				ball.setVelocity(player.getLocation().getDirection().multiply(1.5));

				ball.setMetadata("switch", new FixedMetadataValue(GameMain.getInstance(), player));
				ball.setVelocity(player.getVelocity().multiply(1.5D));
				addCooldown(player, 5);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
			Player player = (Player) event.getDamager().getMetadata("switch").get(0).value();

			if (player == null)
				return;

			Location loc = event.getEntity().getLocation().clone();
			event.getEntity().teleport(player.getLocation().clone());
			player.teleport(loc);
		}
	}

}
