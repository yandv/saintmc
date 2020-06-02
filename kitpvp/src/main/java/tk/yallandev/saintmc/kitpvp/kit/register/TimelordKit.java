package tk.yallandev.saintmc.kitpvp.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class TimelordKit extends Kit {

	public TimelordKit() {
		super("Timelord", "Pare o tempo com seu relógio", Material.WATCH);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!e.getAction().name().contains("RIGHT"))
			return;
		
		if (!hasAbility(e.getPlayer()))
			return;
		
		Player p = e.getPlayer();
		
		if (p.getItemInHand() == null || p.getItemInHand().getType() != Material.WATCH)
			return;
		
		if (CooldownController.getInstance().hasCooldown(p, getName())) {
//			p.sendMessage(CooldownAPI.getCooldownFormated(p, getName()));
			return;
		}

		for (Entity entity : e.getPlayer().getNearbyEntities(20, 20, 20)) {
			if (!(entity instanceof Player)) {
				continue;
			}
			
			if (AdminMode.getInstance().isAdmin((Player) entity))
				continue;
			
			if (GameMain.getInstance().getGamerManager().getGamer(entity.getUniqueId()).isSpawnProtection())
				return;

			((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 255), true);
			((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 250), true);
		}
		
		e.setCancelled(true);
		CooldownController.getInstance().addCooldown(p, new Cooldown(getName(), 25l));
	}

	@Override
	public void applyKit(Player player) {
		player.getInventory().setItem(1, new ItemStack(Material.WATCH));
	}

}
