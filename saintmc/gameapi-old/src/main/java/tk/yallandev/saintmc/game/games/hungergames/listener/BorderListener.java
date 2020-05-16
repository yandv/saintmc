package tk.yallandev.saintmc.game.games.hungergames.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.stage.GameStage;

public class BorderListener extends tk.yallandev.saintmc.game.listener.GameListener {

	public Map<Player, Location> locations = new HashMap<>();

	public BorderListener(GameMain main) {
		super(main);
	}

	@EventHandler
	public void onTime(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (isOnWarning(p)) {
				if (!GameStage.isPregame(getGameMain().getGameStage())) {
					p.sendMessage("§e§l> §fVocê está perto da borda do mundo!");
					continue;
				}
			}
			
			if (isNotInBoard(p) || p.getLocation().getY() > 129) {
				if (GameStage.isPregame(getGameMain().getGameStage())) {
					if (locations.containsKey(p)) {
						p.teleport(locations.get(p));
						continue;
					}
				} else {
					p.sendMessage("§c§l> §fVoc§ passou da borda do mundo!");
					EntityDamageEvent e = new EntityDamageEvent((Entity) p, DamageCause.CUSTOM, 4.0d);
					if (e.isCancelled()) {
						e.setCancelled(false);
					}
					p.setLastDamageCause(e);
					p.damage(4.0);
				}
			}
			locations.put(p, p.getLocation());
		}
	}

	private boolean isNotInBoard(Player p) {
		int size = (int) 1000 / 2;
		return ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
				|| (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
	}

	private boolean isOnWarning(Player p) {
		int size = (int) 1000 / 2;
		size = size - 20;
		return !isNotInBoard(p) && ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
				|| (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
	}

}