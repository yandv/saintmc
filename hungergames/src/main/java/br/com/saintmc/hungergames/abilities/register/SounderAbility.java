package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;
import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class SounderAbility extends Ability {

	private ItemStack helmet;
	private Map<Player, Sounder> playerMap;

	public SounderAbility() {
		super("Sounder", Arrays.asList(new ItemBuilder().name("§aSounder").type(Material.NOTE_BLOCK).build()));
		helmet = new ItemBuilder().name("§aSounder").type(Material.JUKEBOX).build();
		playerMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.getRightClicked() instanceof Player && isAbilityItem(player.getItemInHand())) {
			if (isCooldown(player))
				return;

			Player entity = (Player) event.getRightClicked();

			playerMap.put(entity, new Sounder(entity.getInventory().getHelmet()));
			entity.getInventory().setHelmet(helmet);
			entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 255));
			addCooldown(player, 30);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (playerMap.containsKey(event.getPlayer()))
			playerMap.remove(event.getPlayer());
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.TICK) {
			if (event.getCurrentTick() % 3 == 0) {
				Iterator<Entry<Player, Sounder>> iterator = playerMap.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<Player, Sounder> entry = iterator.next();

					if (entry.getValue().time < System.currentTimeMillis()) {
						entry.getKey().getInventory().setHelmet(entry.getValue().helmet);
						iterator.remove();
					} else {
						entry.getKey().getLocation().getWorld()
								.playEffect(entry.getKey().getLocation().clone().add(0, 2.5, 0), Effect.NOTE, 1);
						entry.getKey().damage(1d);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (hasAbility(player))
			if (isAbilityItem(e.getItem()) && e.getAction() != Action.PHYSICAL) {
				player.updateInventory();
				e.setCancelled(true);
			}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;

		if (isItem(helmet, event.getCurrentItem())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (isItem(helmet, event.getEntity().getItemStack()))
			event.setCancelled(true);
	}

	@RequiredArgsConstructor
	public class Sounder {

		private final ItemStack helmet;
		private long time = System.currentTimeMillis() + 4000l;

	}
}
