package br.com.battlebits.game.games.hungergames.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameStageChangeEvent;
import tk.yallandev.saintmc.game.stage.GameStage;

public class InvincibilityListener extends GameListener {

	public InvincibilityListener(GameMain main) {
		super(main);
	}

	private boolean isInvincibility() {
		return GameStage.isInvincibility(getGameMain().getGameStage());
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onGameStageChange(GameStageChangeEvent event) {
		if (GameStage.isInvincibility(event.getLastStage())) {
			if (!GameStage.isInvincibility(event.getNewStage())) {
				HandlerList.unregisterAll(this);
			}
		}
	}
	
}