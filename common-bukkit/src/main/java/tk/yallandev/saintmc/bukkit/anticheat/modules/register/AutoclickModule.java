package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.anticheat.modules.Clicks;
import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolGetter;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class AutoclickModule extends Module {

	private Map<Player, Clicks> clicksPerSecond;
	private Map<Player, Long> cooldownMap;

	public AutoclickModule() {
		setAlertBungee(true);
		clicksPerSecond = new HashMap<>();
		cooldownMap = new HashMap<>();

		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(BukkitMain.getInstance(), PacketType.Play.Client.ARM_ANIMATION) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						Player player = event.getPlayer();

						if (player == null || ProtocolGetter.getPing(player) >= 100)
							return;

						if (cooldownMap.containsKey(player) && cooldownMap.get(player) > System.currentTimeMillis())
							return;

						if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE)
							return;

						if (player.getTargetBlock((Set<Material>) null, 4).getType() != Material.AIR) {
							return;
						}

						handle(player);
					}

				});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(BlockDamageEvent event) {
		clicksPerSecond.remove(event.getPlayer());
	}

	public void handle(Player player) {
		Clicks click = clicksPerSecond.computeIfAbsent(player, v -> new Clicks());

		if (click.getExpireTime() < System.currentTimeMillis()) {
			if (click.getClicks() >= 20) {
				alert(player, click.getClicks());
			}

			clicksPerSecond.remove(player);
			return;
		}

		click.addClick();
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Clicks>> iterator = clicksPerSecond.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					if (entry.getValue().getClicks() >= 20) {
						alert(entry.getKey(), entry.getValue().getClicks());
					}

					iterator.remove();
				}
			}
		}
	}

}
