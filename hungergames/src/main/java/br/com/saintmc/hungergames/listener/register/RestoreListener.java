package br.com.saintmc.hungergames.listener.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.constructor.Timeout;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreInitEvent;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreStopEvent;
import tk.yallandev.saintmc.common.profile.Profile;

public class RestoreListener implements Listener {

	private List<Entry<UUID, Timeout>> timeoutList;

	private RegisterableListener listener;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRestoreInit(RestoreInitEvent event) {
		for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers()) {
			if (gamer.isPlaying() || gamer.isSpectator() || AdminMode.getInstance().isAdmin(gamer.getPlayer())) {
				Profile profile = new Profile(gamer.getPlayer().getName(), gamer.getUniqueId());

				if (!event.getProfileList().contains(profile))
					event.getProfileList().add(profile);
			}
		}

		timeoutList = new ArrayList<>();

		for (Entry<UUID, Timeout> entry : GameGeneral.getInstance().getTimeoutController().getStoreMap().entrySet()) {
			Profile profile = new Profile("han", entry.getKey());

			if (!event.getProfileList().contains(profile))
				event.getProfileList().add(profile);

			timeoutList.add(entry);
		}

		GameGeneral.getInstance().getTimeoutController().getStoreMap().clear();
		GameGeneral.getInstance().setCountTime(false);

		listener = new RegisterableListener();
		listener.registerListener();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRestoreStop(RestoreStopEvent event) {
		GameGeneral.getInstance().setCountTime(true);

		for (Entry<UUID, Timeout> entry : timeoutList) {
			Player player = Bukkit.getPlayer(entry.getKey());
			
			if (player == null) {
				entry.getValue().setExpireTime(System.currentTimeMillis() + 60000l);
				GameGeneral.getInstance().getTimeoutController().load(entry.getKey(), entry.getValue());
			}
		}

		timeoutList = null;
		listener.unregisterListener();
		listener = null;
	}

	public class RegisterableListener extends ManualRegisterableListener {

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public void onBlockBreak(BlockBreakEvent event) {
			event.setCancelled(true);
		}

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public void onBlockPlace(BlockPlaceEvent event) {
			event.setCancelled(true);
		}

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public void onEntityDamage(EntityDamageEvent event) {
			event.setCancelled(true);
		}

	}

}
