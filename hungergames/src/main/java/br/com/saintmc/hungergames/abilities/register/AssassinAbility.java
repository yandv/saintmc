package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;

public class AssassinAbility extends Ability {

	private Map<UUID, Integer> assassinMap;

	public AssassinAbility() {
		super("Assassin", new ArrayList<>());
		assassinMap = new HashMap<>();
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();

		if (GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying())
			return;

		if (hasAbility(player)) {
			int hits = assassinMap.computeIfAbsent(player.getUniqueId(), v -> 0) + 1;

			if (hits == 5) {
				assassinMap.remove(player.getUniqueId());
				event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.STEP_SOUND,
						Material.REDSTONE_BLOCK);
				event.setDamage(event.getDamage() + 1d);
				return;
			}

			assassinMap.put(player.getUniqueId(), hits);
		}
	}

}
