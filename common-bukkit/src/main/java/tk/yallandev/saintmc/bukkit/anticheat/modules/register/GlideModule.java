package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;

public class GlideModule extends Module {

	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();

		if (player.getAllowFlight() || player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SPECTATOR)
			return;

		/*
		 * Checking if the difference between to and from locations is -0.125D and
		 * checking if the under the player has a block with material AIR
		 */

		if (event.getTo().getY() - event.getFrom().getY() == -0.125D
				&& event.getTo().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.AIR)) {
			alert(player);
		}
	}

}
