package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;

public class CamelAbility extends Ability {
	
	public CamelAbility() {
		super("Camel", new ArrayList<>());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (!hasAbility(player))
			return;
		
		if (event.getTo().getBlock().getBiome().name().contains("DESERT"))
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*6, 0));
	}

}
