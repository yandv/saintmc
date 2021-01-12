package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.CommonConst;

public class CannibalAbility extends Ability {

	public CannibalAbility() {
		super("Cannibal", new ArrayList<>());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity))
			return;

		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();

			if (hasAbility(player)) {
				if (CommonConst.RANDOM.nextInt(100) <= 20) {
					int fome = player.getFoodLevel();
					fome++;

					if (fome <= 20) {
						player.setFoodLevel(fome);

						if (event.getEntity() instanceof Player) {
							((Player) event.getEntity()).addPotionEffect(PotionEffectType.HUNGER.createEffect(60, 0));
						}
					}
				}
			}
		}
	}

}
