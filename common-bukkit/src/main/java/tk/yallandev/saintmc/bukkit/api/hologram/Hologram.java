package tk.yallandev.saintmc.bukkit.api.hologram;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Hologram {
	
	/*
	 * Only Hologram
	 */
	
	void spawn();
	
	void setDisplayName(String displayName);
	
	String getDisplayName();
	
	Location getLocation();
	
	void teleport(Location location);
	
	void destroy();
	
	/*
	 * Viewer Information
	 */
	
	void addViewer(Player player);
	
	void removeViewer(Player player);
	
	boolean isViewer(Player player);
	
	boolean locked(Player player);
	
	void lock(Player player, long time);
	
	List<Player> getViewerList();
	
	/*
	 * Line
	 */
	
	Hologram addLine(String displayName);
	
	List<Hologram> getLineList();
	
	boolean isRegistred();
	
}
