package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BlacksmithAbility extends Ability {

	public BlacksmithAbility() {
		super("blacksmith", Arrays.asList(new ItemBuilder().type(Material.ANVIL).name("§aBlacksmith").build()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!hasAbility(player))
			return;

		ItemStack itemStack = event.getItem();

		if (!isAbilityItem(itemStack))
			return;

		event.setCancelled(true);

		if (isCooldown(player))
			return;

		for (ItemStack item : Stream.concat(Stream.of(player.getInventory().getArmorContents()),
				Stream.of(player.getInventory().getContents())).toArray(ItemStack[]::new)) {
			if (item == null)
				continue;

			Material material = item.getType();

			if (material.name().contains("SWORD") || material.name().contains("CHESTPLATE")
					|| material.name().contains("HELMET") || material.name().contains("BOOTS")
					|| material.name().contains("LEGGINGS") || material.name().contains("AXE")
					|| material.name().contains("PICKAXE") || material.name().contains("SPADE"))
				item.setDurability((short) 0);
		}

		addCooldown(player, 420);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isAbilityItem(event.getItemInHand())) {
			event.setCancelled(true);
		}
	}

}
