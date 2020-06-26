package tk.yallandev.saintmc.common.giftcode;

import tk.yallandev.saintmc.common.account.Member;

public interface Giftcode {

	void execute(Member member);
	
	String getCode();
	
	boolean alreadyUsed();
	
}
