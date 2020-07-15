package tk.yallandev.saintmc.common.account.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusType {

	PVP("pvp", Type.NORMAL), SHADOW("shadow", Type.NORMAL), GLADIATOR("gladiator", Type.NORMAL),
	LOBBY("lobby-combat", Type.NORMAL), HG("hungergames", Type.GAME), SW_SOLO("skywars-solo", Type.GAME),
	SW_TEAM("skywars-team", Type.GAME), SW_SQUAD("skywars-squad", Type.GAME);

	private String mongoCollection;
	private Type type;

	public enum Type {

		NORMAL, GAME;

	}

}
