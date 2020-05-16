package tk.yallandev.saintmc.game.games.hungergames.structure;

import org.bukkit.Location;

public interface Structure {

	public Location findPlace();

	public void place(Location loc);
}
