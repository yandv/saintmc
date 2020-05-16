package tk.yallandev.saintmc.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonPlatform;

public class BukkitPlatform implements CommonPlatform {
	
	@Override
	public UUID getUuid(String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		return player != null ? player.getUniqueId() : null;
	}

	@Override
	public <T> T getPlayerByName(String playerName, Class<T> clazz) {
		Player player = Bukkit.getPlayer(playerName);
		return player != null ? clazz.cast(player) : null;
	}
	
	@Override
	public <T> T getExactPlayerByName(String playerName, Class<T> clazz) {
		Player p = null;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().equals(playerName)) {
				p = player;
				break;
			}
		}
		
		return p != null ? clazz.cast(p) : null;
	}

	@Override
	public <T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz) {
		Player player = Bukkit.getPlayer(uniqueId);
		return player != null ? clazz.cast(player) : null;
	}
	
	@Override
	public void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTask(BukkitMain.getInstance(), runnable);
	}

}
