package tk.yallandev.saintmc.common.account;

import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;

public class MemberVoid extends Member {

	public MemberVoid(MemberModel memberModel) {
		super(memberModel);
	}
	
	public MemberVoid(String playerName, UUID uniqueId, AccountType accountType) {
		super(playerName, uniqueId, accountType);
	}

	@Override
	public void sendMessage(String message) {
		System.out.println("VOID -> " + message);
	}

	@Override
	public void sendMessage(BaseComponent message) {
		
	}

	@Override
	public void sendMessage(BaseComponent[] message) {
		
	}

}
