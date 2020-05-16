package tk.yallandev.saintmc.game.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.GameMode;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.util.ClassGetter;

public class AbilityManager {
	private static HashMap<String, Ability> abilities = new HashMap<>();
	private static HashMap<UUID, List<String>> playerAbilities = new HashMap<>();

	public AbilityManager() {
		initializateAbilities("br.com.battlebits.game.games.hungergames.abilitie");
	}

	public void initializateAbilities(String packageName) {
		int i = 0;
		for (Class<?> abilityClass : ClassGetter.getClassesForPackage(GameMain.getPlugin(), packageName)) {
			if (Ability.class.isAssignableFrom(abilityClass)) {
				try {
					Ability abilityListener;
					
					try {
						abilityListener = (Ability) abilityClass.getConstructor(GameMode.class).newInstance(GameMain.getPlugin());
					} catch (Exception e) {
						abilityListener = (Ability) abilityClass.newInstance();
					}
					
					String abilityName = abilityListener.getClass().getSimpleName().toLowerCase().replace("ability", "");
					
					try {
						Field field = abilityListener.getClass().getSuperclass().getDeclaredField("name");
						field.setAccessible(true);
						field.set(abilityListener, abilityName);
					} catch (Exception e) {
						System.out.println("Failed to put name '" + abilityName + "' to ability '" + abilityListener.getClass().getSimpleName() + ".class'");
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
		GameMain.getPlugin().getLogger().info(i + " habilidades carregadas!");
	}

	public void registerAbilityListeners() {
		for (Ability ability : abilities.values()) {
			if (!(ability instanceof Disableable) || ability.myPlayers.size() > 0)
				Bukkit.getPluginManager().registerEvents(ability, GameMain.getPlugin());
		}
	}

	public static void registerPlayerAbility(Player player, String abilityName) {
		Ability ability = getAbility(abilityName);
		
		if (ability != null)
			ability.registerPlayer(player);
		
		getPlayerAbilities(player).add(abilityName.toLowerCase());
	}

	public void unregisterPlayerAbility(Player player, String abilityName) {
		Ability ability = getAbility(abilityName);
		if (ability != null)
			ability.unregisterPlayer(player);
		getPlayerAbilities(player).remove(abilityName.toLowerCase());
	}

	public static HashMap<String, Ability> getAbilities() {
		return abilities;
	}

	public void unregisterPlayer(Player player) {
		List<String> abilityCopy = new ArrayList<>();
		
		if (playerAbilities.containsKey(player.getUniqueId()))
			abilityCopy.addAll(playerAbilities.get(player.getUniqueId()));
		
		for (String abilityName : abilityCopy) {
			unregisterPlayerAbility(player, abilityName);
		}
		
		playerAbilities.remove(player.getUniqueId());
	}

	public static Ability getAbility(String ability) {
		if (abilities.containsKey(ability.toLowerCase()))
			return abilities.get(ability.toLowerCase());
		else
			System.out.print("Tried to find ability '" + ability + "' but failed!");
		
		return null;
	}

	public static List<String> getPlayerAbilities(Player player) {
		if (playerAbilities.containsKey(player.getUniqueId()))
			return playerAbilities.get(player.getUniqueId());
		
		playerAbilities.put(player.getUniqueId(), new ArrayList<>());
		return playerAbilities.get(player.getUniqueId());
	}

}
