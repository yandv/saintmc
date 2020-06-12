package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class ReaperKit extends Kit {

	public ReaperKit() {
		super("Reaper", "Ceife a alma de seus inimigos por alguns segundos com a sua enxada", Material.WOOD_HOE,
				Arrays.asList(new ItemBuilder().name("§aReaper").type(Material.WOOD_HOE).build()));
	}
	
	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		
		ItemStack item = damager.getItemInHand();
		
		if (!isAbilityItem(item))
			return;
		
		event.setCancelled(true);
		damager.updateInventory();
		Random r = new Random();
		Player damaged = (Player) event.getEntity();
		
		if (damaged instanceof Player) {
			if (r.nextInt(4) == 0) {
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 3 * 20, 0));
			}
		}
	}

}
