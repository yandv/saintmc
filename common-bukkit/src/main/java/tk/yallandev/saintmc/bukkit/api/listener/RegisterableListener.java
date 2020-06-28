package tk.yallandev.saintmc.bukkit.api.listener;

import org.bukkit.event.Listener;

public interface RegisterableListener extends Listener {
	
	void registerListener();
	
	void unregisterListener();
	
	boolean isRegistered();
	
}
