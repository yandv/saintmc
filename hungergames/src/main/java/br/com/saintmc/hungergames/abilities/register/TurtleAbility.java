package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.saintmc.hungergames.abilities.Ability;

public class TurtleAbility extends Ability {

	private List<DamageCause> damageList;

	public TurtleAbility() {
		super("Turtle", new ArrayList<>());
		damageList = new ArrayList<>();

		damageList.add(DamageCause.FALL);
		damageList.add(DamageCause.BLOCK_EXPLOSION);
		damageList.add(DamageCause.ENTITY_EXPLOSION);
		damageList.add(DamageCause.LAVA);
		damageList.add(DamageCause.FIRE_TICK);
		damageList.add(DamageCause.LIGHTNING);
		damageList.add(DamageCause.FIRE);
		damageList.add(DamageCause.MAGIC);
		damageList.add(DamageCause.VOID);
		damageList.add(DamageCause.WITHER);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!hasAbility(player))
			return;

		if (player.isSneaking()) {
			if (damageList.contains(event.getCause()))
				event.setDamage(2.0D);
			else
				event.setDamage(event.getDamage() / 2.0D);
		}
	}

}
