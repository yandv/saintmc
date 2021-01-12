package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
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

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;

public class GrapplerAbility extends Ability {

	private Map<UUID, GrapplingHook> grapplerHooks;

	public GrapplerAbility() {
		super("Grappler", Arrays.asList(new ItemBuilder().name("§aGrappler").type(Material.LEASH).build()));
		grapplerHooks = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!hasAbility(event.getPlayer()))
			return;

		if (event.getItem() == null)
			return;

		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if (!isAbilityItem(item))
			return;

		if (action.name().contains("RIGHT")) {
			event.setCancelled(true);
		}

		item.setDurability((short) 0);
		player.updateInventory();

		if (isCooldown(player))
			return;

		if (event.getAction().name().contains("LEFT")) {
			if (grapplerHooks.containsKey(player.getUniqueId())) {
				grapplerHooks.get(player.getUniqueId()).remove();
				grapplerHooks.remove(player.getUniqueId());
			}
			GrapplingHook hook = new GrapplingHook(player.getWorld(), ((CraftPlayer) player).getHandle());
			Vector direction = player.getLocation().getDirection();
			hook.spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()));
			hook.move(direction.getX() * 7.0D, direction.getY() * 5.0D, direction.getZ() * 7.0D);
			grapplerHooks.put(player.getUniqueId(), hook);
		} else if (event.getAction().name().contains("RIGHT")) {
			if (grapplerHooks.containsKey(player.getUniqueId())) {
				if (!grapplerHooks.get(player.getUniqueId()).isHooked())
					return;

				GrapplingHook hook = grapplerHooks.get(player.getUniqueId());
				Location loc = hook.getBukkitEntity().getLocation();
				Location pLoc = player.getLocation();
				double d = loc.distance(player.getLocation());
				double t = d;
				double v_x = (1.0D + 0.06D * t) * ((isNear(loc, pLoc) ? 0 : loc.getX() - pLoc.getX()) / t);
				double v_y = (0.9D + 0.03D * t) * ((isNear(loc, pLoc) ? 0.1 : loc.getY() - pLoc.getY()) / t);
				double v_z = (1.0D + 0.06D * t) * ((isNear(loc, pLoc) ? 0 : loc.getZ() - pLoc.getZ()) / t);
				Vector v = player.getVelocity();
				v.setX(v_x);
				v.setY(v_y);
				v.setZ(v_z);

				player.setVelocity(v.multiply(1));

				player.getWorld().playSound(player.getLocation(), Sound.STEP_GRAVEL, 1.0F, 1.0F);
				player.setFallDistance(0f);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
		if (!hasAbility(event.getPlayer()))
			return;
		Player p = event.getPlayer();

		if (p.getItemInHand() == null)
			return;

		ItemStack item = p.getItemInHand();

		if (!isAbilityItem(item))
			return;

		item.setDurability((short) 0);
		event.setCancelled(true);
		if (grapplerHooks.containsKey(p.getUniqueId())) {
			if (grapplerHooks.get(p.getUniqueId()).isHooked()) {
				GrapplingHook hook = grapplerHooks.get(p.getUniqueId());
				Location loc = hook.getBukkitEntity().getLocation();
				Location playerLoc = p.getLocation();
				double d = loc.distance(playerLoc);
				double t = d;
				double v_x = (1.0D + 0.04000000000000001D * t)
						* ((isNear(loc, playerLoc) ? 0 : loc.getX() - playerLoc.getX()) / t);
				double v_y = (0.9D + 0.03D * t) * ((isNear(loc, playerLoc) ? 0.1 : loc.getY() - playerLoc.getY()) / t);
				double v_z = (1.0D + 0.04000000000000001D * t)
						* ((isNear(loc, playerLoc) ? 0 : loc.getZ() - playerLoc.getZ()) / t);
				Vector v = p.getVelocity();
				v.setX(v_x);
				v.setY(v_y);
				v.setZ(v_z);
				p.setVelocity(v.multiply(1));

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
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		if (grapplerHooks.containsKey(e.getPlayer().getUniqueId())) {
			grapplerHooks.get(e.getPlayer().getUniqueId()).remove();
			grapplerHooks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (grapplerHooks.containsKey(e.getPlayer().getUniqueId())) {
			grapplerHooks.get(e.getPlayer().getUniqueId()).remove();
			grapplerHooks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		Player p = (Player) event.getEntity();

		if (!hasAbility(p))
			return;

		addCooldown(p.getUniqueId(), 6l);
	}

	public static class GrapplingHook extends EntityFishingHook {

		private Snowball sb;
		private EntitySnowball controller;
		public int a;
		public EntityHuman owner;
		public Entity hooked;
		public boolean lastControllerDead;
		public boolean isHooked;

		public GrapplingHook(org.bukkit.World world, EntityHuman entityhuman) {
			super(((CraftWorld) world).getHandle(), entityhuman);
			this.owner = entityhuman;
		}

		@Override
		public void t_() {
			this.lastControllerDead = this.controller.dead;
			for (Entity entity : this.controller.world.getWorld().getEntities()) {
				if (!(entity instanceof Player))
					continue;
				if (entity.getEntityId() == getBukkitEntity().getEntityId())
					continue;
				if (entity.getEntityId() == this.owner.getBukkitEntity().getEntityId())
					continue;
				if (entity.getEntityId() == this.controller.getBukkitEntity().getEntityId())
					continue;
				if (entity.getLocation().distance(this.controller.getBukkitEntity().getLocation()) > 2.0D)
					continue;
				if (GameGeneral.getInstance().getGamerController().getGamer((Player) entity).isNotPlaying()
						|| AdminMode.getInstance().isAdmin((Player) entity))
					continue;
				this.controller.die();
				this.hooked = entity;
				this.isHooked = true;
				this.locX = entity.getLocation().getX();
				this.locY = entity.getLocation().getY();
				this.locZ = entity.getLocation().getZ();
				this.motX = 0.0D;
				this.motY = 0.04D;
				this.motZ = 0.0D;
			}
			try {
				this.locX = this.hooked.getLocation().getX();
				this.locY = this.hooked.getLocation().getY();
				this.locZ = this.hooked.getLocation().getZ();
				this.motX = 0.0D;
				this.motY = 0.04D;
				this.motZ = 0.0D;
				this.isHooked = true;
			} catch (Exception e) {
				if (this.controller.dead) {
					this.isHooked = true;
				}
				this.locX = this.controller.locX;
				this.locY = this.controller.locY;
				this.locZ = this.controller.locZ;
			}
		}

		public void die() {
		}

		public void remove() {
			super.die();
		}

		public void spawn(Location location) {
			this.sb = (Snowball) this.owner.getBukkitEntity().launchProjectile(Snowball.class);
			this.controller = ((CraftSnowball) this.sb).getHandle();
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { this.controller.getId() });

			Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));

			((CraftWorld) location.getWorld()).getHandle().addEntity(this);
		}

		public boolean isHooked() {
			return this.isHooked;
		}

		public void setHookedEntity(Entity damaged) {
			this.hooked = damaged;
		}
	}
}
