//package tk.yallandev.saintmc.bukkit.anticheat.modules.register;
//
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//
//import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
//
//public class VelocityModule extends Module {
//
////	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
////	public void onEntityDamage(EntityDamageEvent event) {
////		if (event.getCause() != DamageCause.ENTITY_ATTACK)
////			return;
////
////		if (!(event.getEntity() instanceof Player))
////			return;
////
////		Player player = (Player) event.getEntity();
////
////		if (ProtocolGetter.getPing(player) >= 100 || player.hasPotionEffect(PotionEffectType.POISON)
////				|| player.hasPotionEffect(PotionEffectType.WITHER) || player.getFireTicks() > 10)
////			return;
////
////		if (hasSurrondingBlocks(player.getLocation().getBlock()))
////			return;
////
////		Location location = player.getLocation().clone();
////
////		new BukkitRunnable() {
////
////			@Override
////			public void run() {
////				if (!player.isOnline())
////					return;
////
////				if (location.distanceSquared(player.getLocation()) <= 0.001) {
////					alert(player);
////				}
////			}
////		}.runTaskLater(BukkitMain.getInstance(), 7l);
////	}
//
//	public boolean hasSurrondingBlocks(Block block) {
//		Block relative = block.getRelative(BlockFace.DOWN);
//
//		for (BlockFace blockFace : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
//				BlockFace.EAST }) {
//
//			if (relative.getRelative(blockFace).getType() != Material.AIR)
//				return true;
//		}
//
//		return false;
//	}
//
//}
