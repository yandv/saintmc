package br.com.saintmc.hungergames.scheduler.types;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.player.PlayerTimeoutEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.register.game.CombatlogListener;
import br.com.saintmc.hungergames.listener.register.winner.WinnerListener;
import br.com.saintmc.hungergames.structure.impl.FeastStructure;
import br.com.saintmc.hungergames.structure.impl.MinifeastStructure;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class GameScheduler implements GameSchedule {
	
	public static Location feastLocation;

	private GameGeneral gameGeneral;
	private List<Listener> listenerList;

	private int feastTimer;
	
	private FeastStructure feastStructure;

	public GameScheduler() {
		this.gameGeneral = GameGeneral.getInstance();
		this.listenerList = Arrays.asList(new CombatlogListener());

		registerListener();
		checkWin();
	}

	@Override
	public void pulse(int time, GameState gameState) {

		if (time % 240 == 0) {
			MinifeastStructure minifest = new MinifeastStructure();
			Location place = minifest.findPlace();
			minifest.spawn(place);
			
			Bukkit.broadcastMessage("§cUm minifeast spawnou entre §c(X: " + (place.getX() + 100) + " " + ( place.getX() - 100) + ") e §c(Z:" + (place.getZ() + 100) + " " + (place.getZ() - 100) + ")!");
		}

		if (feastStructure == null) {
			if (time == 720) {
				feastStructure = new FeastStructure();
				feastLocation = feastStructure.findPlace();
				feastStructure.spawn(feastLocation);
				
				feastTimer = 300;
				Bukkit.broadcastMessage("§cO feast irá spawnar em " + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + " em " + StringUtils.formatTime(feastTimer));
			}
		} else {
			feastTimer--;
			
			if (feastTimer == 0) {
				feastStructure.spawnChest(feastLocation);
				Bukkit.broadcastMessage("§cO feast spawnou em " + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "!");
				
				feastStructure = null;
			} else if ((feastTimer % 60 == 0 || (feastTimer < 60 && (feastTimer % 15 == 0 || feastTimer == 10 || feastTimer <= 5)))) {
				Bukkit.broadcastMessage("§cO feast irá spawnar em " + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + " em " + StringUtils.formatTime(feastTimer));
			}
		}
		
		if (time == 60 * 35) {
			FeastStructure feast = new FeastStructure(25, 450);
			Location location = feast.findPlace();
			
			feast.spawn(location);
			feast.spawnChest(location);
			Bukkit.broadcastMessage("§cO bonus feast spawnou em algum lugar do mapa!");
		}
		
		if (ServerConfig.getInstance().isFinalBattle()) {
			if (time == 60 * 40) {
				Bukkit.broadcastMessage("§cA arena final vai spawnar em 5 minutos!");
			}
			
			if (time == 60 * 45) {
				Bukkit.broadcastMessage("§cA arena final foi gerada!");
//				World w = Bukkit.getWorld("world");
//				Random r = new Random();
//
//				int x = 0;
//				int z = 0;
//				
//				if (r.nextBoolean())
//					x = -x;
//				
//				if (r.nextBoolean())
//					z = -z;
//				
//				int y = w.getHighestBlockYAt(x, z) + 1;
//				
//				Location location = new Location(w, x, y, z);
//				
//				Bukkit.getOnlinePlayers().forEach(player -> player.teleport(location));
//				
//				org.bukkit.WorldBorder border = w.getWorldBorder();
//				border.setCenter(0, 0);
//				border.setSize(40);
			}
		}
		
		if (ServerConfig.getInstance().isForceWin()) {
			
			
			if (time == 60 * 50) {
				Bukkit.broadcastMessage("§cEm 5 minutos o jogador com a maior quantidade de kills irá vencer!");
			}
			
			if (time == 60 * 55) {
				if (checkWin())
					return;
				
				Bukkit.broadcastMessage("§cO jogador com maior quantidade de kills irá ganhar!");
				
				List<Gamer> gamerList = GameGeneral.getInstance().getGamerController().getGamers().stream().filter(gamer -> !gamer.isNotPlaying()).sorted((gamer1, gamer2) -> Integer.valueOf(gamer1.getMatchKills()).compareTo(gamer2.getMatchKills())).collect(Collectors.toList());
				
				for (int x = 1; x < gamerList.size(); x++) {
					Gamer gamer = gamerList.get(x);
					
					gamer.getPlayer().sendMessage("§cVocê morreu pois não é o jogador com maior quantidade de kills!");
					gamer.setSpectator(true);
					
					if (checkWin())
						break;
				}
			}
			
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		checkWin();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerDeathEvent event) {
		checkWin();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerQuitEvent event) {
		checkWin();
	}
	
	@EventHandler
	public void onPlayerTimeout(PlayerTimeoutEvent event) {
		checkWin();
	}

	public boolean checkWin() {
		if (gameGeneral.getGameState() == GameState.WINNING)
			return false;
		
		if (gameGeneral.getPlayersInGame() > 1) {
			return false;
		}
		
		gameGeneral.setGameState(GameState.WINNING);
		Player pWin = null;

		for (Player p : Bukkit.getOnlinePlayers()) {
			Gamer gamer = gameGeneral.getGamerController().getGamer(p);

			if (gamer.isGamemaker())
				continue;

			if (gamer.isSpectator())
				continue;

			if (!p.isOnline())
				continue;

			pWin = p;
			break;
		}

		unregisterListener();
		GameGeneral.getInstance().getSchedulerController().removeSchedule(this);
		GameMain.getInstance().registerListener(new WinnerListener(pWin));
		return true;
	}

	@Override
	public void registerListener() {
		listenerList.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance()));
	}

	@Override
	public void unregisterListener() {
		listenerList.forEach(listener -> HandlerList.unregisterAll(listener));
	}

}
