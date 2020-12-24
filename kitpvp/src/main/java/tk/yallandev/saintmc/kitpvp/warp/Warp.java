
package tk.yallandev.saintmc.kitpvp.warp;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.WarpScoreboard;

/**
 * 
 * @author yandv
 *
 */

@Getter
public abstract class Warp extends ManualRegisterableListener implements CommandClass {

	private String name;

	@Setter
	private Location spawnLocation;
	@Setter
	private double spawnRadius;

	private WarpSetting warpSettings = new WarpSetting();
	private WarpScoreboard scoreboard;
	
	private Set<UUID> players;

	public Warp(String name, Location location, WarpScoreboard warpScoreboard) {
		this.name = name;

		this.spawnLocation = location;
		this.spawnRadius = 10;
		this.scoreboard = warpScoreboard;
		
		this.players = new HashSet<>();
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
					.count() - 1 == 0l) {
				unregisterListener();
				scoreboard.unregister();
			}
	}
	
	public abstract ItemStack getItem();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Warp))
			return false;
		return ((Warp) obj).getId().equals(this.getId());
	}

}