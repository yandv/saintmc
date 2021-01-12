package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class VikingAbility extends Ability {

	public VikingAbility() {
		super("Viking", new ArrayList<>());
	}

	@EventHandler
	public void onEntityDamageByEntity(PlayerDamagePlayerEvent event) {
		Player player = (Player) event.getDamager();
		ItemStack item = player.getItemInHand();

		if (hasAbility(player))
			if (item.getType().name().contains("_AXE"))
				event.setDamage(event.getDamage() + 1.0d);
	}
}
