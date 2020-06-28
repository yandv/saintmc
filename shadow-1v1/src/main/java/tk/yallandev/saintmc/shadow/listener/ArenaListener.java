package tk.yallandev.saintmc.shadow.listener;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.shadow.event.GladiatorStartEvent;

import java.util.List;

public class ArenaListener implements Listener {

	@EventHandler
	public void onGladiatorStart(GladiatorStartEvent event) {
		Location firstLocation = BukkitMain.getInstance().getLocationFromConfig("first-location");

		event.getChallenge().getEnimy().teleport(firstLocation);

		Location secondLocation = BukkitMain.getInstance().getLocationFromConfig("second-location");

		event.getChallenge().getPlayer().teleport(secondLocation);
	}
}
