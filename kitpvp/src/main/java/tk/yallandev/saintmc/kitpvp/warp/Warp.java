
package tk.yallandev.saintmc.kitpvp.warp;

import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.listener.ScoreboardListener;

/**
 * 
 * @author yandv
 *
 */

@Getter
public abstract class Warp implements Listener, CommandClass {

	private String name;

	@Setter
	private Location spawnLocation;
	@Setter
	private double spawnRadius;

	private WarpSetting warpSettings = new WarpSetting();

	public Warp(String name, Location location) {
		this.name = name;

		this.spawnLocation = location;
		this.spawnRadius = 10;
	}

	public String getId() {
		return this.name.toLowerCase().trim().replace(" ", ".");
	}

	public boolean inWarp(Player player) {
		return GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp() == this;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void removePlayer(PlayerWarpQuitEvent event) {
		if (inWarp(event.getPlayer()))
			if (GameMain.getInstance().getGamerManager().getGamers().stream().filter(gamer -> inWarp(gamer.getPlayer()))
					.collect(Collectors.toList()).isEmpty())
				HandlerList.unregisterAll(this);
	}

	public Scoreboard getScoreboard() {
		return ScoreboardListener.DEFAULT_SCOREBOARD;
	}

	public abstract ItemStack getItem();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Warp))
			return false;
		Warp compare = (Warp) obj;
		return compare.getId().equals(this.getId());
	}

}