package tk.yallandev.saintmc.bukkit.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveUpdateEvent extends PlayerMoveEvent {

    public PlayerMoveUpdateEvent(Player player, Location from, Location to) {
        super(player, from, to);
    }

}
