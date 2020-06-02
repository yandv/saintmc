package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MinerAbility extends Ability {

	public MinerAbility() {
		super("Miner", Arrays.asList(getMinerPickaxe()));
//		options.put("ITEM", new CustomOption("ITEM", getMinerPickaxe(), "§aPicareta do Minerador"));
	}
	
	private static ItemStack getMinerPickaxe() {
		return new ItemBuilder().type(Material.STONE_PICKAXE).glow().enchantment(Enchantment.DURABILITY).enchantment(Enchantment.DIG_SPEED, 2).build();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (hasAbility(e.getPlayer()) && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().name().contains("PICKAXE")) {
			Material mat = e.getBlock().getType();
			
			if (!mat.name().contains("ORE"))
				return;
			
			e.setCancelled(true);
			int minerio = 0;
			
			for (Block b : getNearbyBlocks(e.getBlock(), 5)) {
				if (b.getType() != mat) {
					continue;
				}
				
				minerio++;
				b.setType(Material.AIR);
			}
			
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(mat == Material.COAL_ORE ? Material.COAL : mat, minerio));
		}
	}

	private List<Block> getNearbyBlocks(Block block, int i) {
		List<Block> blocos = new ArrayList<Block>();
		for (int x = -i; x <= i; x++) {
			for (int y = -i; y <= i; y++) {
				for (int z = -i; z <= i; z++) {
					blocos.add(block.getLocation().clone().add(x, y, z).getBlock());
				}
			}
		}
		return blocos;
	}

}