package tk.yallandev.saintmc.game.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scoreboard.DisplaySlot;

import tk.yallandev.saintmc.bukkit.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.gameevents.gamer.GamerJoinEvent;

public class JoinListener extends GameListener {

	public JoinListener(GameMain main) {
		super(main);
	}

	@EventHandler
	public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
		if (GameMain.enabled > System.currentTimeMillis()) {
			event.setLoginResult(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			return;
		}
		
		Gamer gamer = new Gamer(event.getUniqueId());
		
		getGameMain().getGamerManager().addGamer(gamer);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() == Result.KICK_FULL || GameMain.getPlugin().playersLeft() >= 100) {
			Member player = Member.getMember(event.getPlayer().getUniqueId());
			
			if (player.hasGroupPermission(Group.LIGHT)) {
				event.allow();
			} else {
//				event.disallow(Result.KICK_FULL, T.t(GameMain.getPlugin(), player.getLanguage(), "server-full"));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		getGameMain().getGameEventManager().newEvent(new GamerJoinEvent(event.getPlayer().getUniqueId()));
		ScoreboardAPI.createObjectiveIfNotExistsToPlayer(event.getPlayer(), "clear", "", DisplaySlot.SIDEBAR);
	}
}
