package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.event.player.PlayerSelectKitEvent;

public class SelectKitListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayer(PlayerSelectKitEvent event) {
		if (event.getKit() == null)
			return;
		
		if (event.isCancelled())
			return;
		
//		if (event.getKit() instanceof CustomKit) {
//			if (((CustomKit) event.getKit()).getPowerPoints() > 100) {
//				event.getPlayer().sendMessage("§%custom-kit-too-over-power%§");
//				event.setCancelled(true);
//				GameMain.getPlugin().getKitManager().unregisterPlayer(event.getPlayer());
//				return;
//			}
//		}
		
		if(!Gamer.getGamer(event.getPlayer()).hasKit(event.getKit().getName().toLowerCase())) {
			event.getPlayer().sendMessage("§c§l> §fVoc§ n§o possui o kit §c" + NameUtils.formatString(event.getKit().getName()));
			event.setCancelled(true);
			return;
		}
		
		event.getPlayer().sendMessage(" §a* §fVoc§ selecionou o kit §a" + NameUtils.formatString(event.getKit().getName()) + "§f!");
	}
}
