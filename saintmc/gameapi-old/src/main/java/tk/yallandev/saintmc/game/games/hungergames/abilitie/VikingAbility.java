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

public class VikingAbility extends Ability implements Disableable {

	public VikingAbility() {
		super(new ItemStack(Material.DIAMOND_AXE), AbilityRarity.EPIC);
	}

	@EventHandler
	public void onDamageViking(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player d = (Player) e.getDamager();
			ItemStack item = d.getItemInHand();
			if (!hasAbility(d))
				return;
			if (item.getType().name().contains("_AXE"))
				e.setDamage(e.getDamage() + 2);
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 30;
	}
}
