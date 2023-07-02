package tk.yallandev.saintmc.bukkit.api.hologram.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;
import tk.yallandev.saintmc.bukkit.api.hologram.TouchHandler;
import tk.yallandev.saintmc.bukkit.api.hologram.ViewHandler;
import tk.yallandev.saintmc.bukkit.api.packet.PacketBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class CraftSingleHologram implements Hologram {

    private String displayName;
    private Location location;

    @Setter
    private TouchHandler<Hologram> touchHandler;
    @Setter
    private ViewHandler viewHandler;

    private EntityArmorStand armorStand;
    private final Set<UUID> showing;
    private final Set<UUID> invisibleTo;

    public CraftSingleHologram(String displayName, Location location, TouchHandler<Hologram> touchHandler, ViewHandler viewHandler) {
        this.displayName = displayName;
        this.location = location;
        this.touchHandler = touchHandler;
        this.viewHandler = viewHandler;

        this.showing = new HashSet<>();
        this.invisibleTo = new HashSet<>();

        createEntity();
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    public CraftSingleHologram(String displayName, Location location) {
        this(displayName, location, EMPTY_TOUCH_HANDLER, ViewHandler.EMPTY);
    }

    public CraftSingleHologram(String displayName, Location location, ViewHandler viewHandler) {
        this(displayName, location, EMPTY_TOUCH_HANDLER, viewHandler);
    }

    @Override
    public Hologram setDisplayName(String displayName) {
        this.displayName = displayName;
        this.armorStand.setCustomName(displayName);
        this.armorStand.setCustomNameVisible(isCustomNameVisible());

        showing.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
            sendMetadataPacket(player, hasViewHandler() ? viewHandler.onView(this, player, displayName) : displayName);
        });

        return this;
    }

    @Override
    public Hologram updateTitle(Player player) {
        if (!showing.contains(player.getUniqueId())) {
            return this;
        }

        sendMetadataPacket(player, hasViewHandler() ? viewHandler.onView(this, player, displayName) : displayName);
        return this;
    }

    @Override
    public Hologram teleport(Location location) {
        this.location = location;
        this.armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                                    location.getPitch());

        for (Player player : Bukkit.getOnlinePlayers()) {
            hide(player);
            show(player);
        }
        return this;
    }

    @Override
    public Hologram addLineAbove(String line) {
        throw new UnsupportedOperationException("Cannot add line above to single hologram");
    }

    @Override
    public Hologram addLineBelow(String line) {
        throw new UnsupportedOperationException("Cannot add line above to single hologram");
    }

    @Override
    public Collection<Hologram> getLinesBelow() {
        return new ArrayList<>();
    }

    @Override
    public Collection<Hologram> getLinesAbove() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasTouchHandler() {
        return getTouchHandler() != null;
    }

    @Override
    public boolean hasViewHandler() {
        return getViewHandler() != null;
    }

    @Override
    public Hologram hide(Player player) {
        if (!showing.contains(player.getUniqueId())) {
            return this;
        }

        showing.remove(player.getUniqueId());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, new PacketBuilder(
                    PacketType.Play.Server.ENTITY_DESTROY).writeIntegerArray(0, new int[]{armorStand.getId()}).build());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public Hologram show(Player player) {
        if (showing.contains(player.getUniqueId())) {
            return this;
        }

        showing.add(player.getUniqueId());

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));

        if (hasViewHandler()) {
            sendMetadataPacket(player, viewHandler.onView(this, player, displayName));
        }

        return this;
    }

    public void block(Player player) {
        invisibleTo.add(player.getUniqueId());
        hide(player);
    }

    public void unblock(Player player) {
        invisibleTo.remove(player.getUniqueId());
    }

    public boolean isBlocked(Player player) {
        return invisibleTo.contains(player.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isHiddenForPlayer(Player player) {
        return !showing.contains(player.getUniqueId());
    }

    @Override
    public int getEntityId() {
        return armorStand == null ? -1 : armorStand.getId();
    }

    public boolean isCustomNameVisible() {
        return (displayName != null && !displayName.isEmpty());
    }

    private void createEntity() {
        armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());

        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(displayName);
        armorStand.setCustomNameVisible(isCustomNameVisible());

        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                               location.getPitch());
    }

    private void sendMetadataPacket(Player player, String displayName) {
        WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(armorStand.getBukkitEntity()).deepClone();

        if (dataWatcher.hasIndex(2)) {
            WrappedWatchableObject watchableObject = dataWatcher.getWatchableObject(2);

            watchableObject.setValue(displayName);

            dataWatcher.setObject(2, watchableObject);
        }

        PacketContainer packet = ProtocolLibrary.getProtocolManager()
                                                .createPacket(PacketType.Play.Server.ENTITY_METADATA);

        packet.getIntegers().write(0, armorStand.getId());
        packet.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
