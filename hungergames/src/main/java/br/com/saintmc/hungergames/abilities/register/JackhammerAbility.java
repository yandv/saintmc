package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class JackhammerAbility extends Ability {
	
	private Map<Player, Integer> useMap;

	public JackhammerAbility() {
		super("Jackhammer", Arrays.asList(new ItemBuilder().name("§aJackHammer").type(Material.STONE_AXE).build()));
		useMap = new HashMap<>();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		if (hasAbility(p) && p.getItemInHand() != null && p.getItemInHand().getType() == Material.STONE_AXE) {
			int x = e.getBlock().getX();
			int z = e.getBlock().getZ();
			
			if (x >= 489 || x <= -489 || z >= 489 || z <= -489) {
				e.setCancelled(true);
				return;
			}
			
			if (isCooldown(p)) {
				return;
			}
			
			if (useMap.containsKey(p)) {
				useMap.put(p, useMap.get(p) + 1);
			} else {
				useMap.put(p, 1);
			}
			
			if (useMap.get(p) == 6) {
				if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					breakBlock(e.getBlock(), BlockFace.UP);
				}
				
				breakBlock(e.getBlock(), BlockFace.DOWN);
				
				useMap.remove(p);
				addCooldown(p.getUniqueId(), 28l);
			} else {
				if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					breakBlock(e.getBlock(), BlockFace.UP);
				}
				
				breakBlock(e.getBlock(), BlockFace.DOWN);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void breakBlock(final Block b, final BlockFace face) {
		new BukkitRunnable() {
			Block block = b;

			public void run() {
				if (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_PORTAL_FRAME && block.getY() <= 128 && !block.hasMetadata("inquebravel")) {
					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 30);
					block.setType(Material.AIR);
					block = block.getRelative(face);
				} else {
					cancel();
				}
			}
		}.runTaskTimer(BukkitMain.getInstance(), 2L, 2L);
	}

}
