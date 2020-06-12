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

public class SnailKit extends Kit {

	public SnailKit() {
		super("Snail", "Deixe seus inimigos mais lentos ao encosta-los", Material.WEB,
				new ArrayList<>());
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
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
			}
		}
	}

}
