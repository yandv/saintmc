package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.GameMode;
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
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class AutoclickModule extends Module {

	private Map<Player, Clicks> clicksPerSecond;

	public AutoclickModule() {
		setAlertBungee(true);
		clicksPerSecond = new HashMap<>();
	
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(BukkitMain.getInstance(), PacketType.Play.Client.ARM_ANIMATION) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						Player player = event.getPlayer();

						if (player == null)
							return;

						if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE)
							return;
						
						handle(player);
					}

				});
	}	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(BlockDamageEvent event) {
		clicksPerSecond.remove(event.getPlayer());
	}

	public boolean handle(Player player) {
		Clicks click = clicksPerSecond.computeIfAbsent(player, v -> new Clicks());

		if (click.getExpireTime() < System.currentTimeMillis()) {
			if (click.getClicks() >= 16) {
				alert(player, click.getClicks());
			}

			clicksPerSecond.remove(player);
			return false;
		}

		click.addClick();

		if (click.getClicks() >= 25)
			return true;

		return false;
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Clicks>> iterator = clicksPerSecond.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					if (entry.getValue().getClicks() >= 16) {
						alert(entry.getKey(), entry.getValue().getClicks());
					}

					iterator.remove();
				}
			}
		}
	}

}
