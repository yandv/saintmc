package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class PhantomAbility extends Ability {

	private ItemStack helmet = new ItemBuilder().name("§aPhantom").color(Color.GREEN).type(Material.LEATHER_HELMET)
			.build();

	private ItemStack chestplate = new ItemBuilder().name("§aPhantom").color(Color.GREEN)
			.type(Material.LEATHER_CHESTPLATE).build();

	private ItemStack leggings = new ItemBuilder().name("§aPhantom").color(Color.GREEN).type(Material.LEATHER_LEGGINGS)
			.build();

	private ItemStack boots = new ItemBuilder().name("§aPhantom").color(Color.GREEN).type(Material.LEATHER_BOOTS)
			.build();

	private Map<UUID, ItemStack[]> armorContents;

	public PhantomAbility() {
		super("Phantom", Arrays.asList(new ItemBuilder().type(Material.FEATHER).name("§aPhantom").build()));
		armorContents = new HashMap<>();
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && isAbilityItem(player.getItemInHand())) {
			if (isCooldown(player))
				return;

			armorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
			player.getInventory().setHelmet(helmet);
			player.getInventory().setChestplate(chestplate);
			player.getInventory().setLeggings(leggings);
			player.getInventory().setBoots(boots);

			player.setAllowFlight(true);
			player.setFlying(true);

			new BukkitRunnable() {

				int x = 0;

				@Override
				public void run() {
					if (x == 5) {
						player.setAllowFlight(false);

						player.getInventory().setArmorContents(armorContents.get(player.getUniqueId()));
						armorContents.remove(player.getUniqueId());
						cancel();
						return;
					}

					if (x >= 2) {
						player.sendMessage("§eVocê pode voar por mais " + (5 - x) + " segundos!");
					}

					x++;
				}

			}.runTaskTimer(BukkitMain.getInstance(), 20, 20);

			addCooldown(player, 35);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (hasAbility(event.getPlayer())) {
			Player player = event.getPlayer();

			if (player.getAllowFlight()) {
				player.setAllowFlight(false);

				if (armorContents.containsKey(player.getUniqueId())) {
					player.getInventory().setArmorContents(armorContents.get(player.getUniqueId()));
					armorContents.remove(player.getUniqueId());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;

		if (isItem(helmet, event.getCurrentItem()) || isItem(chestplate, event.getCurrentItem())
				|| isItem(leggings, event.getCurrentItem()) || isItem(boots, event.getCurrentItem())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (isItem(helmet, event.getEntity().getItemStack()) || isItem(chestplate, event.getEntity().getItemStack())
				|| isItem(leggings, event.getEntity().getItemStack())
				|| isItem(boots, event.getEntity().getItemStack())) {
			event.setCancelled(true);
		}
	}

}
