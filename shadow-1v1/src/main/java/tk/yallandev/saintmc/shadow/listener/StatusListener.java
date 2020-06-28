package tk.yallandev.saintmc.shadow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.combat.CombatStatus;
import tk.yallandev.saintmc.shadow.event.GladiatorFinishEvent;

public class StatusListener implements Listener {

    public static final int MIN_XP_REWARD  = 10; // xp minimo
    public static final int MAX_XP_REWARD = 20; // xp maximo

    public static final int MIN_XP_LOSE  = 5; // xp minimo
    public static final int MAX_XP_LOSE = 11; // xp maximo

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWarpDeath(GladiatorFinishEvent event) {
        Player player = event.getLoser();
        Player killer = event.getWinner();

        if (killer == null) {
            player.sendMessage("§cVocê morreu!");
            return;
        }

        CombatStatus playerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(player.getUniqueId(), StatusType.SHADOW,
                                                             CombatStatus.class);
        CombatStatus killerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(killer.getUniqueId(), StatusType.SHADOW,
                                                             CombatStatus.class);

        int xpReward = CommonConst.RANDOM.nextInt(MAX_XP_REWARD - MIN_XP_REWARD) + MIN_XP_REWARD,
                xpLost = CommonConst.RANDOM.nextInt(MAX_XP_LOSE - MIN_XP_LOSE) + MIN_XP_LOSE;

        player.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        player.sendMessage("§9" + killer.getName() + " §evenceu.");
        player.sendMessage("§b-" + xpLost + " XP");

        killer.sendMessage("§c" + player.getName() + " §efoi morto por §9" + killer.getName() + "§e.");
        killer.sendMessage("§9" + killer.getName() + " §evenceu.");

        killer.sendMessage("§b+" + xpReward + " XP");

        if (event.getChallenge().isRanked()) {
            playerStatus.addDeath();
            playerStatus.resetKillstreak();

            killerStatus.addKill();
            killerStatus.addKillstreak();

            CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(xpReward);
            CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeXp(xpLost);
        }
    }
}
