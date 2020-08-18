package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;

public class LauncherListener implements Listener {

	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Material type = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();

		if (type == Material.SLIME_BLOCK) {
			player.setVelocity(player.getLocation().getDirection().multiply(1.8F).setY(0.7F));
			player.setFallDistance(-1.0F);
		}
	}

}
