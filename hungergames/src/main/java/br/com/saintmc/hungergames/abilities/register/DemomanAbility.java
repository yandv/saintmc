package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class DemomanAbility extends Ability {

	public DemomanAbility() {
		super("Demoman", Arrays.asList(new ItemBuilder().type(Material.GRAVEL).name("§aDemoman").amount(6).build(),
				new ItemBuilder().type(Material.STONE_PLATE).name("§aDemoman").amount(6).build()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() == Material.STONE_PLATE)
			if (block.hasMetadata("demoman"))
				block.removeMetadata("demoman", GameMain.getInstance());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!hasAbility(player))
			return;

		if (block.getType() == Material.STONE_PLATE)
			block.setMetadata("demoman",
					new FixedMetadataValue(GameMain.getInstance(), player.getUniqueId().toString()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		if (block == null)
			return;

		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId()).isPlaying())
			if (block.hasMetadata("demoman"))
				if (block.getRelative(BlockFace.DOWN).getType() == Material.GRAVEL)
					if (event.getAction() == Action.PHYSICAL) {
						String playerId = block.getMetadata("demoman").stream().findFirst().orElse(null).asString();

						if (UUID.fromString(playerId).equals(event.getPlayer().getUniqueId())) {
							event.getPlayer().sendMessage("§cVocê não pode usar sua própria armadilha!");
							return;
						}

						block.getWorld().createExplosion(block.getLocation().clone().add(0.5D, 0.5D, 0.5D), 3.0F);
					}
	}

}
