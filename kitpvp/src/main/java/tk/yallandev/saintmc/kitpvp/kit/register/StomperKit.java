package tk.yallandev.saintmc.kitpvp.kit.register;

public class StomperKit
//extends Kit
{
//
//	public StomperKit() {
//		super("Stomper", "Pise em cima de seus inimigos", Material.IRON_BOOTS, 21000, new ArrayList<>());
//	}
//
//	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//	public void onDamage(EntityDamageEvent event) {
//		if (!(event.getEntity() instanceof Player))
//			return;
//
//		if (event.getCause() != DamageCause.FALL)
//			return;
//
//		Player stomper = (Player) event.getEntity();
//
//		if (!hasAbility(stomper))
//			return;
//
//		DamageCause cause = event.getCause();
//
//		if (cause != DamageCause.FALL)
//			return;
//
//		double dmg = event.getDamage();
//
//		for (Player stomped : Bukkit.getOnlinePlayers()) {
//			if (stomped.getUniqueId() == stomper.getUniqueId() || stomped.isDead())
//				continue;
//
//			if (stomped.getLocation().distance(stomper.getLocation()) > 5)
//				continue;
//
//			if (stomped.isSneaking() && dmg > 8)
//				dmg = 8;
//
//			PlayerStompedEvent playerStomperEvent = new PlayerStompedEvent(stomped, stomper);
//			Bukkit.getPluginManager().callEvent(playerStomperEvent);
//
//			if (!playerStomperEvent.isCancelled()) {
//				stomped.setMetadata("ignoreDamage",
//						new FixedMetadataValue(GameMain.getInstance(), System.currentTimeMillis() + 1000));
//
//				stomped.damage(0.1D, stomper);
//				stomped.damage(dmg);
//			}
//
//		}
//
//		for (int x = (int) -3; x <= 3; x++) {
//			for (int z = (int) -3; z <= 3; z++) {
//				Location effect = stomper.getLocation().clone().add(x, 0, z);
//
//				if (effect.distance(stomper.getLocation()) > 3)
//					continue;
//
//				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true,
//						(float) effect.getX(), (float) effect.getY(), (float) effect.getZ(), 0.1F, 0.1F, 0.1F, 1, 30);
//
//				Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer.canSee(stomper))
//						.forEach(viewer -> ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet));
//			}
//		}
//
//		stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1, 1);
//
//		if (event.getDamage() > 4.0D)
//			event.setDamage(4.0d);
//	}
//
}
