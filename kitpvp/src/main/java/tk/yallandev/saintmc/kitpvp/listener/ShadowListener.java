package tk.yallandev.saintmc.kitpvp.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.SearchingStartEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;

public class ShadowListener implements Listener {
	
	private Map<Player, Long> cooldownMap;
	
	private Title shadowNormalTitle;
	private Title shadowFastTitle;
	
	public ShadowListener() {
		cooldownMap = new HashMap<>();
		shadowNormalTitle = new SimpleTitle("§a§l1v1", "§fDuelo encontrado!");
		shadowFastTitle = new SimpleTitle("§a§l1v1", "§fDuelo encontrado!");
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
		
		switch (event.getChallengeType()) {
		case SHADOW_FAST: {
			shadowNormalTitle.send(event.getPlayer());
			shadowNormalTitle.send(event.getTarget());
			break;
		}
		case SHADOW_CUSTOM:
		case SHADOW_NORMAL: {
			shadowFastTitle.send(event.getPlayer());
			shadowFastTitle.send(event.getTarget());
			break;
		}
		case SUMO_NORMAL: {
			break;
		}
		}
		
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
