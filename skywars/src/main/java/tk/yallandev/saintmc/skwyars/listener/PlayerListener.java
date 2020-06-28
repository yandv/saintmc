package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.event.game.GameStartEvent;
import tk.yallandev.saintmc.skwyars.event.kit.PlayerSelectKitEvent;
import tk.yallandev.saintmc.skwyars.event.kit.PlayerSelectedKitEvent;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;
import tk.yallandev.saintmc.skwyars.scheduler.MinigameState;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		if (!GameGeneral.getInstance().getMinigameState().isPregame()) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cVocê não pode mais escolher kit!");
			return;
		}

		if (event.getGamer().getKit() == event.getKit()) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§cVocê já está usando esse kit!");
			return;
		}
	}

	@EventHandler
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
		event.getPlayer().sendMessage("§aVocê selecionou o kit " + event.getKit().getName() + "!");
	}

	/**
	 * nao tinha fixado a ideia de como fazer o sistema de tag
	 * 
	 * mas a isso é meio q um esboço de como ficaria, seria a tag friend para time e
	 * enimy para todos os outros jogadores
	 */

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
