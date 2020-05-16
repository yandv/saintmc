package tk.yallandev.saintmc.bukkit.api.title;

import org.bukkit.entity.Player;

public interface Title {
	
	void send(Player player);
	
	void reset(Player player);
	
	void broadcast();
	
}
