package tk.yallandev.saintmc.bukkit.api.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface Hologram {

    public static final TouchHandler<Hologram> EMPTY_TOUCH_HANDLER = (hologram, player, right) -> {

    };

    public static final double DISTANCE = 0.25D;

    /**
     * Change the hologram displayName
     *
     * @param displayName The new displayName
     */

    Hologram setDisplayName(String displayName);

    Hologram updateTitle(Player player);

    /**
     * Teleports the hologram to the specified location
     *
     * @param location The location to teleport the hologram to
     */

    Hologram teleport(Location location);

    /**
     * Adds a line to the hologram above the current lines
     *
     * @param line The line to add
     */

    Hologram addLineAbove(String line);

    /**
     * Adds a line to the hologram below the current lines
     *
     * @param line The line to add
     */

    Hologram addLineBelow(String line);

    /**
     * Get the lines of the hologram
     *
     * @return The lines of the hologram
     */

    Collection<Hologram> getLinesBelow();

    /**
     * Get the lines of the hologram
     *
     * @return The lines of the hologram
     */

    Collection<Hologram> getLinesAbove();

    boolean hasTouchHandler();

    /**
     * Get the touch handler of the hologram
     *
     * @return The touch handler of the hologram
     */

    TouchHandler<Hologram> getTouchHandler();

    /**
     * Set the touch handler of the hologram
     *
     * @param touchHandler The new touch handler
     */

    void setTouchHandler(TouchHandler<Hologram> touchHandler);

    boolean hasViewHandler();

    /**
     * Get the view handler of the hologram
     *
     * @return The view handler of the hologram
     */

    ViewHandler getViewHandler();

    /**
     * Set view handler of the hologram
     *
     * @param viewHandler The view handler of the hologram
     */

    void setViewHandler(ViewHandler viewHandler);

    /**
     * Hide the hologram for the specified player
     *
     * @param player The player to hide the hologram for
     */

    Hologram hide(Player player);

    /**
     * Show the hologram for the specified player
     *
     * @param player The player to show the hologram for
     */

    Hologram show(Player player);

    void block(Player player);

    void unblock(Player player);

    boolean isBlocked(Player player);

    /**
     * Gets the UUID of the hologram
     *
     * @return The UUID of the hologram
     */

    Location getLocation();

    /**
     * Checks if the hologram is hidden for the specified player
     *
     * @param player The player to check
     * @return Whether the hologram is hidden for the player
     */

    boolean isHiddenForPlayer(Player player);

    /**
     * Checks if the hologram is showing for the specified player
     *
     * @param player The player to check
     * @return Whether the hologram is showing for the player
     */

    default boolean isShowingForPlayer(Player player) {
        return !isHiddenForPlayer(player);
    }

    int getEntityId();
}
