package tk.yallandev.saintmc.skwyars.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
@AllArgsConstructor
public enum SkywarsType {
	
	SOLO(12, StatusType.SW_SOLO), TEAM(24, StatusType.SW_TEAM), SQUAD(64, StatusType.SW_SQUAD);
	
	private int maxPlayers;
	private StatusType statusType;

}