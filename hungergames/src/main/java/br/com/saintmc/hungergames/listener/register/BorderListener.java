package br.com.saintmc.hungergames.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.world.ChunkUnloadEvent;

import br.com.saintmc.hungergames.event.game.GameStateChangeEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.GameListener;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;

public class BorderListener extends GameListener{
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		
		if (isOnWarning(player)) {
			if (!isPregame()) {
				player.sendMessage("§e§l> §fVocê está perto da borda do mundo!");
				return;
			}
		}
		
		if (isNotInBoard(player) || player.getLocation().getY() > 129) {
			if (isPregame()) {
				event.setCancelled(true);
			} else {
				player.sendMessage("§c§l> §fVocê passou da borda do mundo!");
				
				@SuppressWarnings("deprecation")
				EntityDamageEvent e = new EntityDamageEvent(player, DamageCause.CUSTOM, 4.0d);
				
				if (e.isCancelled()) {
					e.setCancelled(false);
				}
				
				player.setLastDamageCause(e);
				player.damage(4.0);
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
