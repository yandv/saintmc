package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class AladdinAbility extends Ability {

	public AladdinAbility() {
		super("Aladdin", Arrays.asList(new ItemBuilder().type(Material.CARPET).name("§aAladdin").build()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		if (hasAbility(player) && isAbilityItem(event.getItem())) {
			event.setCancelled(true);

			if (isCooldown(player))
				return;

			@SuppressWarnings("deprecation")
			FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(player.getLocation(), Material.CARPET,
					(byte) 4);

			fallingBlock.setDropItem(false);
			fallingBlock.setPassenger(player);

			fallingBlock.setVelocity(player.getVelocity().clone().multiply(0.1d).setY(0.17D * 7)
					.add(player.getEyeLocation().getDirection().normalize().multiply(1.5D)));
			player.setMetadata("nofall",
					new FixedMetadataValue(GameMain.getInstance(), System.currentTimeMillis() + 5000l));

			new BukkitRunnable() {

				@Override
				public void run() {
					fallingBlock.remove();
				}
			}.runTaskLater(GameMain.getInstance(), 20 * 6);

			addCooldown(player, 30l);
		}

	}

}
