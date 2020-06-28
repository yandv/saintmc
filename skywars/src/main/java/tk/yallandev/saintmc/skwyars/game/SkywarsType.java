package tk.yallandev.saintmc.skwyars.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
@RequiredArgsConstructor
public enum SkywarsType {
	
	SOLO(12), TEAM(24), SQUAD(64);
	
	private final int maxPlayers;
	private StatusType statusType = StatusType.SW_SOLO;

}