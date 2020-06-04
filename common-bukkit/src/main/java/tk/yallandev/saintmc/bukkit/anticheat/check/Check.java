package tk.yallandev.saintmc.bukkit.anticheat.check;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public abstract class Check implements Listener {

	private Map<Player, FutureCallback<CheckLevel>> myPlayers;

	public Check() {
		myPlayers = new HashMap<>();
	}

	public void verify(Player player, FutureCallback<CheckLevel> callback) {
		addPlayer(player, callback);
	}
	
	public boolean isPlayer(Player player) {
		return myPlayers.containsKey(player);
	}

	public void addPlayer(Player player, FutureCallback<CheckLevel> callback) {
		boolean register = myPlayers.isEmpty();
		
		if (!myPlayers.containsKey(player))
			myPlayers.put(player, callback);
		
		System.out.println(register);
		
		if (register)
			Bukkit.getPluginManager().registerEvents(this, BukkitMain.getInstance());
	}
	
	public FutureCallback<CheckLevel> getPlayerCallback(Player player) {
		return myPlayers.get(player);
	}

	public void removePlayer(Player player) {
		if (myPlayers.containsKey(player))
			myPlayers.remove(player);
		
		if (myPlayers.isEmpty())
			HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
		System.out.println("bigode");
	}

	public enum CheckLevel {

		NO_CHANCE, MAYBE;

	}

}
