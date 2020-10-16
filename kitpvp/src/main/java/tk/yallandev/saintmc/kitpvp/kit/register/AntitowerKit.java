package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.kitpvp.event.kit.stomper.PlayerStompedEvent;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class AntitowerKit extends Kit {

	public AntitowerKit() {
		super("Antitower", "Não seja stompado\n\n§aAdicionado recentemente!", Material.GOLD_HELMET, 22000,
				new ArrayList<>());
	}

	@EventHandler
	public void onPlayerStomped(PlayerStompedEvent event) {
		if (hasAbility(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().damage(4.0d);
		}
	}

}
