package tk.yallandev.saintmc.game.manager;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerManager {
	
	private final static ServerManager INSTANCE = new ServerManager();
	
	private boolean pvp = true;
	private boolean damage = true;
	private boolean build = true;
	private boolean place = true;
	private boolean drop = true;
	private boolean pickup = true;
	private boolean kit = true;
	
	private int maxKits = 2;
	
	public static ServerManager getInstance() {
		return INSTANCE;
	}
}