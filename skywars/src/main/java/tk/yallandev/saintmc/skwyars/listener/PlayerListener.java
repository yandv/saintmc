package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeTagEvent;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.event.game.GameStartEvent;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

public class PlayerListener implements Listener {

	private String friendPrefix = "§a";
	private String enimyPrefix = "§c";

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (GameGeneral.getInstance().getMinigameState() == MinigameState.WAITING)
			return;

		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		for (Player o : Bukkit.getOnlinePlayers()) {
			if (gamer.getTeam().equals(GameGeneral.getInstance().getGamerController().getGamer(o).getTeam())) {
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(player, "a", friendPrefix, ""), o);
			} else {
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(player, "b", enimyPrefix, ""), o);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChangeTag(PlayerChangeTagEvent event) {
		if (GameGeneral.getInstance().getMinigameState() == MinigameState.WAITING)
			return;

		event.setCancelled(true);

		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
	
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (gamer.getTeam().equals(GameGeneral.getInstance().getGamerController().getGamer(o).getTeam())) {
				ScoreboardAPI.leaveTeamToPlayer(o, "b", player);
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, "a", friendPrefix, ""), player);
			} else {
				ScoreboardAPI.leaveTeamToPlayer(o, "a", player);
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, "b", enimyPrefix, ""), player);
			}
		}
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			for (Player o : Bukkit.getOnlinePlayers()) {
				if (gamer.getTeam().equals(GameGeneral.getInstance().getGamerController().getGamer(o).getTeam())) {
					ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(player, "a", friendPrefix, ""),
							o);
				} else {
					ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(player, "b", enimyPrefix, ""),
							o);
				}
			}
		}
	}

}
