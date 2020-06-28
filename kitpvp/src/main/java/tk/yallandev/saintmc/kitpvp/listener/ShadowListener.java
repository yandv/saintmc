package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;

public class ShadowListener implements Listener {
	
	private Title shadowNormalTitle;
	private Title shadowFastTitle;
	
	public ShadowListener() {
		shadowNormalTitle = new SimpleTitle("§a§l1v1", "§fDuelo encontrado!");
		shadowFastTitle = new SimpleTitle("§a§l1v1", "§fDuelo encontrado!");
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
	
}
