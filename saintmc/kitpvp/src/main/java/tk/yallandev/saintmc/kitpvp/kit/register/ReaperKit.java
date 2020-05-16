package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class ReaperKit extends Kit {

	public ReaperKit() {
		super("Reaper", "Ceife a alma de seus inimigos por alguns segundos com a sua enxada", Material.WOOD_HOE);
	}
	
	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		
		ItemStack item = damager.getItemInHand();
		
		if (item == null)
			return;
		
		if (item == null || item.getType() != Material.WOOD_HOE)
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

	@Override
	public void applyKit(Player player) {
		player.getInventory().setItem(1, new ItemStack(Material.WOOD_HOE));
	}

}
