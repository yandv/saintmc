package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BlinkAbility extends Ability {

	private Map<UUID, Integer> uses;

	public BlinkAbility() {
		super("Blink", Arrays.asList(new ItemBuilder().type(Material.NETHER_STAR).name("§aBlink").build())	);
		uses = new HashMap<>();
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

		Block block = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(5.0D)).getBlock();

		if (block.getY() > 132) {
			player.sendMessage("§cVocê não pode usar o blink depois da altura!");
			return;
		}

		int used = uses.computeIfAbsent(player.getUniqueId(), v -> 0) + 1;

		if (used >= 4) {
			addCooldown(player, 15);
			uses.remove(player.getUniqueId());
			return;
		}

		uses.put(player.getUniqueId(), used);

		if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
			block.getRelative(BlockFace.DOWN).setType(Material.LEAVES);
		}

		ActionBarAPI.send(player, "§aUsos restantes: " + (3 - used));

		player.teleport(new Location(player.getWorld(), block.getX(), block.getY(), block.getZ(),
				player.getLocation().getYaw(), player.getLocation().getPitch()));
		player.setFallDistance(0.0F);
		player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 50.0F);
	}

}
