package tk.yallandev.saintmc.skwyars.controller;

import java.util.UUID;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.common.controller.StoreController;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

public class GamerController extends StoreController<UUID, Gamer> {
	
	public Gamer getGamer(UUID key) {
		return super.getValue(key);
	}
	
	public void loadGamer(Gamer value) {
		super.load(value.getUniqueId(), value);
	}
	
	public boolean unloadGamer(UUID key) {
		return super.unload(key);
	}

	public Gamer getGamer(Player player) {
		if (player == null)
			return null;
		
		return super.getValue(player.getUniqueId());
	}
	
}
