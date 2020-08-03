package tk.yallandev.saintmc.kitpvp.listener;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.utils.RewardCalculator;
import tk.yallandev.saintmc.kitpvp.warp.DuelWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.PartyWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;
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

		if (event.getWarp() instanceof PartyWarp)
			return;

		if (event.getWarp() instanceof SumoWarp) {

			player.sendMessage("§c§l> §fVocê §cmorreu§f para o §c" + killer.getName() + "§f!");
			killer.sendMessage("§a§l> §fVocê matou o §a" + player.getName() + "§f!");

			return;
		}

		boolean duels = (event.getWarp() instanceof DuelWarp);
		StatusType statusType = duels ? StatusType.SHADOW : StatusType.PVP;

		NormalStatus playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
				statusType, NormalStatus.class);
		NormalStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(killer.getUniqueId(),
				statusType, NormalStatus.class);

		int winnerXp = RewardCalculator.calculateReward(player, playerStatus, killer, killerStatus);

		if (duels)
			winnerXp *= 1.5;

		int winnerMoney = RewardCalculator.calculateReward(player, playerStatus, killer, killerStatus);

		if (duels)
			winnerMoney *= 1.5;

		int lostXp = CommonConst.RANDOM.nextInt(8) + 1;
		int lostMoney = 50 * (CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId())
				.hasGroupPermission(Group.BLIZZARD) ? 2 : 1);

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

		if (event.getWarp() instanceof ShadowWarp) {
			int mushroomSoup = (int) Arrays.asList(killer.getInventory().getContents()).stream()
					.filter(item -> item != null && item.getType() == Material.MUSHROOM_SOUP).count();

			player.sendMessage("§cO jogador ficou com " + (CommonConst.DECIMAL_FORMAT.format(killer.getHealth()))
					+ " corações e com " + (mushroomSoup) + " sopas!");
			killer.sendMessage("§aVocê ficou com " + (CommonConst.DECIMAL_FORMAT.format(killer.getHealth()))
					+ " corações e com " + (mushroomSoup) + " sopas!");
		}

		killerStatus.addKill();
		killerStatus.addKillstreak();

		if (killerStatus.getKillstreak() % 5 == 0)
			Bukkit.broadcastMessage("§9KillStreak> §fO jogador §a" + killer.getName() + "§f está com §6Killstreak de "
					+ killerStatus.getKillstreak() + "§f!");

		CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addXp(winnerXp);
		CommonGeneral.getInstance().getMemberManager().getMember(killer.getUniqueId()).addMoney(winnerMoney);
		CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeXp(lostXp);
		CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).removeMoney(lostMoney);
	}

}
