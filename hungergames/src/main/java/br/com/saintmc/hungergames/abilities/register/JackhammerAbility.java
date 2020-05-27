package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class JackhammerAbility extends Ability {

	public JackhammerAbility() {
		super("Jackhammer", Arrays.asList(new ItemBuilder().name("§aJackHammer").type(Material.STONE_AXE).build()));
	}
	
	private HashMap<Player, Integer> jackHammeruses = new HashMap<Player, Integer>();

	@EventHandler
	public void Jack(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (hasAbility(p) && p.getItemInHand() != null && p.getItemInHand().getType() == Material.STONE_AXE) {
			int x = e.getBlock().getX();
			int z = e.getBlock().getZ();
			
			if (x >= 489 || x <= -489 || z >= 489 || z <= -489) {
				e.setCancelled(true);
				return;
			}
			
			if (CooldownAPI.hasCooldown(p.getUniqueId(), getName())) {
				p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
				p.sendMessage(CooldownAPI.getCooldownFormated(p.getUniqueId(), getName()));
				return;
			}
			
			if (jackHammeruses.containsKey(p)) {
				jackHammeruses.put(p, jackHammeruses.get(p) + 1);
			} else {
				jackHammeruses.put(p, 1);
			}
			
			if (jackHammeruses.get(p) == 6) {
				if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					breakBlock(e.getBlock(), BlockFace.UP);
				}
				
				breakBlock(e.getBlock(), BlockFace.DOWN);
				
				jackHammeruses.remove(p);
				CooldownAPI.addCooldown(p.getUniqueId(), getName(), 28l);
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
		}.runTaskTimer(BukkitMain.getInstance(), 5L, 5L);
	}

}
