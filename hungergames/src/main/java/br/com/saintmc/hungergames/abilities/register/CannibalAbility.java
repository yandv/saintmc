package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.abilities.Ability;

public class CannibalAbility extends Ability {
	
	public CannibalAbility() {
		super("Cannibal", new ArrayList<>());
	}
	
	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player && hasAbility((Player) e.getDamager()) && new Random().nextInt(100) <= 20) {
			((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 1), true);
			Player p = (Player) e.getDamager();
			int fome = p.getFoodLevel();
			fome++;
			
			if (fome <= 20) {
				p.setFoodLevel(fome);
			}
		}
	}

}
