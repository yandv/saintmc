package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.utils.item.ItemUtils;

public class LauncherAbility extends Ability {

	public LauncherAbility() {
		super("Launcher", Arrays.asList(new ItemBuilder().name("§aLauncher").type(Material.SPONGE).amount(20).build()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player))
			if (event.getBlockPlaced().getType() == Material.SPONGE)
				event.getBlockPlaced().setMetadata("launcher", new FixedMetadataValue(BukkitMain.getInstance(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata("launcher")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);

			ItemUtils.addItem(event.getPlayer(), new ItemBuilder().name("§aLauncher").type(Material.SPONGE).build(),
					event.getPlayer().getLocation());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

		if (block.getType() == Material.SPONGE) {
			if (block.hasMetadata("launcher")) {
				player.setVelocity(player.getLocation().getDirection().multiply(0).setY(3.5));
				player.setMetadata("nofall",
						new FixedMetadataValue(GameMain.getPlugin(), System.currentTimeMillis() + 5000l));
				player.setMetadata("anticheat-bypass",
						new FixedMetadataValue(GameMain.getInstance(), System.currentTimeMillis() + 5000l));
			}
		}
	}

}
