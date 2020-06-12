package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class SwitcherKit extends Kit {

	public SwitcherKit() {
		super("Switcher", "Troque de lugar com seus inimigos com sua bola de neve", Material.SNOW_BALL,
				Arrays.asList(new ItemBuilder().name("§aSwitcher").type(Material.SNOW_BALL).amount(16).build()));
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() instanceof Player && hasAbility((Player) e.getEntity().getShooter()) && e.getEntity() instanceof Snowball) {
			Player p = (Player) e.getEntity().getShooter();
			
			e.getEntity().setMetadata("switch", new FixedMetadataValue(GameMain.getPlugin(), p));
			e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(1.5D));
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		
		if (!e.getDamager().hasMetadata("switch"))
			return;
		
		Player p = (Player) e.getDamager().getMetadata("switch").get(0).value();

		if (p == null)
			return;

		if (GameMain.getInstance().getGamerManager().getGamer(e.getEntity().getUniqueId()).isSpawnProtection())
			return;

		Location loc = e.getEntity().getLocation().clone();
		e.getEntity().teleport(p.getLocation().clone());
		p.teleport(loc);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (hasAbility(event.getEntity()))
			event.getEntity().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 3));
	}

}
