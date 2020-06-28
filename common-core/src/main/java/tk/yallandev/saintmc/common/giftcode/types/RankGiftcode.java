package tk.yallandev.saintmc.common.giftcode.types;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.giftcode.Giftcode;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;

@Getter
public class RankGiftcode implements Giftcode {

	private String code;

	private RankType rankType;
	private long rankTime;

	private boolean alreadyUsed;

	public RankGiftcode(String code, RankType rankType, long rankTime) {
		this.code = code;
		this.rankType = rankType;
		this.rankTime = rankTime;
	}

	@Override
	public void execute(Member member) {
		if (member.hasRank(getRankType())) {
			member.getRanks().put(getRankType(),
					member.getRanks().get(getRankType()) + (getRankTime() - System.currentTimeMillis()));
		} else
			member.getRanks().put(getRankType(), getRankTime());

		member.saveRanks();
		member.sendMessage("§aVocê ativou o código " + code + " de " + Tag.valueOf(getRankType().name()).getPrefix()
				+ "§a por " + DateUtils.getTime(getRankTime()) + "!");
		alreadyUsed = true;
	}

	@Override
	public boolean alreadyUsed() {
		return alreadyUsed;
	}

}
