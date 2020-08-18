package net.saintmc.anticheat.check.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEatSoupEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.saintmc.anticheat.account.Member;
import net.saintmc.anticheat.alert.AlertMetadata;
import net.saintmc.anticheat.alert.AlertType;
import net.saintmc.anticheat.check.CheckClass;
import net.saintmc.anticheat.controller.MemberController;
import net.saintmc.anticheat.storage.InventoryChangeStorage;
import net.saintmc.anticheat.storage.InventoryCloseStorage;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolGetter;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class CombatCheck extends PacketAdapter implements CheckClass, Listener {

	private Map<Player, Clicks> macroMap;
	private Map<Player, Clicks> clickMap;

	public CombatCheck(Plugin plugin) {
		super(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY);
		macroMap = new HashMap<>();
		clickMap = new HashMap<>();

		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (event.isCancelled())
							return;

						Player player = event.getPlayer();

						if (player == null || ProtocolGetter.getPing(player) >= 150)
							return;

						if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
							return;

						if (event.getPacket().getEntityUseActions().read(0) == EntityUseAction.ATTACK) {
							handle(player);
						}
					}

				});
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		clickMap.remove(event.getPlayer());
	}

	public void handle(Player player) {
		Clicks click = clickMap.computeIfAbsent(player, v -> new Clicks());

		if (click.getExpireTime() < System.currentTimeMillis()) {
			if (click.getClicks() >= 23) {
				alert(player, AlertType.AUTOCLICK, new AlertMetadata("cps", click.getClicks()));
			}

			clickMap.remove(player);
			return;
		}

		click.addClick();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;

		if (event.isShiftClick())
			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
				return;

		Clicks click = macroMap.computeIfAbsent(player, v -> new Clicks());

		if (click.getExpireTime() < System.currentTimeMillis()) {
			if (click.getClicks() >= 25) {
				alert(player, AlertType.MACRO, new AlertMetadata("cps", click.getClicks()));
			}

			macroMap.remove(player);
			return;
		}

		click.addClick();
	}

	@EventHandler(priority = EventPriority.MONITOR)
//	@CheckHandler(checkType = CheckType.INTERACT)
	public boolean onPlayerEatSoup(PlayerEatSoupEvent interactStorage) {
		Member member = MemberController.INSTANCE.load(interactStorage.getPlayer());

		InventoryChangeStorage change = member.getLastChangeStorage();
		InventoryCloseStorage close = member.getLastInventoryClose();

		if (change == null || change.getCurrentItem() == null
				|| change.getCurrentItem().getType() != Material.MUSHROOM_SOUP)
			return false;

		if (close == null || change.getTickTime() > close.getTickTime() + 5) {
			alert(member, AlertType.AUTOSOUP);
			return true;
		}

		return false;
	}

	@EventHandler
	public void onServerTime(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Clicks>> iterator = clickMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					if (entry.getValue().getClicks() >= 23) {
						alert(entry.getKey(), AlertType.AUTOCLICK,
								new AlertMetadata("cps", entry.getValue().getClicks()));
					}

					iterator.remove();
				}
			}

			iterator = macroMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					if (entry.getValue().getClicks() >= 25) {
						alert(entry.getKey(), AlertType.MACRO, new AlertMetadata("cps", entry.getValue().getClicks()));
					}

					iterator.remove();
				}
			}
		}
	}

	@Getter
	@NoArgsConstructor
	public class Clicks {

		private int clicks;
		private long expireTime = System.currentTimeMillis() + 1000;

		public void addClick() {
			clicks++;
		}

	}

}
