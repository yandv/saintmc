package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class AchillesAbility extends Ability implements Disableable {

	public AchillesAbility() {
		super(new ItemStack(Material.WOOD_SWORD), AbilityRarity.LEGENDARY);
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		Player entity = (Player) event.getEntity();
		
		if (!hasAbility(entity))
			return;
		
		if (damager.getItemInHand() == null)
			return;
		
		ItemStack item = damager.getItemInHand();
		
		if (item.getType().toString().contains("WOOD_")) {
			event.setDamage(event.getDamage() + 3.0D);
		} else {
			event.setDamage(event.getDamage() - 2.0D);
		}
	}
	
	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 18;
	}

}
