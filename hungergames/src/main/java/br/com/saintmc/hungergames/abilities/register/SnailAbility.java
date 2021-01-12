package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class SnailAbility extends Ability {

	public SnailAbility() {
		super("Snail", new ArrayList<>());
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = event.getDamager();

		if (!hasAbility(damager))
			return;

		Random r = new Random();
		Player damaged = event.getPlayer();

		if (r.nextInt(3) == 0) {
			damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
		}
	}
}
