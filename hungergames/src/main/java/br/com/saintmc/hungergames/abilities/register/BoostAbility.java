package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class BoostAbility extends Ability {

	public BoostAbility() {
		super("Booster",
				Arrays.asList(new ItemBuilder().type(Material.LAPIS_BLOCK).name("§aBoost").amount(20).build()));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.LAPIS_BLOCK && isAbilityItem(event.getItemInHand())) {
			Player p = event.getPlayer();

			Direction direction = getDirection(p);

			if (direction == null) {
				event.setCancelled(true);
				p.sendMessage("§cDireção não encontrada!");
				return;
			}

			event.getBlock().setMetadata("direction", new FixedMetadataValue(GameMain.getInstance(), direction));
			p.sendMessage("§aO seu boost está mirando para o " + NameUtils.formatString(direction.name()));
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Block relative = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

		if (relative.getType() == Material.LAPIS_BLOCK) {
			if (relative.hasMetadata("direction")) {
				Direction direction = (Direction) relative.getMetadata("direction").stream().findFirst().orElse(null)
						.value();

				Vector v = event.getPlayer().getVelocity();

				float x = 0.0F;
				float y = 0.180f;
				float z = 0.0F;

				switch (direction) {
				case EAST: {
					x = x + 2.25F;
					break;
				}
				case NORTH: {
					z = z - 2.25F;
					break;
				}
				case SOUTH: {
					z = z + 2.25F;
					break;
				}
				case WEST: {
					x = x - 2.25F;
					break;
				}
				}

				v.setX(x);
				v.setY(y);
				v.setZ(z);
				player.setVelocity(v);
			}
		}
	}

	public static Direction getDirection(Player player) {
		float yaw = player.getLocation().getYaw();
		if (yaw < 0) {
			yaw += 360;
		}
		if (yaw >= 315 || yaw < 45) {
			return Direction.SOUTH;
		} else if (yaw < 135) {
			return Direction.WEST;
		} else if (yaw < 225) {
			return Direction.NORTH;
		} else if (yaw < 315) {
			return Direction.EAST;
		}
		return Direction.NORTH;
	}

	private enum Direction {

		SOUTH, EAST, WEST, NORTH;

	}

}
