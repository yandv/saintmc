package tk.yallandev.saintmc.bukkit.anticheat.check.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import lombok.NoArgsConstructor;
import tk.yallandev.saintmc.bukkit.anticheat.check.Check;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class AutoclickCheck extends Check {

	private Map<Player, Clicks> clicksPerSecond;

	public AutoclickCheck() {
		clicksPerSecond = new HashMap<>();
	}
	
	@Override
	public void verify(Player player, FutureCallback<CheckLevel> callback) {
		super.verify(player, callback);
		clicksPerSecond.computeIfAbsent(player, v -> new Clicks());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!isPlayer(event.getPlayer()))
			return;
		
		if (event.getAction() != Action.LEFT_CLICK_AIR)
			return;
		
		Player player = event.getPlayer();
		Clicks click = clicksPerSecond.computeIfAbsent(player, v -> new Clicks());
		
		if (click.expireTime < System.currentTimeMillis()) {

			if (click.clicks >= 30) {
				getPlayerCallback(player).result(CheckLevel.MAYBE, null);
			} else if (click.clicks >= 25) {
				getPlayerCallback(player).result(CheckLevel.MAYBE, null);
			} else if (click.clicks >= 16) {
				getPlayerCallback(player).result(CheckLevel.MAYBE, null);
			} else {
				getPlayerCallback(player).result(CheckLevel.NO_CHANCE, null);
			}
			
			clicksPerSecond.remove(player);
			removePlayer(player);
			return;
		}

		click.clicks++;
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Clicks>> iterator = clicksPerSecond.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().expireTime < System.currentTimeMillis()) {
					iterator.remove();
					
					if (entry.getValue().clicks >= 30) {
						getPlayerCallback(entry.getKey()).result(CheckLevel.MAYBE, null);
					} else if (entry.getValue().clicks >= 25) {
						getPlayerCallback(entry.getKey()).result(CheckLevel.MAYBE, null);
					} else if (entry.getValue().clicks >= 16) {
						getPlayerCallback(entry.getKey()).result(CheckLevel.MAYBE, null);
					} else {
						getPlayerCallback(entry.getKey()).result(CheckLevel.NO_CHANCE, null);
					}
					
					removePlayer(entry.getKey());
					clicksPerSecond.remove(entry.getKey());
				}
			}
		}
	}

	@NoArgsConstructor
	public class Clicks {

		private int clicks;
		private long expireTime = System.currentTimeMillis() + 1000;

	}

}
