package tk.yallandev.saintmc.skwyars.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;
import tk.yallandev.saintmc.skwyars.utils.ClassGetter;

public class AbilityController {

	private Map<String, Kit> kitMap;

	public AbilityController() {
		kitMap = new HashMap<>();	
	}

	public void registerKits() {
		for (Class<?> clazz : ClassGetter.getClassesForPackage(GameMain.getInstance().getClass(),
				"tk.yallandev.saintmc.skwyars.game.kit.register")) {
			if (Kit.class.isAssignableFrom(clazz)) {
				try {
					Kit kit = (Kit) clazz.newInstance();
					kitMap.put(kit.getName().toLowerCase(), kit);
					System.out.println(kit.getClass().getSimpleName());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Kit getKitByName(String kitName) {
		return kitMap.get(kitName.toLowerCase());
	}

	public Collection<Kit> getKits() {
		return kitMap.values();
	}

}
