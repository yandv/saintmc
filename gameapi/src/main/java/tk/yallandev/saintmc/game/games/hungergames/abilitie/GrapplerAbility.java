package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.games.hungergames.abilitie.constructor.GrapplingHook;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class GrapplerAbility extends Ability implements Disableable {

	private Map<UUID, GrapplingHook> grapplerHooks;

	public GrapplerAbility() {
		super(new ItemStack(Material.LEASH), AbilityRarity.MYSTIC);
		grapplerHooks = new HashMap<>();
		options.put("VECTOR_MULTIPLY", new CustomOption("VECTOR_MULTIPLY", new ItemStack(Material.NETHER_STAR), 1, 8, 10, 12));
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), -1, 0, 5, 10));
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.LEASH), "§aGrappler"));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractListener(PlayerInteractEvent event) {
		if (!hasAbility(event.getPlayer()))
			return;
		if (event.getItem() == null)
			return;
		Action a = event.getAction();
		Player p = event.getPlayer();
		ItemStack item = p.getItemInHand();
		ItemStack ITEM = getOption(p, "ITEM").getItemStack();
		if (!ItemUtils.isEquals(item, ITEM))
			return;
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		item.setDurability(ITEM.getDurability());
		p.updateInventory();
		
		if (CooldownAPI.hasCooldown(p, getName())) {
			p.sendMessage(CooldownAPI.getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}
		
		if (event.getAction().name().contains("LEFT")) {
			if (grapplerHooks.containsKey(p.getUniqueId())) {
				grapplerHooks.get(p.getUniqueId()).remove();
				grapplerHooks.remove(p.getUniqueId());
			}
			GrapplingHook hook = new GrapplingHook(p.getWorld(), ((CraftPlayer) p).getHandle());
			Vector direction = p.getLocation().getDirection();
			hook.spawn(p.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()));
			hook.move(direction.getX() * 5.0D, direction.getY() * 5.0D, direction.getZ() * 5.0D);
			grapplerHooks.put(p.getUniqueId(), hook);
		} else if (event.getAction().name().contains("RIGHT")) {
			if (grapplerHooks.containsKey(p.getUniqueId())) {
				if (!grapplerHooks.get(p.getUniqueId()).isHooked())
					return;
				
				GrapplingHook hook = grapplerHooks.get(p.getUniqueId());
				Location loc = hook.getBukkitEntity().getLocation();
				Location pLoc = p.getLocation();
				double d = loc.distance(p.getLocation());
				double t = d;
				double v_x = (1.0D + 0.04000000000000001D * t) * ((isNear(loc, pLoc) ? 0 : loc.getX() - pLoc.getX()) / t);
				double v_y = (0.9D + 0.03D * t) * ((isNear(loc, pLoc) ? 0.1 : loc.getY() - pLoc.getY()) / t);
				double v_z = (1.0D + 0.04000000000000001D * t) * ((isNear(loc, pLoc) ? 0 : loc.getZ() - pLoc.getZ()) / t);
				Vector v = p.getVelocity();
				v.setX(v_x);
				v.setY(v_y);
				v.setZ(v_z);
				p.setVelocity(v.multiply((((double) getOption(p, "VECTOR_MULTIPLY").getValue()) / 10)));
				if (p.getLocation().getY() < hook.getBukkitEntity().getLocation().getY()) {
					p.setFallDistance(0);
				}
				p.getWorld().playSound(p.getLocation(), Sound.STEP_GRAVEL, 1.0F, 1.0F);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeashEntityListener(PlayerLeashEntityEvent event) {
		if (!hasAbility(event.getPlayer()))
			return;
		Player p = event.getPlayer();
		
		if (p.getItemInHand() == null)
			return;
		
		ItemStack item = p.getItemInHand();
		ItemStack ITEM = getOption(p, "ITEM").getItemStack();
		
		if (!ItemUtils.isEquals(item, ITEM))
			return;
		
		item.setDurability(ITEM.getDurability());
		event.setCancelled(true);
		if (grapplerHooks.containsKey(p.getUniqueId())) {
			if (grapplerHooks.get(p.getUniqueId()).isHooked()) {
				GrapplingHook hook = grapplerHooks.get(p.getUniqueId());
				Location loc = hook.getBukkitEntity().getLocation();
				Location playerLoc = p.getLocation();
				double d = loc.distance(playerLoc);
				double t = d;
				double v_x = (1.0D + 0.04000000000000001D * t) * ((isNear(loc, playerLoc) ? 0 : loc.getX() - playerLoc.getX()) / t);
				double v_y = (0.9D + 0.03D * t) * ((isNear(loc, playerLoc) ? 0.1 : loc.getY() - playerLoc.getY()) / t);
				double v_z = (1.0D + 0.04000000000000001D * t) * ((isNear(loc, playerLoc) ? 0 : loc.getZ() - playerLoc.getZ()) / t);
				Vector v = p.getVelocity();
				v.setX(v_x);
				v.setY(v_y);
				v.setZ(v_z);
				p.setVelocity(v.multiply((((double) getOption(p, "VECTOR_MULTIPLY").getValue()) / 10)));
				
				if (playerLoc.getY() < hook.getBukkitEntity().getLocation().getY()) {
					p.setFallDistance(0);
				}
				
				p.getWorld().playSound(playerLoc, Sound.STEP_GRAVEL, 1.0F, 1.0F);
			}
		}
	}

	private boolean isNear(Location loc, Location playerLoc) {
		return loc.distance(playerLoc) < 1.5;
	}

	@EventHandler
	public void onPlayerItemHeldListener(PlayerItemHeldEvent e) {
		if (grapplerHooks.containsKey(e.getPlayer().getUniqueId())) {
			grapplerHooks.get(e.getPlayer().getUniqueId()).remove();
			grapplerHooks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerQuitListener(PlayerQuitEvent e) {
		if (grapplerHooks.containsKey(e.getPlayer().getUniqueId())) {
			grapplerHooks.get(e.getPlayer().getUniqueId()).remove();
			grapplerHooks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player p = (Player) event.getEntity();
		
		if (!hasAbility(p))
			return;
		
		if (getOption(p, "COOLDOWN").getValue() <= 0)
			return;
		
		CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return (3 * getOption("VECTOR_MULTIPLY", map).getValue()) + (40 - (4 * (getOption("COOLDOWN", map).getValue())));
	}

}
