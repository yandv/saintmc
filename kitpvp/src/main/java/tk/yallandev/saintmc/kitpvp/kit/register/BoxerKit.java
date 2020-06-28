package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class BoxerKit extends Kit {

	public BoxerKit() {
		super("Boxer", "Vire um boxeador e esteja acustumado a receber pancadas e a revida-las", Material.STONE_SWORD,
				12000, new ArrayList<>());
	}

	@EventHandler
	public void onBoxer(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player damager = (Player) event.getDamager();

		if (!hasAbility(damager))
			return;

		if (damager.getItemInHand().getType() == Material.AIR) {
			event.setDamage(event.getDamage() + 2);
		}
	}

	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player damaged = (Player) event.getEntity();

		if (!hasAbility(damaged))
			return;

		if (event.getDamage() - 1 >= 1)
			event.setDamage(event.getDamage() - 1);
		else
			event.setDamage(1);
	}

}
