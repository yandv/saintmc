package tk.yallandev.saintmc.bukkit.api.cooldown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.cooldown.event.CooldownFinishEvent;
import tk.yallandev.saintmc.bukkit.api.cooldown.event.CooldownStartEvent;
import tk.yallandev.saintmc.bukkit.api.cooldown.event.CooldownStopEvent;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.ItemCooldown;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.bukkit.api.listener.RegisterableListener;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

/**
 * 
 * Store and display like actionbar for players all Cooldown
 * 
 * Based on {@link https://gitlab.com/Battlebits/} CooldownAPI
 * 
 * @author yandv
 *
 */

public class CooldownController implements Listener {

	@Getter
	private static CooldownController instance = new CooldownController();

	private static final char CHAR = '|';

	private Map<UUID, List<Cooldown>> map;
	private RegisterableListener listener;

	public CooldownController() {
		map = new ConcurrentHashMap<>();
		listener = new CooldownListener();
	}

	/**
	 * Add cooldown to player
	 * 
	 * @param player
	 * @param cooldown
	 */

	public void addCooldown(Player player, Cooldown cooldown) {
		CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());

			boolean add = true;

			for (Cooldown cool : list) {
				if (cool.getName().equals(cooldown.getName())) {
					cool.update(cooldown.getDuration(), cooldown.getStartTime());
					add = false;
				}
			}

			if (cooldown instanceof ItemCooldown) {
				try {
					BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
							.getMember(player.getUniqueId());

					if (member.getCustomClient() != null)
						member.getCustomClient().sendCooldown(cooldown.getName(),
								((ItemCooldown) cooldown).getItem().getType(), (int) cooldown.getDuration());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (add)
				list.add(cooldown);

			if (!map.isEmpty())
				listener.registerListener();
		}
	}

	/**
	 * Add cooldown to player
	 * 
	 * @param player
	 * @param cooldown
	 */

	public void addCooldown(UUID uuid, String name, long duration) {
		Player player = Bukkit.getPlayer(uuid);

		if (player == null)
			return;

		Cooldown cooldown = new Cooldown(name, duration);

		CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());

			boolean add = true;

			for (Cooldown cool : list) {
				if (cool.getName().equals(cooldown.getName())) {
					cool.update(cooldown.getDuration(), cooldown.getStartTime());
					add = false;
				}
			}

			if (add)
				list.add(cooldown);

			if (!map.isEmpty())
				listener.registerListener();
		}
	}

	/**
	 * Remove player cooldown
	 * 
	 * @param player
	 * @param cooldown
	 */

	public boolean removeCooldown(Player player, String name) {
		if (map.containsKey(player.getUniqueId())) {
			List<Cooldown> list = map.get(player.getUniqueId());
			Iterator<Cooldown> it = list.iterator();
			while (it.hasNext()) {
				Cooldown cooldown = it.next();

				if (cooldown.getName().equals(name)) {
					it.remove();
					Bukkit.getPluginManager().callEvent(new CooldownStopEvent(player, cooldown));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * Check if player has cooldown
	 * 
	 * @param player
	 * @param name
	 * @return boolean
	 */

	public boolean hasCooldown(Player player, String name) {
		if (map.containsKey(player.getUniqueId())) {
			List<Cooldown> list = map.get(player.getUniqueId());
			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return true;
		}
		return false;
	}

	/**
	 *
	 * Check if uniqueId has cooldown
	 * 
	 * @param uniqueId
	 * @param name
	 * @return
	 */

	public boolean hasCooldown(UUID uniqueId, String name) {
		if (map.containsKey(uniqueId)) {
			List<Cooldown> list = map.get(uniqueId);
			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return true;
		}
		return false;
	}

	/**
	 * 
	 * Return the cooldown of player, if the player not have cooldown will return
	 * null
	 * 
	 * @param uniqueId
	 * @param name
	 * @return
	 */

	public Cooldown getCooldown(UUID uniqueId, String name) {
		if (map.containsKey(uniqueId)) {
			List<Cooldown> list = map.get(uniqueId);

			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return cooldown;
		}
		return null;
	}

	public class CooldownListener extends ManualRegisterableListener {

		@EventHandler
		public void onUpdate(UpdateEvent event) {
			if (event.getType() != UpdateType.TICK)
				return;

			if (event.getCurrentTick() % 5 == 0)
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

						if (!(cooldown instanceof ItemCooldown))
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

		@EventHandler
		public void onCooldown(CooldownStopEvent event) {
			if (map.isEmpty()) {
				unregisterListener();
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

			ActionBarAPI.send(player, "§f" + cooldown.getName() + " " + bar.toString() + " §f"
					+ (CommonConst.DECIMAL_FORMAT.format(cooldown.getRemaining())) + " segundos");
		}
	}

}
