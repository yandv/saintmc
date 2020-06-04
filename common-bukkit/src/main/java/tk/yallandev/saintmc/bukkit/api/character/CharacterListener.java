package tk.yallandev.saintmc.bukkit.api.character;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.juliarn.npc.event.PlayerNPCInteractEvent;

public class CharacterListener implements Listener {
	
	@EventHandler
	public void onPlayerNPCInteract(PlayerNPCInteractEvent event) {
		Player player = event.getPlayer();
		
		Character character = Character.getCharacter(event.getNPC().getEntityId());
		
		if (character != null) {
			character.getInteractHandler().onInteract(player, event.getNPC(), event.getAction());
		}
	}

}
