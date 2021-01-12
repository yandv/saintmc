package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;

import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.event.ability.ChallengeGladiatorEvent;
import br.com.saintmc.hungergames.event.ability.ChallengeUltimatoEvent;
import br.com.saintmc.hungergames.event.ability.PlayerAjninTeleportEvent;
import br.com.saintmc.hungergames.event.ability.PlayerEndermageEvent;
import br.com.saintmc.hungergames.event.ability.PlayerNinjaTeleportEvent;

public class NeoAbility extends Ability {

	public NeoAbility() {
		super("neo", new ArrayList<>());
	}

	@EventHandler
	public void onChallengeGladiator(ChallengeGladiatorEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onChallengeUltimato(ChallengeUltimatoEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onChallengeUltimato(PlayerEndermageEvent event) {
		if (hasAbility(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerNinjaTeleport(PlayerNinjaTeleportEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerAjninTeleport(PlayerAjninTeleportEvent event) {
		if (hasAbility(event.getTarget()))
			event.setCancelled(true);
	}

}
