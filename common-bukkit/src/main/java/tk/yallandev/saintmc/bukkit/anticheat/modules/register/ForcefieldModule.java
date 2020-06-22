//package tk.yallandev.saintmc.bukkit.anticheat.modules.register;
//
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//
//import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
//
//public class ForcefieldModule extends Module {
//	
//	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
//		
//		if (!(event.getDamager() instanceof Player))
//			return;
//		
////		Player player = (Player) event.getDamager();
////		Entity damaged = event.getEntity();
//		
////		if (FightUtil.isHacking(player.getPlayer(), player.getPlayer().getLocation(), damaged)) {
////			alert(player);
////		}
//	}
//
//}
