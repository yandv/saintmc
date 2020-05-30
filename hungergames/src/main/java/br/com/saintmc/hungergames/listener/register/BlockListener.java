package br.com.saintmc.hungergames.listener.register;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;

@SuppressWarnings("deprecation")
public class BlockListener extends GameListener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!ServerConfig.getInstance().isPlace()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!ServerConfig.getInstance().isBuild()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlock(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		Collection<ItemStack> dropList = new ArrayList<>(event.getBlock().getDrops());

		if (event.getBlock().getType() == Material.COCOA) {
			dropList.add(new ItemStack(Material.INK_SACK, event.getBlock().getData() >= 8 ? 3 : 1, (short) 3));
			event.setCancelled(true);
		} else if (event.getBlock().getType() == Material.CACTUS) {
			
			Block block = event.getBlock().getRelative(BlockFace.UP);
			
			while (block.getType() == Material.CACTUS) {
				dropList.add(new ItemStack(Material.CACTUS));
				block = block.getRelative(BlockFace.UP);
			}
			
			event.setCancelled(true);
		} else if (event.getBlock().getType().name().contains("_MUSHROOM")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
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

		event.getBlock().getDrops().clear();
	}

}
