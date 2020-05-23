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
