package tk.yallandev.saintmc.skwyars.listener;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;

import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameStateChangeEvent;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.game.team.Team;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;
import tk.yallandev.saintmc.skwyars.scheduler.MinigameState;

public class WinnerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		checkWin();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		checkWin();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameStateChange(GameStateChangeEvent event) {
		if (event.getToState().isGametime())
			new BukkitRunnable() {

				@Override
				public void run() {
					checkWin();
				}
			}.runTaskLater(GameMain.getInstance(), 5l);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		checkWin();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if (GameGeneral.getInstance().getMinigameState() == MinigameState.WINNING)
			event.setCancelled(true);
	}

	private void checkWin() {
		if (GameGeneral.getInstance().getMinigameState() == MinigameState.WINNING)
			return;

		if (GameGeneral.getInstance().getTeamController().getTeamPlayingList().isEmpty()) {
			Bukkit.broadcastMessage("§aNenhum jogador ganhou!");

			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getOnlinePlayers().forEach(player -> GameMain.getInstance().sendPlayAgain(player));

					if (Bukkit.getOnlinePlayers().size() == 0)
						Bukkit.shutdown();
				}
			}.runTaskTimer(GameMain.getInstance(), 0, 10);
			return;
		}

		if (GameGeneral.getInstance().getTeamController().getTeamPlayingList().size() == 1) {
			Team team = GameGeneral.getInstance().getTeamController().getTeamPlayingList().stream().findFirst()
					.orElse(null);
			List<Gamer> gamerList = team.getGamerList();

			for (Gamer gamer : gamerList) {
				gamer.getPlayer().setGameMode(GameMode.CREATIVE);

				gamer.getPlayer().sendMessage(" §2§lVOCÊ VENCEU!");
				gamer.getPlayer().sendMessage(" ");
				gamer.getPlayer().sendMessage("§a+15 moedas§f pela vitória!");
				gamer.getPlayer().sendMessage("§a+20 moedas§f pela participação!");
				gamer.getPlayer().sendMessage("§a+30 moedas§f por 1 eleminações!");
//				CommonGeneral.getInstance().getStatusManager().loadStatus(gamer.getUniqueId(),
//						GameMain.getInstance().getSkywarsType().getStatusType(), GameStatus.class).addWin();
			}

			Bukkit.broadcastMessage("§e"
					+ Joiner.on(", ").join(gamerList.stream().map(Gamer::getPlayerName).collect(Collectors.toList()))
					+ " " + (gamerList.size() == 1 ? "ganhou" : "ganharam") + " a partida!");

			GameGeneral.getInstance().setGameState(MinigameState.WINNING);
		}
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		if (GameGeneral.getInstance().getMinigameState() == MinigameState.WINNING) {
			Location location = GameMain.getInstance().getLocationFromConfig("firework");

//			FireworkAPI.spawn(location.add(4, 0, 0), Color.GREEN, Color.GRAY, Type.BURST, true);
//			FireworkAPI.spawn(location.add(-4, 0, 0), Color.GREEN, Color.GRAY, Type.BURST, true);
//			FireworkAPI.spawn(location.add(0, 0, 4), Color.GREEN, Color.GRAY, Type.BURST, true);
//			FireworkAPI.spawn(location.add(0, 0, -4), Color.GREEN, Color.GRAY, Type.BURST, true);
//
//			FireworkAPI.spawn(location.add(6, 0, 0), Color.RED, Color.GRAY, Type.BURST, true);
//			FireworkAPI.spawn(location.add(-6, 0, 0), Color.RED, Color.GRAY, Type.BURST, true);
//			FireworkAPI.spawn(location.add(0, 0, 6), Color.RED, Color.GRAY, Type.BURST, true);
//			FireworkAPI.spawn(location.add(0, 0, -6), Color.RED, Color.GRAY, Type.BURST, true);

			if (event.getTime() >= 20) {
				Bukkit.shutdown();
				return;
			}

			if (event.getTime() >= 17) {
				Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cO servidor foi finalizado!"));

				if (Bukkit.getOnlinePlayers().size() == 0)
					Bukkit.shutdown();
				return;
			}

			if (event.getTime() >= 13) {
				Bukkit.getOnlinePlayers().forEach(player -> GameMain.getInstance().sendPlayAgain(player));

				if (Bukkit.getOnlinePlayers().size() == 0)
					Bukkit.shutdown();
				return;
			}

			if (event.getTime() >= 10) {
				Bukkit.getOnlinePlayers().forEach(player -> GameMain.getInstance().sendPlayAgain(player));

				if (Bukkit.getOnlinePlayers().size() == 0)
					Bukkit.shutdown();
				return;
			}
		}
	}
}
