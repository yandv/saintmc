package tk.yallandev.saintmc.common.account.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusType {

	PVP("pvp", Type.NORMAL), SHADOW("shadow", Type.NORMAL), GLADIATOR("gladiator", Type.NORMAL),
	HG("hungergames", Type.GAME), LOBBY("lobby-combat", Type.NORMAL);

	private String mongoCollection;
	private Type type;

	public enum Type {

		NORMAL, GAME;

	}

}
