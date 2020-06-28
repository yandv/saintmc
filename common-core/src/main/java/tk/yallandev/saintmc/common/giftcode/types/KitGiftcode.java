package tk.yallandev.saintmc.common.giftcode.types;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.giftcode.Giftcode;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@Getter
public class KitGiftcode implements Giftcode {
	
	private String code;
	private String kitName;
	private long kitTime;
	private boolean alreadyUsed;
	
	public KitGiftcode(String code, String kitName, long kitTime) {
		this.code = code;
		this.kitName = kitName;
		this.kitTime = kitTime;
	}

	@Override
	public void execute(Member member) {
		member.addPermission("kit." + kitName.toLowerCase());
		member.sendMessage("§aVocê recebeu o kit §a" + NameUtils.formatString(kitName) + "§f!");
		alreadyUsed = true;
	}
	
	@Override
	public boolean alreadyUsed() {
		return alreadyUsed;
	}

}
