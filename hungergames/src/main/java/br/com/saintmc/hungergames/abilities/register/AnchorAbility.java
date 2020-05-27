package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;

public class AnchorAbility extends Ability {

	public AnchorAbility() {
		super("Anchor", new ArrayList<>());
	}

	@EventHandler
	public void onPlayerDamagePlayerListener(EntityDamageByEntityEvent e) {
		if (GameGeneral.getInstance().getGameState() != GameState.GAMETIME)
			return;

		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if (e.getDamager() instanceof Player) {
				Player d = (Player) e.getDamager();

				if (hasAbility(p) || hasAbility(d)) {
					if (!GameGeneral.getInstance().getGamerController().getGamer(p).isNotPlaying()
							&& !GameGeneral.getInstance().getGamerController().getGamer(d).isNotPlaying()) {
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
}
