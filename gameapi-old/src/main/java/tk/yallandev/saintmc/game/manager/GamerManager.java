package tk.yallandev.saintmc.game.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tk.yallandev.saintmc.game.constructor.Gamer;

public class GamerManager {

	private Map<UUID, Gamer> gamers;

	public GamerManager() {
		gamers = new HashMap<>();
	}

	public void addGamer(Gamer gamer) {
		gamers.put(gamer.getUniqueId(), gamer);
	}

	public Collection<Gamer> getGamers() {
		return gamers.values();
	}

	public Gamer getGamer(UUID uuid) {
		return gamers.get(uuid);
	}

	public void removeGamer(UUID uuid) {
		gamers.remove(uuid);
	}
}
