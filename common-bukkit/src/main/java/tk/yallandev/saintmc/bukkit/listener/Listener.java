package tk.yallandev.saintmc.bukkit.listener;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.server.Server;

@Getter
public class Listener implements org.bukkit.event.Listener {
	
	private BukkitMain main;
	
	public Listener() {
		main = BukkitMain.getInstance();
	}
	
	public Server getServerConfig() {
		return main.getServerConfig();
	}

}
