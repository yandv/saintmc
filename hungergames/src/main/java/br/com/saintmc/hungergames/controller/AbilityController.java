package br.com.saintmc.hungergames.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.common.controller.StoreController;
import tk.yallandev.saintmc.common.utils.ClassGetter;

public class AbilityController extends StoreController<String, Ability> {

	private Map<String, Ability> abilities;

	public AbilityController() {
		abilities = new HashMap<>();
//		playerAbilities = new HashMap<>();
	}

	public void load(String packageName) {
		int i = 0;
		for (Class<?> abilityClass : ClassGetter.getClassesForPackage(GameMain.getInstance().getClass(), packageName)) {
			if (Ability.class.isAssignableFrom(abilityClass)) {
				try {
					Ability abilityListener;

					try {
						abilityListener = (Ability) abilityClass.getConstructor(GameMode.class)
								.newInstance(GameMain.getInstance());
					} catch (Exception e) {
						abilityListener = (Ability) abilityClass.newInstance();
					}

					String abilityName = abilityListener.getClass().getSimpleName().toLowerCase().replace("ability",
							"");

					try {
						Field field = abilityListener.getClass().getSuperclass().getDeclaredField("name");
						field.setAccessible(true);
						field.set(abilityListener, abilityName);
					} catch (Exception e) {
						System.out.println("Failed to put name '" + abilityName + "' to ability '"
								+ abilityListener.getClass().getSimpleName() + ".class'");
						e.printStackTrace();
					}

					abilities.put(abilityName, abilityListener);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print("Erro ao carregar a habilidade " + abilityClass.getSimpleName());
				}
				i++;
			}
		}
		GameMain.getInstance().getLogger().info(i + " habilidades carregadas!");
	}

	public void registerAbilityListeners() {
		for (Ability ability : abilities.values())
			if (ability.getMyPlayers().size() > 0)
				Bukkit.getPluginManager().registerEvents(ability, GameMain.getInstance());
	}

	public void registerPlayerAbility(Player player, String abilityName) {
		Ability ability = getAbility(abilityName);

		if (ability != null)
			ability.registerPlayer(player);
	}

	public void unregisterPlayerAbility(Player player, String abilityName) {
		Ability ability = getAbility(abilityName);
		
		if (ability != null)
			ability.unregisterPlayer(player);
	}

	public Map<String, Ability> getAbilities() {
		return abilities;
	}

	public Ability getAbility(String ability) {
		if (abilities.containsKey(ability.toLowerCase()))
			return abilities.get(ability.toLowerCase());
		else
			System.out.print("Tried to find ability '" + ability + "' but failed!");

		return null;
	}

}
