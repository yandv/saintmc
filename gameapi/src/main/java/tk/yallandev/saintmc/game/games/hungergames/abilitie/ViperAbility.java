package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class ViperAbility extends Ability implements Disableable {

	public ViperAbility() {
		super(new ItemStack(Material.FERMENTED_SPIDER_EYE), AbilityRarity.COMMON);
		options.put("CHANCE", new CustomOption("CHANCE", new ItemStack(Material.GOLD_NUGGET), -1, 1, 3, 5));
		options.put("DURATION", new CustomOption("DURATION", new ItemStack(Material.FERMENTED_SPIDER_EYE), 1, 3, 5, 10));
	}

	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!(event.getDamager() instanceof Player))
			return;
		Player damager = (Player) event.getDamager();
		if (!hasAbility(damager))
			return;
		Random r = new Random();
		Player damaged = (Player) event.getEntity();
		if (damaged instanceof Player) {
			if (r.nextInt(getOption(damager, "CHANCE").getValue()) == 0) {
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getOption(damager, "DURATION").getValue() * 20, 0));
			}
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return getOption("DURATION", map).getValue() * 3 + (18 - (getOption("CHANCE", map).getValue() * 3));
	}
}
