package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class AchillesAbility extends Ability {

	public AchillesAbility() {
		super("Achilles", new ArrayList<>());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageEvent(PlayerDamagePlayerEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player damager = (Player) event.getDamager();

		if (damager.getItemInHand() == null)
			return;

		Player entity = (Player) event.getPlayer();

		if (hasAbility(entity)) {
			ItemStack item = damager.getItemInHand();

			if (item.getType() == Material.WOOD_PICKAXE)
				event.setDamage(event.getDamage() + 3.0D);
			else if (item.getType() == Material.WOOD_AXE || item.getType() == Material.WOOD_SWORD
					|| item.getType() == Material.WOOD_SPADE)
				event.setDamage(event.getDamage() + 3.5D);
			else {
				if (event.getDamage() - 1.5D <= 0.0)
					event.setDamage(event.getDamage());
				else
					event.setDamage(event.getDamage() - 1.5D);
				damager.sendMessage("§cVocê está batendo em um Achilles, use items de madeira para dar mais dano!");
			}
		}
	}

}
