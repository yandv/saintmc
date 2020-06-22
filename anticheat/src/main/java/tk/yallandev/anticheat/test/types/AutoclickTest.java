package tk.yallandev.anticheat.test.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.yallandev.anticheat.stats.constructor.Clicks;
import tk.yallandev.anticheat.test.Test;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class AutoclickTest extends Test {

	private Map<Player, Clicks> clicksPerSecond;

	public AutoclickTest() {
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

		if (click.getExpireTime() < System.currentTimeMillis()) {

			if (click.getClicks() >= 30) {
				getPlayerCallback(player).result(CheckLevel.DEFINITELY, null);
			} else if (click.getClicks() >= 25) {
				getPlayerCallback(player).result(CheckLevel.PROBABLY, null);
			} else if (click.getClicks() >= 16) {
				getPlayerCallback(player).result(CheckLevel.MAYBE, null);
			} else {
				getPlayerCallback(player).result(CheckLevel.NO_CHANCE, null);
			}

			clicksPerSecond.remove(player);
			removePlayer(player);
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
					iterator.remove();

					if (entry.getValue().getClicks() >= 30) {
						getPlayerCallback(entry.getKey()).result(CheckLevel.MAYBE, null);
					} else if (entry.getValue().getClicks() >= 25) {
						getPlayerCallback(entry.getKey()).result(CheckLevel.MAYBE, null);
					} else if (entry.getValue().getClicks() >= 16) {
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

}
