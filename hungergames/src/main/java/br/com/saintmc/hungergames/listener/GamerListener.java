package br.com.saintmc.hungergames.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.gamer.Gamer;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class GamerListener extends GameListener {
	
	private Map<UUID, Long> timeoutMap;
	
	public GamerListener() {
		timeoutMap = new HashMap<>();
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;
		
		Player player = event.getPlayer();
		
		Gamer gamer = new Gamer(player);
		GameGeneral.getInstance().getGamerController().loadGamer(player.getUniqueId(), gamer);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId()) == null) {
			event.getPlayer().kickPlayer("§cConta não carregada!");
			return;
		}
		
		if (!isPregame()) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId());
			
			if (timeoutMap.containsKey(event.getPlayer().getUniqueId())) {
				timeoutMap.remove(event.getPlayer().getUniqueId());
				
				if (!gamer.isNotPlaying())
					event.setJoinMessage("§e" + event.getPlayer().getName() + " entrou no torneio!");
			} else {
				event.setJoinMessage("§e" + event.getPlayer().getName() + " entrou no torneio!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (isPregame()) {
			GameGeneral.getInstance().getGamerController().unload(event.getPlayer().getUniqueId());
		} else {
			Player player = event.getPlayer();
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
			
			if (gamer.isNotPlaying()) {
				GameGeneral.getInstance().getGamerController().unload(player.getUniqueId());
				event.setQuitMessage(null);
				return;
			}
			
			timeoutMap.put(player.getUniqueId(), System.currentTimeMillis() + 50000l);
		}
	}
	
	@EventHandler
	public void update(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		
		Iterator<Entry<UUID, Long>> iterator = timeoutMap.entrySet().iterator();
		
		if (iterator.hasNext()) {
			Entry<UUID, Long> entry = iterator.next();
			
			if (entry.getValue() > System.currentTimeMillis()) {
				Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(entry.getKey());
				
				gamer.setTimeout(true);
				Bukkit.broadcastMessage("§b" + gamer.getPlayerName() + " demorou demais para relogar e foi desclassificado!");
				
				iterator.remove();
			}
		}
	}
	
}
