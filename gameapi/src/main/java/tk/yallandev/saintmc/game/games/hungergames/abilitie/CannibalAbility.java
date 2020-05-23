package 	tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class CannibalAbility extends Ability implements Disableable {
	
	public CannibalAbility() {
		super(new ItemStack(Material.RAW_FISH), AbilityRarity.COMMON);
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

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 15;
	}

}
