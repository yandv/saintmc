package br.com.saintmc.hungergames.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.kit.PlayerSelectKitEvent;
import br.com.saintmc.hungergames.event.kit.PlayerSelectedKitEvent;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import tk.yallandev.saintmc.common.controller.StoreController;
import tk.yallandev.saintmc.common.utils.ClassGetter;

public class KitController extends StoreController<String, Kit> {
	
	private Map<String, Kit> kits = new HashMap<>();
	
	public void load(String packageName) {
		int i = 0;
		
		for (Class<?> abilityClass : ClassGetter.getClassesForPackage(GameMain.getInstance().getClass(), packageName)) {
			if (Kit.class.isAssignableFrom(abilityClass)) {
				try {
					Kit abilityListener;
					try {
						abilityListener = (Kit) abilityClass.getConstructor(GameMain.class).newInstance(GameMain.getInstance());
					} catch (Exception e) {
						abilityListener = (Kit) abilityClass.newInstance();
					}
					String kitName = abilityListener.getClass().getSimpleName().toLowerCase().replace("kit", "");
					kits.put(kitName.toLowerCase(), abilityListener);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print("Erro ao carregar o kit " + abilityClass.getSimpleName());
				}
				i++;
			}
		}
		
		GameMain.getInstance().getLogger().info(i + " kits carregados!");
	}
	
	public void selectKit(Player player, Kit kit, KitType kitType) {
		PlayerSelectKitEvent event = new PlayerSelectKitEvent(player, kit, kitType);
		Bukkit.getPluginManager().callEvent(event);
		
		if (!event.isCancelled()) {
			setKit(player, kit, kitType);
		}
	}
	
	public void setKit(Player player, Kit kit, KitType kitType) {
		GameGeneral.getInstance().getAbilityController().unregisterPlayer(player);
		kit.registerAbilities(player);
		
		GameGeneral.getInstance().getGamerController().getGamer(player).setKit(kitType, kit);
		Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(player, kit, kitType));
	}

	public void unregisterPlayer(Player player, KitType kitType) {
		GameGeneral.getInstance().getAbilityController().unregisterPlayer(player);
		GameGeneral.getInstance().getGamerController().getGamer(player).removeKit(kitType);
		Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(player, null, kitType));
	}
	
	public Kit getKit(String kitName) {
		if (kits.containsKey(kitName.toLowerCase()))
			return kits.get(kitName.toLowerCase());
		else
			System.out.print("Tried to find ability '" + kitName + "' but failed!");
		return null;
	}

	public Map<String, Kit> getKits() {
		return kits;
	}
	
	public Collection<Kit> getAllKits() {
		return kits.values();
	}

}
