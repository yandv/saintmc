package tk.yallandev.saintmc.bukkit.api.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import tk.yallandev.saintmc.bukkit.BukkitMain;

public class ManualRegisterableListener implements RegisterableListener {
	
	private boolean registered;
	
	@Override
	public void registerListener() {
		
		if (!registered) {
			Bukkit.getPluginManager().registerEvents(this, BukkitMain.getInstance());
			registered = true;
		}
		
	}

	@Override
	public void unregisterListener() {
		if (registered) {
			HandlerList.unregisterAll(this);
			registered = false;
		}
	}

}
