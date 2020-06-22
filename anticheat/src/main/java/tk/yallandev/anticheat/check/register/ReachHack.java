package tk.yallandev.anticheat.check.register;

import java.util.AbstractMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import tk.yallandev.anticheat.check.Hack;
import tk.yallandev.anticheat.utils.UtilMath;
import tk.yallandev.anticheat.utils.UtilPlayer;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolGetter;

public class ReachHack extends Hack {

	public Map<Player, Integer> count;
	public Map<Player, Map.Entry<Double, Double>> offsets;

	public ReachHack() {
		offsets = new WeakHashMap<>();
		count = new WeakHashMap<>();
	}

//	public ReachHack() {
//		ProtocolLibrary.getProtocolManager().addPacketListener(
//				new PacketAdapter(AnticheatController.getInstance().getPlugin(), PacketType.Play.Client.USE_ENTITY) {
//
//					@Override
//					public void onPacketReceiving(PacketEvent event) {
//						if (event.getPlayer() == null) {
//							event.setCancelled(true);
//							return;
//						}
//
//						if (event.getPlayer() == null) {
//							event.setCancelled(true);
//							return;
//						}
//
//						PacketContainer packetContainer = event.getPacket();
//
//						Player player = event.getPlayer();
//
//						int n = (Integer) packetContainer.getIntegers().read(0);
//						Entity entity = null;
//
//						for (Entity entity2 : player.getWorld().getEntities())
//							if (entity2.getEntityId() == n)
//								entity = entity2;
//
//						if (!(entity instanceof Player))
//							return;
//
//						Player player2 = (Player) entity;
//
//						PlayerStats playerStats = AnticheatController.getInstance().getPlayerController()
//								.getPlayerStats(player);
//						PlayerStats playerStats2 = AnticheatController.getInstance().getPlayerController()
//								.getPlayerStats(player2);
//
//						double d = player.getLocation().distance(player2.getLocation());
//						long l = 701;
//						double d2 = maxRange;
//
//						if (lastAttack.containsKey(player.getUniqueId())) {
//							l = System.currentTimeMillis() - lastAttack.get(player.getUniqueId());
//						}
//
//						lastAttack.put(player.getUniqueId(), System.currentTimeMillis());
//
//						if (player.getGameMode() == GameMode.CREATIVE)
//							return;
//
//						if (player.isFlying() || player2.isFlying())
//							return;
//
//						if (d > 6.0)
//							return;
//
//						if (!player2.hasLineOfSight((Entity) player))
//							return;
//
//						if (player.isSprinting())
//							d2 += 0.2;
//
//						if (player2.isSprinting())
//							d2 += 0.3;
//
//						double d3 = AngleUtil.getOffsets(player2, (LivingEntity) player)[0];
//						int n2 = firstHitThreshold;
//						int n3 = ProtocolGetter.getPing(player);
//						if (n3 > 400) {
//							return;
//						}
//						if (n3 > 300) {
//							d2 *= 1.6;
//						} else if (n3 > 250) {
//							d2 *= 2.0;
//						} else if (n3 > 200) {
//							d2 *= 1.4;
//						}
//						
//						System.out.println(new DecimalFormat("#.##").format(d));
//						
//						if (d >= d2 * 1.4) {
//							alert(player);
//							System.out.println(new DecimalFormat("#.##").format(d));
//						} else {
//							alert(player);
//						}
//					}
//				});
//	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ())
			return;
		double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()),
				UtilMath.getHorizontalVector(event.getTo().toVector()));
		double horizontal = Math.sqrt(Math.pow(event.getTo().getX() - event.getFrom().getX(), 2.0)
				+ Math.pow(event.getTo().getZ() - event.getFrom().getZ(), 2.0));
		offsets.put(event.getPlayer(), new AbstractMap.SimpleEntry<>(OffsetXZ, horizontal));
	}

	@EventHandler
	public void onATTACK(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player))
			return;
		Player damager = (Player) e.getDamager();
		Player player = (Player) e.getEntity();
		double Reach = UtilMath.trim(2, UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32);
		double Reach2 = UtilMath.trim(2, UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32);

		double Difference;

		if (damager.getAllowFlight() || player.getAllowFlight())
			return;

		if (!count.containsKey(damager)) {
			count.put(damager, 0);
		}

		int Count = count.get(damager);
		long Time = System.currentTimeMillis();
		double MaxReach = 3.1;
		double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - player.getEyeLocation().getYaw());
		double speedToVelocityDif = 0;
		double offsets = 0.0D;

		double lastHorizontal = 0.0D;

		if (this.offsets.containsKey(damager)) {
			offsets = (this.offsets.get(damager)).getKey();
			lastHorizontal = (this.offsets.get(damager)).getValue();
		}

		if (ProtocolGetter.getPing(damager) > 92 || ProtocolGetter.getPing(player) > 92)
			return;

		speedToVelocityDif = Math.abs(offsets - player.getVelocity().length());
		MaxReach += (YawDifference * 0.001);
		MaxReach += lastHorizontal * 1.5;
		MaxReach += speedToVelocityDif * 0.08;

		if (damager.getLocation().getY() > player.getLocation().getY()) {
			Difference = damager.getLocation().getY() - player.getLocation().getY();
			MaxReach += Difference / 2.5;
		} else if (player.getLocation().getY() > damager.getLocation().getY()) {
			Difference = player.getLocation().getY() - damager.getLocation().getY();
			MaxReach += Difference / 2.5;
		}

		MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;

		int PingD = ProtocolGetter.getPing(damager);
		int PingP = ProtocolGetter.getPing(player);

		MaxReach += ((PingD + PingP) / 2) * 0.0024;

		if (PingD > 400) {
			MaxReach += 1.0D;
		}

//		if (UtilTime.elapsed(Time, 10000)) {
//			count.remove(damager);
//			Time = System.currentTimeMillis();
//		}

		if (Reach > MaxReach) {
			count.put(damager, Count + 1);
		} else {
			if (Count >= -2) {
				count.put(damager, Count - 1);
			}
		}

		if (Reach2 > 6) {
			e.setCancelled(true);
		}

		if (Count >= 2 && Reach > MaxReach && Reach < 20.0) {
			count.remove(damager);
			if (ProtocolGetter.getPing(player) < 115) {
				alert(player);
				System.out.println(Reach + " > " + MaxReach + " MS: " + PingD + " Velocity Difference: " + speedToVelocityDif);

			}
//            dumplog(damager, "Logged for Reach" + Reach2 + " > " + MaxReach);
			return;
		}
	}
}
