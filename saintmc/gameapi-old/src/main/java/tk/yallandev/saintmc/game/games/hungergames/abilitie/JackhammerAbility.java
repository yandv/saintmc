package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.interfaces.Disableable;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class JackhammerAbility extends Ability implements Disableable {

	public JackhammerAbility() {
		super(new ItemStack(Material.STONE_AXE), AbilityRarity.COMMON);
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), -1, 25, 30, 35));
		options.put("ITEM", new CustomOption("ITEM", new ItemBuilder().type(Material.STONE_AXE).build(), "§aJackHammer"));
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
			
			if (this.jackHammeruses.containsKey(p)) {
				this.jackHammeruses.put(p, this.jackHammeruses.get(p) + 1);
			} else {
				this.jackHammeruses.put(p, 1);
			}
			
			if (this.jackHammeruses.get(p) == 6) {
				if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					quebrar(e.getBlock(), BlockFace.UP);
				} else {
					quebrar(e.getBlock(), BlockFace.DOWN);
				}
				this.jackHammeruses.remove(p);
				CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
			} else {
				if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					quebrar(e.getBlock(), BlockFace.UP);
				} else {
					quebrar(e.getBlock(), BlockFace.DOWN);
				}
			}
		}
	}

	private void quebrar(final Block b, final BlockFace face) {
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

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 0;
	}
	
}
