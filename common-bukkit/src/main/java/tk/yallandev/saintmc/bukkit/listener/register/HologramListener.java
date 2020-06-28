package tk.yallandev.saintmc.bukkit.listener.register;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;
import tk.yallandev.saintmc.bukkit.api.hologram.TouchHandler;
import tk.yallandev.saintmc.bukkit.api.hologram.impl.CraftSingleHologram;

import java.util.*;

public class HologramListener implements Listener {

    public static final Map<Integer, Hologram> HOLOGRAM_MAP = new HashMap<>();

    private static final double MAX_DISTANCE = 128;

    private final Map<Player, Long> playerCooldown;

    public HologramListener() {
        playerCooldown = new HashMap<>();

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(BukkitMain.getInstance(), ListenerPriority.LOWEST,
                        PacketType.Play.Client.USE_ENTITY) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.isCancelled()) {
                            return;
                        }

                        int entityId = event.getPacket().getIntegers().read(0);
                        Hologram hologram = Optional.ofNullable(HOLOGRAM_MAP.get(entityId))
                                .orElse(BukkitMain.getInstance().getHologramController()
                                        .getHologramById(entityId));

                        if (hologram == null) return;

                        if (hologram.getEntityId() == entityId) {
                            hologram.getTouchHandler().onTouch(hologram, event.getPlayer(),
                                    event.getPacket().getEntityUseActions().read(0) ==
                                            EnumWrappers.EntityUseAction.INTERACT ?
                                            TouchHandler.TouchType.RIGHT :
                                            TouchHandler.TouchType.LEFT);
                            playerCooldown.put(event.getPlayer(), System.currentTimeMillis() + 200L);
                            event.setCancelled(true);
                        }
                    }
                });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        List<Location> lstLoc= new ArrayList<>();

        for(Block b : player.getLineOfSight((HashSet<Byte>) null, 5)){
            lstLoc.add(b.getLocation());
        }

        Hologram hologram = HOLOGRAM_MAP.values()
                .stream()
                .filter(h -> lstLoc.stream()
                        .anyMatch(loc -> loc.getBlockX() == h.getLocation().getBlockX() && loc.getBlockZ() == h.getLocation().getBlockZ()))
                .findFirst().orElse(null);

        if (hologram == null) return;

        hologram.getTouchHandler().onTouch(hologram, player, event.getAction().name().contains("RIGHT") ?
                TouchHandler.TouchType.RIGHT :
                TouchHandler.TouchType.LEFT);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BukkitMain.getInstance().getHologramController().getHolograms().forEach(hologram -> {
            if (hologram.isBlocked(event.getPlayer())) return;

            if (hologram.getLocation().getWorld().equals(event.getPlayer().getLocation().getWorld())) {
                if (hologram.getLocation().distance(event.getPlayer().getLocation()) < MAX_DISTANCE) {
                    hologram.show(event.getPlayer());
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                BukkitMain.getInstance().getHologramController().getHolograms().forEach(hologram -> {
                    if (hologram.getLocation().getWorld().equals(event.getPlayer().getLocation().getWorld())) {
                        if (hologram.isBlocked(event.getPlayer())) return;

                        if (hologram.isShowingForPlayer(event.getPlayer()) &&
                                hologram.getLocation().distance(event.getPlayer().getLocation()) < MAX_DISTANCE) {
                            hologram.hide(event.getPlayer());
                            hologram.show(event.getPlayer());
                        }
                    }
                });
            }
        }.runTaskLater(BukkitMain.getInstance(), 5L);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleHologramForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        handleHologramForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        BukkitMain.getInstance().getHologramController().getHolograms().forEach(hologram -> {
            hologram.hide(event.getPlayer());
            hologram.unblock(event.getPlayer());
        });
    }

    private void handleHologramForPlayer(Player player) {
        BukkitMain.getInstance().getHologramController().getHolograms().forEach(hologram -> {
            if (hologram.isBlocked(player)) return;

            if (hologram.isShowingForPlayer(player)) {
                if (!hologram.getLocation().getWorld().equals(player.getLocation().getWorld())) {
                    hologram.hide(player);
                } else if (hologram.getLocation().distance(player.getLocation()) > MAX_DISTANCE) {
                    hologram.hide(player);
                }
            } else {
                if (hologram.getLocation().getWorld().equals(player.getLocation().getWorld()) &&
                        hologram.getLocation().distance(player.getLocation()) < MAX_DISTANCE) {
                    hologram.show(player);
                }
            }
        });
    }
}
