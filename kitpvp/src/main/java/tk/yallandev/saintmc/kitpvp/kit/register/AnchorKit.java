package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class AnchorKit extends Kit {

	public AnchorKit() {
		super("Anchor", "Se prenda ao chão e não saia dele", Material.ANVIL, new ArrayList<>());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if (e.getDamager() instanceof Player) {
				Player d = (Player) e.getDamager();

				if (hasAbility(p) || hasAbility(d)) {
					p.getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);
					
					if (e.getDamage() < ((Damageable) p).getHealth()) {
						e.setCancelled(true);
						p.damage(e.getFinalDamage());
					}
				}
			}
		}
	}

}
