package tk.yallandev.saintmc.game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.event.player.PlayerSelectKitEvent;
import tk.yallandev.saintmc.game.event.player.PlayerSelectedKitEvent;
import tk.yallandev.saintmc.game.util.ClassGetter;

public class KitManager {

	private static HashMap<UUID, Kit> playersCurrentKit = new HashMap<>();

	private static HashMap<String, Kit> kits = new HashMap<>();
	private static List<Kit> allKits = new ArrayList<>();

	public KitManager() {
		initializateKits("br.com.battlebits.game.games.hungergames.kit");
	}

	public void initializateKits(String packageName) {
		int i = 0;
		for (Class<?> abilityClass : ClassGetter.getClassesForPackage(GameMain.getPlugin(), packageName)) {
			if (Kit.class.isAssignableFrom(abilityClass)) {
				try {
					Kit abilityListener;
					try {
						abilityListener = (Kit) abilityClass.getConstructor(GameMain.class).newInstance(GameMain.getPlugin());
					} catch (Exception e) {
						abilityListener = (Kit) abilityClass.newInstance();
					}
					String kitName = abilityListener.getClass().getSimpleName().toLowerCase().replace("kit", "");
					allKits.add(abilityListener);
					kits.put(kitName.toLowerCase(), abilityListener);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print("Erro ao carregar o kit " + abilityClass.getSimpleName());
				}
				i++;
			}
		}
		GameMain.getPlugin().getLogger().info(i + " kits carregados!");
	}

	public static HashMap<String, Kit> getKits() {
		return kits;
	}

	public static Kit getKit(String kitName) {
		if (kits.containsKey(kitName.toLowerCase()))
			return kits.get(kitName.toLowerCase());
		else
			System.out.print("Tried to find ability '" + kitName + "' but failed!");
		return null;
	}
	
	public static List<Kit> getAllKits() {
		return allKits;
	}

	public Kit getPlayerKit(Player player) {
		return playersCurrentKit.containsKey(player.getUniqueId()) ? playersCurrentKit.get(player.getUniqueId()) : null;
	}

	public Kit getPlayerKit(UUID uuid) {
		return playersCurrentKit.containsKey(uuid) ? playersCurrentKit.get(uuid) : null;
	}

	public void selectKit(Player player, Kit kit) {
		PlayerSelectKitEvent event = new PlayerSelectKitEvent(player, kit);
		Bukkit.getPluginManager().callEvent(event);
		
		if (!event.isCancelled()) {
			GameMain.getPlugin().getAbilityManager().unregisterPlayer(player);
			kit.loadAbilities(player);
			playersCurrentKit.put(player.getUniqueId(), kit);
			Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(player, kit));
		}
	}
	
	public void setKit(Player player, Kit kit) {
		GameMain.getPlugin().getAbilityManager().unregisterPlayer(player);
		kit.loadAbilities(player);
		playersCurrentKit.put(player.getUniqueId(), kit);
		Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(player, kit));
	}

	public void unregisterPlayer(Player player) {
		GameMain.getPlugin().getAbilityManager().unregisterPlayer(player);
		playersCurrentKit.remove(player.getUniqueId());
		Bukkit.getPluginManager().callEvent(new PlayerSelectedKitEvent(player, null));
	}
	
	public static HashMap<UUID, Kit> getPlayersCurrentKit() {
		return playersCurrentKit;
	}

}
