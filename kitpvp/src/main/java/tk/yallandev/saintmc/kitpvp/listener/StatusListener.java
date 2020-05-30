package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.utils.RewardCalculator;
import tk.yallandev.saintmc.kitpvp.warp.DuelWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.SumoWarp;

public class StatusListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		Player player = event.getPlayer();
		Player killer = event.getKiller();
		
		if (killer == null) {
			player.sendMessage("§c§l> §fVocê §cmorreu§f!");
			return;
		}
		
		if (event.getWarp() instanceof SumoWarp) {
			
			player.sendMessage("§c§l> §fVocê §cmorreu§f para o §c" + killer.getName() + "§f!");
			killer.sendMessage("§a§l> §fVocê matou o §a" + player.getName() + "§f!");
			
			return;
		}
		
		boolean duels = (event.getWarp() instanceof DuelWarp);
		StatusType statusType = duels ? StatusType.SHADOW : StatusType.PVP;
		
		Status playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(), statusType);
		Status killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(killer.getUniqueId(), statusType);
		
		int winnerXp = RewardCalculator.calculateReward(player, playerStatus, killer, killerStatus);
		
		if (duels) {
			winnerXp *= 1.5;
		}
		
		player.sendMessage("§c§l> §fVocê §cmorreu§f para o §c" + killer.getName() + "§f!");
		player.sendMessage("§c§l> §fVocê perdeu §c0§f!");
		
		playerStatus.addDeath();
		playerStatus.resetKillstreak();
		
		killer.sendMessage("§a§l> §fVocê matou o §a" + player.getName() + "§f!");
		killer.sendMessage("§a§l> §fVocê ganhou §a" + winnerXp + "§f" + (duels ? " §7(1.5x no duels)" : "") + "§f!");
		
		killerStatus.addKill();
		killerStatus.addKillstreak();
		
		CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(winnerXp);
	}

}
