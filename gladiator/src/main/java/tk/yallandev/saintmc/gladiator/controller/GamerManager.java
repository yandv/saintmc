package tk.yallandev.saintmc.gladiator.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import tk.yallandev.saintmc.gladiator.gamer.Gamer;

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
	
	public Collection<Gamer> filter(Predicate<? super Gamer> predicate) {
		return gamerMap.values().stream().filter(predicate).collect(Collectors.toList());
	}
	
	public Collection<Gamer> getGamers() {
		return gamerMap.values();
	}

}
