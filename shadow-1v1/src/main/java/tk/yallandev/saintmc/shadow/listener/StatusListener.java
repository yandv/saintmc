package tk.yallandev.saintmc.shadow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.combat.CombatStatus;
import tk.yallandev.saintmc.shadow.event.GladiatorFinishEvent;

public class StatusListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWarpDeath(GladiatorFinishEvent event) {
        Player player = event.getLoser();
        Player killer = event.getWinner();

        if (killer == null) {
            player.sendMessage("§cVocê morreu!");
            return;
        }

        CombatStatus playerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(player.getUniqueId(), StatusType.GLADIATOR,
                                                             CombatStatus.class);
        CombatStatus killerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(killer.getUniqueId(), StatusType.GLADIATOR,
                                                             CombatStatus.class);

        player.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        player.sendMessage("§9" + killer.getName() + " §evenceu.");

        killer.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        killer.sendMessage("§9" + killer.getName() + " §evenceu.");

        playerStatus.addDeath();
        playerStatus.resetKillstreak();

        killerStatus.addKill();
        killerStatus.addKillstreak();

        if (event.getChallenge().isRanked()) {
            int xpReward = 10, xpLost = 10;


            CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(xpReward);
            CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeXp(xpLost);

            killer.sendMessage("§b+" + xpReward + " XP");
            player.sendMessage("§b-" + xpLost + " XP");
        }
    }
}
