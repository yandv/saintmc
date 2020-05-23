package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class StomperAbility extends Ability implements Disableable {

	public StomperAbility() {
		super(new ItemStack(Material.ANVIL), AbilityRarity.MYSTIC);
		options.put("RADIUS", new CustomOption("RADIUS", new ItemStack(Material.BEACON), 1, 2, 5, 8));
		options.put("POWER", new CustomOption("POWER", new ItemStack(Material.BLAZE_POWDER), 1, 8, 10, 12));
	}

	@EventHandler
	public void onStomper(EntityDamageEvent event) {
		Entity entityStomper = event.getEntity();
		
		if (!(entityStomper instanceof Player))
			return;
		
		Player stomper = (Player) entityStomper;
		Gamer gamer = GameMain.getPlugin().getGamerManager().getGamer(stomper.getUniqueId());
		
		if (!hasAbility(stomper))
			return;
		
		if (gamer.isGamemaker() || gamer.isSpectator())
			return;
		
		DamageCause cause = event.getCause();
		
		if (cause != DamageCause.FALL)
			return;
		
		double dmg = event.getDamage();
		CustomOption RADIUS = getOption(stomper, "RADIUS");
		CustomOption POWER = getOption(stomper, "POWER");
		
		for (Player stompado : Bukkit.getOnlinePlayers()) {
			if (stompado.getUniqueId() == stomper.getUniqueId())
				continue;
			
			if (stompado.getLocation().distance(stomper.getLocation()) > RADIUS.getValue())
				continue;
			
			double dmg2 = dmg * (POWER.getValue() / 10d);
			
			if (stompado.isSneaking() && dmg2 > 8)
				dmg2 = 8;
			
			stompado.damage(dmg2, stomper);
		}
		
		stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1, 1);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getCause() != DamageCause.FALL)
			return;
		
		Player p = (Player) event.getEntity();
		
		if (event.getDamage() < 4.0D)
			return;
		
		if (hasAbility(p)) {
			event.setCancelled(true);
			p.damage(4.0D);
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return getOption("RADIUS", map).getValue() * 4 + getOption("POWER", map).getValue() * 4;
	}

}
