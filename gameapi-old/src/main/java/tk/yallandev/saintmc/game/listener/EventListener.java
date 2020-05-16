package tk.yallandev.saintmc.game.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.game.GameMain;

public class EventListener extends GameListener {

	public EventListener(GameMain main) {
		super(main);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		getGameMain().checkTimer();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		getGameMain().checkTimer();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAdminMode(PlayerAdminModeEvent event) {
		getGameMain().checkTimer();
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		e.setCancelled(true);
	}

}
