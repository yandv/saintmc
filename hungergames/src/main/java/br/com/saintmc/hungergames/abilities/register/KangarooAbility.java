package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
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

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

@SuppressWarnings("deprecation")
public class KangarooAbility extends Ability {

	private List<Player> jumpList;

	public KangarooAbility() {
		super("Kangaroo", Arrays.asList(new ItemBuilder().name("§aKangaroo").type(Material.FIREWORK).build()));
		jumpList = new ArrayList<>();
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
		
		if (!isAbilityItem(item))
			return;
		
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		
		item.setDurability((short) 0);
		p.updateInventory();
		
		if (isCooldown(p))
			return;
		
		if (p.isOnGround()) {
			if (!p.isSneaking()) {
				Vector vector = p.getEyeLocation().getDirection();
				vector.multiply(0.6F);
				vector.setY(1.0F);
				p.setVelocity(vector);
				if (jumpList.contains(p)) {
					jumpList.remove(p);
				}
			} else {
				Vector vector = p.getEyeLocation().getDirection();
				vector.multiply(1.5D);
				vector.setY(0.55F);
				p.setVelocity(vector);
				if (jumpList.contains(p)) {
					jumpList.remove(p);
				}
			}
		} else {
			if (!jumpList.contains(p)) {
				if (!p.isSneaking()) {
					Vector vector = p.getEyeLocation().getDirection();
					vector.multiply(0.6F);
					vector.setY(1.0F);
					p.setVelocity(vector);
					jumpList.add(p);
				} else {
					Vector vector = p.getEyeLocation().getDirection();
					vector.multiply(1.5D);
					vector.setY(0.55F);
					p.setVelocity(vector);
					jumpList.add(p);
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (jumpList.contains(event.getPlayer()))
			jumpList.remove(event.getPlayer());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (!hasAbility(p))
			return;
		
		if (!jumpList.contains(p))
			return;
		
		if (!p.isOnGround())
			return;
		
		jumpList.remove(p);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (GameState.isInvincibility(GameGeneral.getInstance().getGameState()))
			return;
		
		Player kangaroo = (Player) event.getEntity();
		
		if (!hasAbility(kangaroo))
			return;
		
		addCooldown(kangaroo.getUniqueId(), 7);
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

}
