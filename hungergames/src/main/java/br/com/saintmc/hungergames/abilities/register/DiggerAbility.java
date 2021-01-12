package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class DiggerAbility extends Ability {

	private static final int RADIUS = 4;
	private static final int HEIGHT = 6;

	public DiggerAbility() {
		super("Digger", Arrays.asList(new ItemBuilder().name("§aDigger").type(Material.DRAGON_EGG).build()));
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && isAbilityItem(event.getPlayer().getItemInHand())) {
			event.setCancelled(true);
			player.updateInventory();

			if (ServerConfig.getInstance().isBuildEnabled() && ServerConfig.getInstance().isPlaceEnabled()) {
				if (event.getClickedBlock() == null)
					return;

				if (isCooldown(player))
					return;

				if (!ServerConfig.getInstance().isBuildEnabled()) {
					player.sendMessage("§cO build está desativado!");
					return;
				}

				for (int x = -RADIUS; x <= RADIUS; x++) {
					for (int z = -RADIUS; z <= RADIUS; z++) {
						for (int y = 0; y <= HEIGHT; y++) {
							Block block = event.getClickedBlock().getLocation().clone().subtract(x, y, z).getBlock();

							if (block.getType() == Material.BEDROCK)
								continue;

							block.setType(Material.AIR);
						}
					}
				}

				player.sendMessage("§aBlocos quebrados!");
				addCooldown(player, 35);
			}
		}
	}

}
