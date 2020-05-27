package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class SwitcherAbility extends Ability {

	public SwitcherAbility() {
		super("Switcher", Arrays.asList(new ItemBuilder().type(Material.SNOW_BALL).name("§aSwitcher").amount(16).build()));
	}
	
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() instanceof Player && hasAbility((Player) e.getEntity().getShooter()) && e.getEntity() instanceof Snowball) {
			Player p = (Player) e.getEntity().getShooter();
			
			if (GameState.isInvincibility(GameGeneral.getInstance().getGameState())) {
				e.setCancelled(true);
				ItemStack item = new ItemBuilder().type(Material.SNOW_BALL).name("§aSwitcher").amount(16).build();
				item.setAmount(1);
				p.getInventory().addItem(item);
				return;
			}
			
			e.getEntity().setMetadata("switch", new FixedMetadataValue(GameMain.getInstance(), p));
			e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(1.5D));
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager().hasMetadata("switch")) {
			Player p = (Player) e.getDamager().getMetadata("switch").get(0).value();
			
			if (p == null)
				return;
			
			Location loc = e.getEntity().getLocation().clone();
			e.getEntity().teleport(p.getLocation().clone());
			p.teleport(loc);
		}
	}

}
