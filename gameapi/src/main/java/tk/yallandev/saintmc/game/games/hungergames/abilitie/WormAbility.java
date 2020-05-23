package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class WormAbility extends Ability implements Disableable {

	public WormAbility() {
		super(new ItemStack(Material.DIRT), AbilityRarity.COMMON);
		options.put("REGENERATION",
				new CustomOption("REGENERATION", new ItemStack(Material.POTION, 1, (byte) 8193), 1, 0, 1, 1));
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
				if (getOption(p, "REGENERATION").getValue() > 0)
					p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
							20 * getOption(p, "REGENERATION").getValue(), 2));
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

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 10 + (getOption("REGENERATION", map).getValue() * 5);
	}
}
