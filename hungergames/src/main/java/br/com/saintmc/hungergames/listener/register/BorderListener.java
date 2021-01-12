package br.com.saintmc.hungergames.listener.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.GameListener;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class BorderListener extends GameListener {

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying())
					continue;
				
				if (isOnWarning(player)) {
					if (!isPregame()) {
						player.sendMessage("§eVocê está perto da borda do mundo!");
						return;
					}
				}

				if (isNotInBoard(player) || player.getLocation().getY() > 155) {
					if (isPregame()) {
						if (player.getLocation().getY() > 155)
							player.setVelocity(player.getLocation().toVector().setX(0).setZ(0).setY(80)
									.subtract(player.getLocation().toVector()).normalize().multiply(1.2));
						else
							player.setVelocity(player.getLocation().toVector().setX(0).setZ(0)
									.subtract(player.getLocation().toVector()).normalize().multiply(1.2));
					} else {
						player.sendMessage("§cVocê passou da borda do mundo!");

						@SuppressWarnings("deprecation")
						EntityDamageEvent entityDamageEvent = new EntityDamageEvent(player, DamageCause.CUSTOM, 4.0d);

						if (entityDamageEvent.isCancelled())
							entityDamageEvent.setCancelled(false);

						player.setLastDamageCause(entityDamageEvent);
						player.damage(4.0);
						player.setFireTicks(50);
					}
				}
			}
	}

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		if (event.getToState() == GameState.WINNING)
			HandlerList.unregisterAll(this);
	}

	private boolean isNotInBoard(Player p) {
		int size = (int) 1000 / 2;
		return ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
				|| (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
	}

	private boolean isOnWarning(Player p) {
		int size = (int) 1000 / 2;
		size = size - 20;
		return !isNotInBoard(p) && ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
				|| (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
	}

}
