package tk.yallandev.saintmc.common.account;

import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;

public class MemberVoid extends Member {

	public MemberVoid(MemberModel memberModel) {
		super(memberModel);
	}
	
	public MemberVoid(String playerName, UUID uniqueId) {
		super(playerName, uniqueId);
	}

	@Override
	public void sendMessage(String message) {
		
	}

	@Override
	public void sendMessage(BaseComponent message) {
		
	}

	@Override
	public void sendMessage(BaseComponent[] message) {
		
	}

}
