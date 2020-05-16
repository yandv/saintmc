package tk.yallandev.saintmc.bukkit.api.cooldown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.cooldown.event.CooldownFinishEvent;
import tk.yallandev.saintmc.bukkit.api.cooldown.event.CooldownStartEvent;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.ItemCooldown;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class CooldownAPI implements Listener {

	private static final char CHAR = '|';
	private static final Map<UUID, List<Cooldown>> map = new ConcurrentHashMap<>();

	public static void addCooldown(Player player, Cooldown cooldown) {
		CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());

			List<Cooldown> l = list.stream().filter(c -> c.getName().equalsIgnoreCase(cooldown.getName()))
					.collect(Collectors.toList());

			if (l.size() == 0)
				list.add(cooldown);
			else
				for (Cooldown cool : list)
					cool.update(cooldown.getDuration(), cooldown.getStartTime());
		}
	}
	
	public static void addCooldown(UUID uuid, String name, long duration) {
		Player player = Bukkit.getPlayer(uuid);
		
		if (player == null)
			return;
		
		Cooldown cooldown = new Cooldown(name, duration);
		
		CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());

			List<Cooldown> l = list.stream().filter(c -> c.getName().equalsIgnoreCase(cooldown.getName()))
					.collect(Collectors.toList());

			if (l.size() == 0)
				list.add(cooldown);
			else
				for (Cooldown cool : list)
					cool.update(cooldown.getDuration(), cooldown.getStartTime());
		}
	}

	public static boolean removeCooldown(Player player, String name) {
		if (map.containsKey(player.getUniqueId())) {
			List<Cooldown> list = map.get(player.getUniqueId());
			Iterator<Cooldown> it = list.iterator();
			while (it.hasNext()) {
				Cooldown cooldown = it.next();
				if (cooldown.getName().equals(name)) {
					it.remove();
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean hasCooldown(Player player, String name) {
		if (map.containsKey(player.getUniqueId())) {
			List<Cooldown> list = map.get(player.getUniqueId());
			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return true;
		}
		return false;
	}
	
	public static boolean hasCooldown(UUID uniqueId, String name) {
		if (map.containsKey(uniqueId)) {
			List<Cooldown> list = map.get(uniqueId);
			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return true;
		}
		return false;
	}
	
	public static Cooldown getCooldown(UUID uniqueId, String name) {
		if (map.containsKey(uniqueId)) {
			List<Cooldown> list = map.get(uniqueId);
			
			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return cooldown;
		}
		return null;
	}
	
	public static String getCooldownFormated(UUID uniqueId, String name) {
		Cooldown cooldown = getCooldown(uniqueId, name);
		
		if (cooldown == null) {
			return "1 segundo";
		}
		
		return "§c§l> §fO §c" + name + "§f está em cooldown de §c" + DateUtils.formatDifference((long)cooldown.getRemaining()) + "§f!";
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.TICK)
			return;

		if (event.getCurrentTick() % 2 > 0)
			return;

		for (UUID uuid : map.keySet()) {
			Player player = Bukkit.getPlayer(uuid);

			if (player != null) {
				List<Cooldown> list = map.get(uuid);
				Iterator<Cooldown> it = list.iterator();

				/* Found Cooldown */
				Cooldown found = null;
				while (it.hasNext()) {
					Cooldown cooldown = it.next();
					if (!cooldown.expired()) {
						if (cooldown instanceof ItemCooldown) {
							ItemStack hand = player.getItemInHand();
							if (hand != null && hand.getType() != Material.AIR) {
								ItemCooldown item = (ItemCooldown) cooldown;
								if (hand.equals(item.getItem())) {
									item.setSelected(true);
									found = item;
									break;
								}
							}
							continue;
						}
						found = cooldown;
						continue;
					}
					it.remove();
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
					CooldownFinishEvent e = new CooldownFinishEvent(player, cooldown);
					Bukkit.getServer().getPluginManager().callEvent(e);
				}

				/* Display Cooldown */
				if (found != null) {
					display(player, found);
				} else if (list.isEmpty()) {
					ActionBarAPI.send(player, " ");
					map.remove(uuid);
				} else {
					Cooldown cooldown = list.get(0);
					if (cooldown instanceof ItemCooldown) {
						ItemCooldown item = (ItemCooldown) cooldown;
						if (item.isSelected()) {
							item.setSelected(false);
							ActionBarAPI.send(player, " ");
						}
					}
				}
			}
		}
	}

	private void display(Player player, Cooldown cooldown) {
		StringBuilder bar = new StringBuilder();
		double percentage = cooldown.getPercentage();
		double count = 20 - Math.max(percentage > 0D ? 1 : 0, percentage / 5);

		for (int a = 0; a < count; a++)
			bar.append("§a" + CHAR);
		for (int a = 0; a < 20 - count; a++)
			bar.append("§c" + CHAR);

		ActionBarAPI.send(player, "§eCooldown " + bar.toString());
	}

}
