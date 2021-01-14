package tk.yallandev.saintmc.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;

@Getter
@AllArgsConstructor
public enum ServerType {

	LOGIN, LOBBY, LOBBY_HG, LOBBY_SKYWARS, LOBBY_BEDWARS,

	FULLIRON, SIMULATOR, GLADIATOR,

	EVENTO, HUNGERGAMES,

	SW_SOLO, SW_TEAM, SW_SQUAD,
	
	SK_SOLO, SK_TEAM, SK_SQUAD,

	BW_SOLO, BW_TEAM, BW_SQUAD,

	CLANXCLAN, SCREENSHARE,

	NETWORK, NONE;

	public boolean isLobby() {
		return this.name().contains("LOBBY");
	}

	public ServerType getServerLobby() {
		switch (this) {
		case HUNGERGAMES:
		case EVENTO:
			return CommonConst.LOBBY_HG ? LOBBY_HG : LOBBY;
		default:
			return LOBBY;
		}
	}

	public boolean canSendData() {
		return true;
	}

}
