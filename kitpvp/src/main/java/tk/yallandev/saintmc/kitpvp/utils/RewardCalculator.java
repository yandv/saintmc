package tk.yallandev.saintmc.kitpvp.utils;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;

public class RewardCalculator {

	public static int calculateReward(Player player, NormalStatus playerStatus, Player killer,
			NormalStatus killerStatus) {
		int playerKd = playerStatus.getKills() / (playerStatus.getDeaths() == 0 ? 1 : playerStatus.getDeaths());
		int playerXp = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).getXp();

		int killerKd = killerStatus.getKills() / (killerStatus.getDeaths() == 0 ? 1 : killerStatus.getDeaths());
		int killerXp = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).getXp();

		int xp = CommonConst.RANDOM.nextInt(18) + 1;

		int kdDifference = killerKd - playerKd;

		if (kdDifference - 1.2 > 0)
			xp += CommonConst.RANDOM.nextInt(5) + 1;

		int xpDifference = killerXp - playerXp;

		if (xpDifference - 300 > 0) {
			if (xpDifference / 1000 > 0) {
				xp += CommonConst.RANDOM.nextInt(8) + 1;
			} else if (xpDifference / 100 > 0) {
				xp += CommonConst.RANDOM.nextInt(5) + 1;
			}
		} else if (xpDifference > 0)
			xp += CommonConst.RANDOM.nextInt(3) + 1;

		if (xp < 1) {
			xp = 1;
		}

		xp += playerStatus.getKillstreak() / 10;

		return xp;
	}

	public static void main(String[] args) {
		System.out.print(3 / 0);
	}
}
