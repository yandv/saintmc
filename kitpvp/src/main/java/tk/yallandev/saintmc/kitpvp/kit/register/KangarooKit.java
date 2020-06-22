package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KangarooKit extends Kit {
	
	private List<Player> kangaroodj = new ArrayList<>();

	public KangarooKit() {
		super("Kangaroo", "Use o seu foguete para movimentar-se mais rapidamente pelo mapa", Material.FIREWORK, Arrays.asList(new ItemBuilder().name("§aKangaroo").type(Material.FIREWORK).build()));
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
		
		if (!isAbilityItem(item))
			return;
		
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		
		if (CooldownController.getInstance().hasCooldown(p, getName())) {
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
		
		addCooldown(kangaroo, 4l);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
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
	
}