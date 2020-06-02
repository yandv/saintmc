package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.GameState;

public class HulkAbility extends Ability {
	
	public HulkAbility() {
		super("Hulk", new ArrayList<>());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(p);
		
		if (hasAbility(p) && (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) && event.getRightClicked() instanceof Player && p.getPassenger() == null && !p.isInsideVehicle() && event.getRightClicked().getPassenger() == null && !event.getRightClicked().isInsideVehicle()) {
			if (GameState.isInvincibility(GameGeneral.getInstance().getGameState())) {
				return;
			}
			
			if (gamer.isNotPlaying())
				return;
			
			if (isCooldown(p)) {
				return;
			}
			
			p.setPassenger(event.getRightClicked());
			addCooldown(p.getUniqueId(), 12l);
		}
	}
}
