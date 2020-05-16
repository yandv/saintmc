package tk.yallandev.saintmc.common.account.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusType {
	
	PVP("pvp_status", Type.NORMAL), SHADOW("shadow_status", Type.NORMAL), HG("hungergames_status", Type.NORMAL);
	
	private String mongoCollection;
	private Type type;
	
	public enum Type {
		
		NORMAL, GAME;
		
	}

}
