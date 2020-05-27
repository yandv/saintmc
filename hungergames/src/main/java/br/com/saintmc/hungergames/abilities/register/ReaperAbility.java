package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class ReaperAbility extends Ability {

	public ReaperAbility() {
		super("Reaper", Arrays.asList(new ItemBuilder().type(Material.WOOD_HOE).name(ChatColor.GOLD + "Reaper").build()));
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = (Player) event.getDamager();
		
		if (!hasAbility(damager))
			return;
		
		ItemStack item = damager.getItemInHand();
		
		if (item == null)
			return;
		
		if (!isAbilityItem(item))
			return;
		
		event.setCancelled(true);
		item.setDurability((short) 0);
		damager.updateInventory();
		Random r = new Random();
		Player damaged = (Player) event.getPlayer();
		
		if (damaged instanceof Player) {
			if (r.nextInt(3) == 0) {
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 0));
			}
		}
	}
}
