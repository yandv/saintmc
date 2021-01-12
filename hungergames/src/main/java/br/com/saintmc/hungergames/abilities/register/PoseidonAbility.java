package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;

public class PoseidonAbility extends Ability {

	public PoseidonAbility() {
		super("Poseidon", new ArrayList<>());
	}
	
	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		
		if (hasAbility(player)) {
			if (player.getLocation().getBlock().getType().name().contains("WATER")) {
				player.addPotionEffect(PotionEffectType.SPEED.createEffect(20*5, 0));
				player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(20*5, 0));
			}
		}
	}

}
