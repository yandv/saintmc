package tk.yallandev.saintmc.bukkit.api.hologram;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.BukkitMain;

public class HologramListener implements Listener {

	private static final Set<Hologram> holograms = new HashSet<>();
	public static final int HOLOGRAM_DISTANCE = Bukkit.getViewDistance() * 16;

	public static Set<Hologram> getHolograms() {
		return holograms;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Hologram hologram : holograms) {
					if (!hologram.isRegistred())
						continue;

					if (hologram.getLocation().distance(event.getPlayer().getLocation()) < HOLOGRAM_DISTANCE && hologram.getLocation().getWorld().getName().equalsIgnoreCase(event.getPlayer().getLocation().getWorld().getName())) {
						if (!hologram.isViewer(event.getPlayer())) {
							hologram.addViewer(event.getPlayer());
							hologram.lock(event.getPlayer(), 5000);
						}
					} else {
						hologram.removeViewer(event.getPlayer());
					}
				}
			}
		}.runTaskAsynchronously(BukkitMain.getInstance());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled())
			return;

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Hologram hologram : holograms) {
					if (!hologram.locked(event.getPlayer()) || !hologram.isRegistred())
						continue;

					if (hologram.getLocation().distance(event.getTo()) < HOLOGRAM_DISTANCE && hologram.getLocation().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName())) {
						if (hologram.getLocation().distance(event.getFrom()) > HOLOGRAM_DISTANCE) {
							hologram.removeViewer(event.getPlayer());
							hologram.addViewer(event.getPlayer());
							hologram.lock(event.getPlayer(), 5000);
						}
					} else {
						hologram.removeViewer(event.getPlayer());
					}
				}
			}
		}.runTaskAsynchronously(BukkitMain.getInstance());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Hologram hologram : holograms) {
					if (!hologram.locked(event.getPlayer()) || !hologram.isRegistred())
						continue;

					if (hologram.getLocation().distance(event.getTo()) < HOLOGRAM_DISTANCE && hologram.getLocation().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName())) {
						if (!hologram.isViewer(event.getPlayer())) {
							hologram.addViewer(event.getPlayer());
							hologram.lock(event.getPlayer(), 5000);
						}
					} else {
						hologram.removeViewer(event.getPlayer());
					}
				}
			}
		}.runTaskAsynchronously(BukkitMain.getInstance());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Hologram hologram : holograms) {
					if (!hologram.isRegistred())
						continue;

					if (hologram.getLocation().distance(event.getPlayer().getLocation()) < HOLOGRAM_DISTANCE  && hologram.getLocation().getWorld().getName().equalsIgnoreCase(event.getPlayer().getLocation().getWorld().getName())) {
						hologram.removeViewer(event.getPlayer());
						hologram.addViewer(event.getPlayer());
					} else {
						hologram.removeViewer(event.getPlayer());
					}
				}
			}
		}.runTaskAsynchronously(BukkitMain.getInstance());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (Hologram hologram : holograms)
			hologram.removeViewer(event.getPlayer());
	}

}