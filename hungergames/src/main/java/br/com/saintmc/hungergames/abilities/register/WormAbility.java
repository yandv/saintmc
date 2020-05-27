package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;

public class WormAbility extends Ability {

	public WormAbility() {
		super("Worm", new ArrayList<>());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWorm(BlockDamageEvent event) {
		if (!hasAbility(event.getPlayer()))
			return;

		if (event.getBlock().getType() != Material.DIRT)
			return;

		if (event.getBlock().getData() != 0)
			return;

		Player p = event.getPlayer();

		if (p.getItemInHand() == null)
			return;

		if (p.getItemInHand().getType() != Material.AIR)
			return;

		double dist = event.getBlock().getLocation().distance(p.getWorld().getSpawnLocation());

		if (dist < 500) {
			if (p.getHealth() < 20.0D) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 1, 2));
			} else if (p.getFoodLevel() < 20) {
				p.setFoodLevel(p.getFoodLevel() + 1);
			}
			
			event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND,
					Material.DIRT.getId());
			event.getBlock().setType(Material.AIR);
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5D, 0.0D, 0.5D),
					new ItemStack(Material.DIRT));
		}
	}
}
