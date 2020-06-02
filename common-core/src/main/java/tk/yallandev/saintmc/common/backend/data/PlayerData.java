package tk.yallandev.saintmc.common.backend.data;

import java.util.Collection;
import java.util.UUID;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;

public interface PlayerData {
	
	/*
	 * Member Info
	 */

	MemberModel loadMember(UUID uniqueId);
	
	MemberModel loadMember(String playerName);
	
	void createMember(MemberModel memberModel);
	
	void createMember(Member member);
	
	void updateMember(Member member, String fieldName);
	
	Collection<MemberModel> ranking(String fieldName);
	
	/*
	 * Discord Member Info
	 */
	
	MemberModel loadMember(long discordId);
	
	/*
	 * Member Cache
	 */
	
	void cacheMember(UUID uniqueId);
	
	boolean checkCache(UUID uniqueId);
	
	/*
	 * Connection
	 */
	
	void closeConnection();
}
