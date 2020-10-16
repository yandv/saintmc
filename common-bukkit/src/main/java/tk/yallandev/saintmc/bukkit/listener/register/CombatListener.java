package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.MetadataValue;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;

public class CombatListener extends Listener {

//	private Queue<EntityDamageByEntityEvent> hitQueue = new ConcurrentLinkedQueue<>();
//
//	public CombatListener() {
//
//		new BukkitRunnable() {
//			public void run() {
//				while (hitQueue.size() > 0) {
//					EntityDamageByEntityEvent event = hitQueue.remove();
//					Bukkit.getPluginManager().callEvent(event);
//
//					if (!event.isCancelled())
//						((Damageable) event.getEntity()).damage(event.getFinalDamage(), event.getDamager());
//				}
//			}
//		}.runTaskTimer(BukkitMain.getInstance(), 1L, 1L);
//
//		ProtocolLibrary.getProtocolManager().getAsynchronousManager()
//				.registerAsyncHandler(new PacketAdapter(BukkitMain.getInstance(), ListenerPriority.LOWEST,
//						PacketType.Play.Client.USE_ENTITY) {
//					
//					@SuppressWarnings("deprecation")
//					public void onPacketReceiving(PacketEvent event) {
//						PacketContainer packet = event.getPacket();
//						Player attacker = event.getPlayer();
//						Entity entity = (Entity) packet.getEntityModifier(event).read(0);
//						Damageable target = (entity instanceof Damageable) ? (Damageable) entity : null;
//						World world = attacker.getWorld();
//						if (packet.getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK
//								&& target != null && !target.isDead() && world == target.getWorld() && world.getPVP()
//								&& attacker.getLocation().distanceSquared(target.getLocation()) < 4.5
//								&& (!(target instanceof Player)
//										|| ((Player) target).getGameMode() != GameMode.CREATIVE)) {
//
//							double damage = 1.0D;
//							ItemStack itemStack = attacker.getItemInHand();
//
//							if (itemStack != null) {
//								damage = getDamage(itemStack.getType());
//
//								if (!itemStack.getEnchantments().isEmpty()) {
//									if (itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)
//											&& isArthropod(entity.getType()))
//										damage += 1 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
//
//									if (itemStack.containsEnchantment(Enchantment.DAMAGE_UNDEAD)
//											&& isUndead(entity.getType()))
//										damage += 1 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
//
//									if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL))
//										damage += 0.5 * itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
//								}
//							}
//
//							Bukkit.broadcastMessage("Dano: " + damage);
//
//							event.setCancelled(true);
//							PacketContainer damageAnimation = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);
//							damageAnimation.getIntegers().write(0, Integer.valueOf(target.getEntityId()));
//							damageAnimation.getBytes().write(0, Byte.valueOf((byte) 2));
//
//							hitQueue.add(new EntityDamageByEntityEvent((Entity) attacker, (Entity) target,
//									EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
//						}
//					}
//
//					public void onPacketSending(PacketEvent event) {
//					}
//
//				}).start();
//	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getCause() == DamageCause.FALL)
			if (player.hasMetadata("nofall")) {
				MetadataValue metadata = player.getMetadata("nofall").stream().findFirst().orElse(null);

				if (metadata.asLong() > System.currentTimeMillis())
					event.setCancelled(true);

				metadata.invalidate();
			}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = null;

		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();

			if (projectile.getShooter() instanceof Player) {
				damager = (Player) projectile.getShooter();
			}
		}

		if (!(damager instanceof Player))
			return;

		PlayerDamagePlayerEvent playerDamagePlayerEvent = new PlayerDamagePlayerEvent(player, damager,
				event.isCancelled(), event.getDamage(), event.getFinalDamage());

		Bukkit.getPluginManager().callEvent(playerDamagePlayerEvent);

		event.setCancelled(playerDamagePlayerEvent.isCancelled());
		event.setDamage(playerDamagePlayerEvent.getDamage());
	}

}
