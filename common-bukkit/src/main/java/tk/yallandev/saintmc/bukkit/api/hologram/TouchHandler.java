package tk.yallandev.saintmc.bukkit.api.hologram;

import org.bukkit.entity.Player;

/**
 * Class to watch a Hologram Touch
 *
 * @author yandv
 */

public interface TouchHandler<T> {

    public void onTouch(T type, Player player, TouchType touchType);

    public enum TouchType {

        LEFT,
        RIGHT;
    }
}
