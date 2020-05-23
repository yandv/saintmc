package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class IronmanAbility extends Ability implements Disableable {
	
	public IronmanAbility() {
		super(new ItemStack(Material.IRON_INGOT), AbilityRarity.LEGENDARY);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity().getKiller() instanceof Player && hasAbility(e.getEntity().getKiller())) {
			Player killer = e.getEntity().getKiller();
			Gamer gamer = Gamer.getGamer(killer);
			
			if (killer.getInventory().firstEmpty() == -1) {
				killer.getWorld().dropItemNaturally(killer.getLocation(), new ItemStack(Material.IRON_INGOT, gamer.getMatchkills() + 1));
			} else {
				killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT, gamer.getMatchkills() + 1));
			}
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 0;
	}

}
