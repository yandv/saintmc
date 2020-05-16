package tk.yallandev.saintmc.kitpvp.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.SearchingStartEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;

public class ShadowListener implements Listener {
	
	private Map<Player, Long> cooldownMap;
	
	public ShadowListener() {
		cooldownMap = new HashMap<>();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSearchingStart(SearchingStartEvent event) {
		Player player = event.getPlayer();
		
		if (isInCooldown(player)) {
			player.sendMessage("§c§l> §fAguarde para entrar na fila do §a1v1 rápido§f novamente!");
			event.setCancelled(true);
			return;
		}
		
		putCooldown(player);
	}
	
	@EventHandler
	public void onFightStart(FightStartEvent event) {
		
	}
	
	@EventHandler
	public void onPlayerWarpQuit(PlayerWarpQuitEvent event) {
		cooldownMap.remove(event.getPlayer());
	}
	
	public boolean isInCooldown(Player player) {
		return cooldownMap.containsKey(player) && cooldownMap.get(player) > System.currentTimeMillis();
	}

	public void putCooldown(Player player) {
		cooldownMap.put(player, System.currentTimeMillis() + 3000);
	}

}
