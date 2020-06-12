package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class ViperKit extends Kit {

	public ViperKit() {
		super("Viper", "Envenene seus inimigos ao encosta-los", Material.SPIDER_EYE, new ArrayList<>());
	}
	
	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		
		if (!hasAbility(damager))
			return;
		
		Random r = new Random();
		Player damaged = (Player) event.getEntity();
		if (damaged instanceof Player) {
			if (r.nextInt(4) == 0) {
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
			}
		}
	}

}
