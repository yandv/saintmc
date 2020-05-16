package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.interfaces.Disableable;

public class BoxerAbility extends Ability implements Disableable {

	public BoxerAbility() {
		super(new ItemStack(Material.STONE_SWORD), AbilityRarity.COMMON);
		options.put("DAMAGE", new CustomOption("DAMAGE", new ItemStack(Material.STONE_SWORD), 1, 1, 1, 3));
		options.put("REDUCE", new CustomOption("REDUCE", new ItemStack(Material.REDSTONE), 1, 1, 1, 2));
	}

	@EventHandler
	public void onBoxer(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		
		if (!hasAbility(damager))
			return;
		
		if (damager.getItemInHand().getType() == Material.AIR) {
			event.setDamage(event.getDamage() + getOption(damager, "DAMAGE").getValue());
		}
	}
	
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player damaged = (Player) event.getEntity();
		
		if (!hasAbility(damaged))
			return;
		
		CustomOption REDUCE = getOption(damaged, "REDUCE");
		
		if (event.getDamage() - REDUCE.getValue() >= 1)
			event.setDamage(event.getDamage() - REDUCE.getValue());
		else
			event.setDamage(1);
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return (getOption("DAMAGE", map).getValue() * 5) + (getOption("REDUCE", map).getValue() * 10);
	}
}
