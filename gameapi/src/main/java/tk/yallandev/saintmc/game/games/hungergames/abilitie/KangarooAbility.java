package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.ArrayList;
import java.util.HashMap;

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

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.stage.GameStage;

@SuppressWarnings("deprecation")
public class KangarooAbility extends Ability implements Disableable {

	private ArrayList<Player> kangaroodj;

	public KangarooAbility() {
		super(new ItemStack(Material.FIREWORK), AbilityRarity.MYSTIC);
		kangaroodj = new ArrayList<>();
		options.put("VECTOR_MULTIPLY", new CustomOption("VECTOR_MULTIPLY", new ItemStack(Material.NETHER_STAR), 1, 8, 10, 12));
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), -1, 0, 5, 10));
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.FIREWORK), "§aKangaroo"));
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
		
		ItemStack KANGAROO_ITEM = getOption(p, "ITEM").getItemStack();
		
		if (!ItemUtils.isEquals(item, KANGAROO_ITEM))
			return;
		
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		
		item.setDurability(KANGAROO_ITEM.getDurability());
		p.updateInventory();
		
		if (CooldownController.hasCooldown(p.getUniqueId(), getName())) {
			p.sendMessage(CooldownController.getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}
		
		if (p.isOnGround()) {
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
		
		if (!p.isOnGround())
			return;
		
		kangaroodj.remove(p);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (GameStage.isInvincibility(GameMain.getPlugin().getGameStage()))
			return;
		
		Player kangaroo = (Player) event.getEntity();
		
		if (!hasAbility(kangaroo))
			return;
		
		if (getOption(kangaroo, "COOLDOWN").getValue() <= 0)
			return;
		
		CooldownController.addCooldown(kangaroo.getUniqueId(), getName(), getOption(kangaroo, "COOLDOWN").getValue());
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
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return (3 * getOption("VECTOR_MULTIPLY", map).getValue()) + (40 - (4 * (getOption("COOLDOWN", map).getValue())));
	}

}
