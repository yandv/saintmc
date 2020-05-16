package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.battlebits.game.GameMain;
import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.interfaces.Disableable;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.stage.GameStage;

public class SwitcherAbility extends Ability implements Disableable {

	public SwitcherAbility() {
		super(new ItemBuilder().type(Material.SNOW_BALL).build(), AbilityRarity.RARE);
		options.put("ITEM", new CustomOption("ITEM", new ItemBuilder().type(Material.SNOW_BALL).amount(16).build(), "�aSwitcher"));
	}
	
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() instanceof Player && hasAbility((Player) e.getEntity().getShooter()) && e.getEntity() instanceof Snowball) {
			Player p = (Player) e.getEntity().getShooter();
			
			if (GameStage.isInvincibility(GameMain.getPlugin().getGameStage())) {
				e.setCancelled(true);
				ItemStack item = getOption("ITEM").getItemStack();
				item.setAmount(1);
				p.getInventory().addItem(item);
				return;
			}
			
			e.getEntity().setMetadata("switch", new FixedMetadataValue(GameMain.getPlugin(), p));
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

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 20;
	}

}
