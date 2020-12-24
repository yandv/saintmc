package tk.yallandev.saintmc.kitpvp.warp.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public abstract class WarpScoreboard implements Listener {

	public final Scoreboard scoreboard;
	@Setter
	public Warp warp;

	private boolean registered;

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		if (event.getWarp() == warp)
			loadScoreboard(event.getPlayer());

		updateScore(UpdateType.PLAYER);
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpQuitEvent event) {
		if (event.getWarp() == warp)
			unloadScoreboard(event.getPlayer());

		updateScore(UpdateType.PLAYER);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		if (event.getWarp() == warp) {
			updateScore(event.getPlayer(), UpdateType.STATUS);

			if (event.getKiller() != null)
				updateScore(event.getKiller(), UpdateType.STATUS);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
		if (GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).getWarp() == warp)
			updateScore(event.getPlayer(), UpdateType.GROUP);
	}

	public void register() {
		if (!registered) {
			Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
			registered = true;
		}
	}

	public void unregister() {
		if (registered) {
			HandlerList.unregisterAll(this);
			registered = false;
		}
	}

	public abstract void loadScoreboard(Player player);

	public abstract void unloadScoreboard(Player player);

	/**
	 * 
	 * Update the score to all
	 * 
	 * @param updateType
	 */

	public abstract void updateScore(UpdateType updateType);

	/**
	 * 
	 * Update the score to a single player
	 * 
	 * @param updateType
	 */

	public abstract void updateScore(Player player, UpdateType updateType);

	/**
	 * 
	 * Update the score to a single player
	 * 
	 * @param updateType
	 */

	public abstract <T> void updateScore(Player player, T t);

	public enum UpdateType {

		/* WARP */

		PLAYER, STATUS, CUSTOM,

		/* GENERAL */

		GROUP, RANK;

	}

}
