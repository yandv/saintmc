package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.ability.PlayerStompedEvent;

public class StomperAbility extends Ability {

	public StomperAbility() {
		super("Stomper", new ArrayList<>());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		if (event.getDamage() < 4.0D)
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		Player player = (Player) event.getEntity();

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());

		if (gamer.isGamemaker() || gamer.isSpectator())
			return;

		if (hasAbility(player)) {
			double dmg = event.getDamage();

			for (Player stomped : Bukkit.getOnlinePlayers()) {
				if (stomped.getUniqueId() == player.getUniqueId() || stomped.isDead())
					continue;

				if (stomped.getLocation().distance(player.getLocation()) > 5)
					continue;

				if (stomped.isSneaking() && dmg > 8)
					dmg = 8;

				PlayerStompedEvent playerStomperEvent = new PlayerStompedEvent(stomped, player);
				Bukkit.getPluginManager().callEvent(playerStomperEvent);

				if (!playerStomperEvent.isCancelled()) {
					stomped.setMetadata("ignoreDamage",
							new FixedMetadataValue(GameMain.getInstance(), System.currentTimeMillis() + 1000));

					stomped.damage(0.1D, player);
					stomped.damage(dmg);
				}
			}

			player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);

			if (event.getDamage() > 4.0D)
				event.setDamage(4.0d);
		}
	}

}
