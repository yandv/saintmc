package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class FiremanKit extends Kit {

	public FiremanKit() {
		super("Fireman", "Não tome dano para fogo nem lava\n\n§aAdicionado recentemente!", Material.LAVA_BUCKET, 12000,
				new ArrayList<>());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!hasAbility(player))
			return;

		if (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK)
			event.setCancelled(true);
	}

}
