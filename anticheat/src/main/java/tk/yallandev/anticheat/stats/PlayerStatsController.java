package tk.yallandev.anticheat.stats;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.common.controller.StoreController;

public class PlayerStatsController extends StoreController<UUID, PlayerStats> {
	
	public PlayerStatsController() {
		setDefaultFunction(new Function<UUID, PlayerStats>() {
			
			@Override
			public PlayerStats apply(UUID uniqueId) {
				return new PlayerStats(uniqueId);
			}
		});
	}
	
	public PlayerStats getPlayerStats(UUID key) {
		return super.getValue(key);
	}

	public PlayerStats getPlayerStats(Player player) {
		return super.getValue(player.getUniqueId());
	}


}
