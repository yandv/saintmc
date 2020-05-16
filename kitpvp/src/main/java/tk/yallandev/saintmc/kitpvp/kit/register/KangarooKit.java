package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KangarooKit extends Kit {
	
	private ArrayList<Player> kangaroodj = new ArrayList<>();

	public KangarooKit() {
		super("Kangaroo", "Use o seu foguete para movimentar-se mais rapidamente pelo mapa", Material.FIREWORK);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		Action a = event.getAction();
		ItemStack item = p.getItemInHand();
		
		if (!a.name().contains("RIGHT") && !a.name().contains("LEFT"))
			return;
		
		if (!hasAbility(p))
			return;
		
		if (item == null)
			return;
		
		if (item.getType() != Material.FIREWORK)
			return;
		
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		
		if (CooldownAPI.hasCooldown(p, getName())) {
//			p.sendMessage(GameMain.getPlugin().getCooldownManager().getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}
		
		if (((Entity)p).isOnGround()) {
			if (!p.isSneaking()) {
				Vector vector = p.getEyeLocation().getDirection();
				vector.multiply(0.6F);
				vector.setY(1.0F);
				p.setVelocity(vector);
				if (kangaroodj.contains(p)) {
					kangaroodj.remove(p);
				}
			} else {
				Vector vector = p.getEyeLocation().getDirection();
				vector.multiply(1.5D);
				vector.setY(0.55F);
				p.setVelocity(vector);
				if (kangaroodj.contains(p)) {
					kangaroodj.remove(p);
				}
			}
		} else {
			if (!kangaroodj.contains(p)) {
				if (!p.isSneaking()) {
					Vector vector = p.getEyeLocation().getDirection();
					vector.multiply(0.6F);
					vector.setY(1.0F);
					p.setVelocity(vector);
					kangaroodj.add(p);
				} else {
					Vector vector = p.getEyeLocation().getDirection();
					vector.multiply(1.5D);
					vector.setY(0.55F);
					p.setVelocity(vector);
					kangaroodj.add(p);
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (kangaroodj.contains(event.getPlayer()))
			kangaroodj.remove(event.getPlayer());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (!hasAbility(p))
			return;
		
		if (!kangaroodj.contains(p))
			return;
		
		if (!((Entity)p).isOnGround())
			return;
		
		kangaroodj.remove(p);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player kangaroo = (Player) event.getEntity();
		
		if (!hasAbility(kangaroo))
			return;
		
		CooldownAPI.addCooldown(kangaroo, new Cooldown(getName(), 4l));
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getCause() != DamageCause.FALL)
			return;
		
		Player p = (Player) event.getEntity();
		
		if (event.getDamage() < 7.0D)
			return;
		
		if (hasAbility(p)) {
			event.setCancelled(true);
			p.damage(7.0D);
		}
	}
	
	@Override
	public void applyKit(Player player) {
		player.getInventory().setItem(1, new ItemStack(Material.FIREWORK));
	}
	
}