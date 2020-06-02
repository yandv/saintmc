package br.com.saintmc.hungergames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.saintmc.hungergames.constructor.SimpleKit;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import lombok.Data;
import lombok.Getter;
import tk.yallandev.saintmc.common.permission.Group;

@Data
public class ServerConfig {
	
	@Getter
	private static ServerConfig instance = new ServerConfig();
	
	private SimpleKit defaultSimpleKit = null;
	
	private Map<KitType, Kit> defaultKit;
	private Map<KitType, List<Kit>> disabledKits;
	
	private boolean timeInWaiting = true;
	
	private boolean spectatorEnabled = true;
	private boolean joinEnabled;
	
	private boolean finalBattle = true;
	private boolean forceWin = true;
	
	private boolean build = true;
	private boolean place = true;
	
	private Group spectatorGroup;
	private Group respawnGroup;
	private Group kitSpawnGroup;
	
	private String title = "§b§lDOUBLE KIT";
	
	public ServerConfig() {
		defaultKit = new HashMap<>();
		disabledKits = new HashMap<>();
		
		spectatorGroup = Group.LIGHT;
		respawnGroup = Group.BLIZZARD;
		kitSpawnGroup = Group.SAINT;
	}
	
	public boolean isDisabled(Kit kit, KitType kitType) {
		return disabledKits.computeIfAbsent(kitType, v -> new ArrayList<>()).contains(kit);
	}
	
	public void disableKit(Kit kit, KitType kitType) {
		if (isDisabled(kit, kitType))
			return;
		
		disabledKits.computeIfAbsent(kitType, v -> new ArrayList<>()).add(kit);
	}
	
	public void enableKit(Kit kit, KitType kitType) {
		if (!isDisabled(kit, kitType))
			return;
		
		disabledKits.computeIfAbsent(kitType, v -> new ArrayList<>()).remove(kit);
	}
	
	public boolean hasPrimaryKit() {
		return defaultKit.containsKey(KitType.PRIMARY);
	}
	
	public boolean hasSecondaryKit() {
		return defaultKit.containsKey(KitType.SECONDARY);
	}
	
	public boolean hasDefaultSimpleKit() {
		return defaultSimpleKit != null;
	}

}
