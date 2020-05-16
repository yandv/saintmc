package tk.yallandev.saintmc.kitpvp.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tk.yallandev.saintmc.kitpvp.gamer.Gamer;

public class GamerManager {
	
	private Map<UUID, Gamer> gamerMap;
	
	public GamerManager() {
		gamerMap = new HashMap<>();
	}
	
	public void loadGamer(UUID uuid, Gamer gamer) {
		if (gamerMap.containsKey(uuid))
			return;
		
		gamerMap.put(uuid, gamer);
	}
	
	public void unloadGamer(UUID uuid) {
		if (!gamerMap.containsKey(uuid))
			return;
		
		gamerMap.remove(uuid);
	}
	
	public Gamer getGamer(UUID uuid) {
		return gamerMap.get(uuid);
	}
	
	public Collection<Gamer> getGamers() {
		return gamerMap.values();
	}

}
