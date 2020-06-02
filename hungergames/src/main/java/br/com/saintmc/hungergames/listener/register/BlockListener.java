package br.com.saintmc.hungergames.listener.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

@SuppressWarnings("deprecation")
public class BlockListener extends GameListener {

	private List<Material> blockList = Arrays.asList(Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.COCOA,
			Material.CACTUS, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.PUMPKIN);

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!ServerConfig.getInstance().isPlace()) {
			event.setCancelled(!Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.TRIAL));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!ServerConfig.getInstance().isBuild()) {
			event.setCancelled(!Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.TRIAL));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlock(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (!blockList.contains(event.getBlock().getType()))
			return;

		event.setCancelled(true);
		Player player = event.getPlayer();

		Collection<ItemStack> dropList = new ArrayList<>(event.getBlock().getDrops());

		if (event.getBlock().getType() == Material.COCOA) {
			dropList.add(new ItemStack(Material.INK_SACK, event.getBlock().getData() >= 8 ? 3 : 1, (short) 3));
		} else if (event.getBlock().getType() == Material.CACTUS) {
			dropList.removeIf(item -> item.getType() == Material.CACTUS);
			Block block = event.getBlock();

			while (block.getType() == Material.CACTUS) {
				dropList.add(new ItemStack(Material.CACTUS));

				Block relative = block.getRelative(BlockFace.UP);

				blockBlock(relative);
				block.setType(Material.AIR);
				block = relative;
			}
		}

		for (ItemStack item : dropList) {
			int slot = player.getInventory().first(item.getType());

			if (slot == -1) {
				slot = player.getInventory().firstEmpty();

				if (slot == -1) {
					boolean needDrop = true;

					for (ItemStack itemContent : player.getInventory().getContents()) {
						if (itemContent.getType() == item.getType())
							if (itemContent.getAmount() + item.getAmount() <= 64) {
								player.getInventory().addItem(item);
								needDrop = false;
							} else {

								while (itemContent.getAmount() + item.getAmount() <= 64 && item.getAmount() >= 0) {
									itemContent.setAmount(itemContent.getAmount() + 1);
									item.setAmount(item.getAmount() - 1);
								}

								if (item.getAmount() <= 0) {
									needDrop = false;
								}
							}
					}

					if (needDrop)
						event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), item);
				} else {
					player.getInventory().addItem(item);
				}
			} else {
				if (player.getInventory().getItem(slot).getAmount() + item.getAmount() > 64) {
					slot = player.getInventory().firstEmpty();

					if (slot == -1) {
						boolean needDrop = true;

						for (ItemStack itemContent : player.getInventory().getContents()) {
							if (itemContent.getType() == item.getType())
								if (itemContent.getAmount() + item.getAmount() <= 64) {
									player.getInventory().addItem(item);
									needDrop = false;
								} else {
									while (itemContent.getAmount() + item.getAmount() <= 64 && item.getAmount() >= 0) {
										itemContent.setAmount(itemContent.getAmount() + 1);
										item.setAmount(item.getAmount() - 1);
									}

									if (item.getAmount() <= 0) {
										needDrop = false;
									}
								}
						}

						if (needDrop)
							event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), item);

					} else {
						player.getInventory().addItem(item);
					}
				} else {
					player.getInventory().addItem(item);
				}
			}
		}

		if (event.isCancelled()) {
			blockNearBlocks(event.getBlock());

			event.getBlock().setMetadata("phsicsBlock", new FixedMetadataValue(getGameMain(), "cangaroo"));
			event.getBlock().setType(Material.AIR);
		}
	}

	public void blockNearBlocks(Block block) {
		for (BlockFace blockFace : new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH,
				BlockFace.SOUTH }) {
			blockBlock(block.getRelative(blockFace));
		}
	}

	public void blockBlock(Block block) {
		if (!block.hasMetadata("phsicsBlock"))
			block.setMetadata("phsicsBlock", new FixedMetadataValue(getGameMain(), ""));
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (blockList.contains(event.getBlock().getType()))
			if (event.getBlock().hasMetadata("phsicsBlock"))
				event.setCancelled(true);
	}

}
