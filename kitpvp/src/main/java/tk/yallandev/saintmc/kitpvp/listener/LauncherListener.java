package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;

public class LauncherListener implements Listener {

	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Material type = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();

		boolean noFall = false;

		if (type == Material.DIAMOND_BLOCK) {
			player.setVelocity(player.getLocation().getDirection().multiply(0).setY(2.5));
			noFall = true;
		} else if (type == Material.SPONGE) {
			player.setVelocity(player.getLocation().getDirection().multiply(0).setY(4));
			noFall = true;
		}

		if (noFall) {
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 6.0F, 1.0F);
			player.setMetadata("nofall",
					new FixedMetadataValue(GameMain.getInstance(), System.currentTimeMillis() + 5000l));
			player.setMetadata("anticheat-bypass",
					new FixedMetadataValue(GameMain.getInstance(), System.currentTimeMillis() + 5000l));
		}
	}

}
