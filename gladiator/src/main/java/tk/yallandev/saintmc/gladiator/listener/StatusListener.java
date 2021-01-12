package tk.yallandev.saintmc.gladiator.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.gladiator.event.GladiatorFinishEvent;

public class StatusListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerWarpDeath(GladiatorFinishEvent event) {
		Player player = event.getLoser();
		Player killer = event.getWinner();

		if (killer == null) {
			player.sendMessage("§cVocê morreu!");
			return;
		}

		NormalStatus playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
				StatusType.GLADIATOR, NormalStatus.class);
		NormalStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(killer.getUniqueId(),
				StatusType.GLADIATOR, NormalStatus.class);

		int winnerXp = CommonConst.RANDOM.nextInt(13) + 5;
		int winnerMoney = 50;

		int lostXp = CommonConst.RANDOM.nextInt(8) + 1;
		int lostMoney = 20;

		player.spigot()
				.sendMessage(
						new MessageBuilder("§cVocê morreu para o " + killer.getName() + "!")
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										TextComponent.fromLegacyText(
												"§c-" + lostXp + " xp" + "\n" + "§c-" + lostMoney + " coins")))
								.create());

		if (playerStatus.getKillstreak() >= 10)
			Bukkit.broadcastMessage(
					"§9KillStreak> §fO jogador §a" + player.getName() + "§f perdeu o seu §6Killstreak de "
							+ playerStatus.getKillstreak() + "§f para o §c" + killer.getName() + "§f!");

		playerStatus.addDeath();
		playerStatus.resetKillstreak();

		killer.spigot()
				.sendMessage(
						new MessageBuilder("§aVocê matou o " + player.getName() + "!")
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										TextComponent.fromLegacyText(
												"§a+" + winnerXp + " xp" + "\n" + "§a+" + winnerMoney + " coins")))
								.create());

		killerStatus.addKill();
		killerStatus.addKillstreak();

		if (killerStatus.getKillstreak() % 5 == 0)
			Bukkit.broadcastMessage("§9KillStreak> §fO jogador §a" + killer.getName() + "§f está com §6Killstreak de "
					+ killerStatus.getKillstreak() + "§f!");

		CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(winnerXp);
		CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addMoney(winnerMoney);
		CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeXp(lostXp);
		CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeMoney(lostMoney);

//		NormalStatus playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
//				StatusType.GLADIATOR, NormalStatus.class);
//		NormalStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(killer.getUniqueId(),
//				StatusType.GLADIATOR, NormalStatus.class);
//
//		int winnerXp = RewardCalculator.calculateReward(player, playerStatus, killer, killerStatus);
//		int winnerXp = 10;
//
//		int lostXp = CommonConst.RANDOM.nextInt(8) + 1;
//
//		player.sendMessage("§c§l> §fVocê §cmorreu§f para o §c" + killer.getName() + "§f!");
//		player.sendMessage("§c§l> §fVocê perdeu §c" + lostXp + "§f!");
//
//		if (playerStatus.getKillstreak() >= 10)
//			Bukkit.broadcastMessage(
//					"§9KillStreak> §fO jogador §a" + player.getName() + "§f perdeu o seu §6Killstreak de "
//							+ playerStatus.getKillstreak() + "§f para o §c" + killer.getName() + "§f!");
//
//		playerStatus.addDeath();
//		playerStatus.resetKillstreak();
//		
//		killer.sendMessage("§a§l> §fVocê matou o §a" + player.getName() + "§f!");
//		killer.sendMessage("§a§l> §fVocê ganhou §a" + winnerXp + " xp§f!");
//
//		killerStatus.addKill();
//		killerStatus.addKillstreak();
//		
//		if (killerStatus.getKillstreak() % 5 == 0)
//			Bukkit.broadcastMessage("§9KillStreak> §fO jogador §a" + killer.getName() + "§f está com §6Killstreak de "
//					+ killerStatus.getKillstreak() + "§f!");
//
//		CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(winnerXp);
//		CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeXp(lostXp);
	}

}
