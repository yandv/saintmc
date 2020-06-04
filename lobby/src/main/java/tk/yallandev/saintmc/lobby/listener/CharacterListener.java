package tk.yallandev.saintmc.lobby.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.github.juliarn.npc.NPC;

import tk.yallandev.saintmc.bukkit.api.character.Character;
import tk.yallandev.saintmc.bukkit.api.character.Character.Interact;
import tk.yallandev.saintmc.lobby.menu.server.HungergamesInventory;

public class CharacterListener implements Listener {

	public CharacterListener() {
		new Character("§aHungerGames", UUID.fromString("fa1a1461-8e39-4536-89ba-6a54143ddaeb"), new Location(Bukkit.getWorld("world"), 3, 118, 18, 180f, 0f), new Interact() {
			
			@Override
			public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
				new HungergamesInventory(player);
				return false;
			}
		});
	}

}
