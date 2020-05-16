package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class MoveListener implements Listener {

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);

		if (!gamer.isFlying()) {
				player.setVelocity(player.getLocation().getDirection().multiply(3).setY(0.6));
				player.setAllowFlight(false);
				player.setFlying(false);

				new BukkitRunnable() {

					@Override
					public void run() {
						if (!gamer.isFlying())
							player.setAllowFlight(true);
					}
				}.runTaskLater(BukkitMain.getInstance(), 20 * 2);

				event.setCancelled(true);
			}
	}

}
