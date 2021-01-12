package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

public class StatusListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (gamer.isPlaying()) {
			GameStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					GameMain.getInstance().getSkywarsType().getStatusType(), GameStatus.class);

			if (player.getKiller() instanceof Player) {
				Player killer = (Player) player.getKiller();
				Gamer gamerKiller = GameGeneral.getInstance().getGamerController().getGamer(killer);

				if (gamerKiller.isPlaying()) {
					GameStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(
							killer.getUniqueId(), GameMain.getInstance().getSkywarsType().getStatusType(),
							GameStatus.class);

					killerStatus.addKill();
					killerStatus.addKillstreak();
					gamerKiller.addMatchKill();
					CommonGeneral.getInstance().getMemberManager().getMember(gamerKiller.getUniqueId()).addMoney(30);
				}
			}

			status.addDeath();
			status.resetKillstreak();
		}
	}

}
