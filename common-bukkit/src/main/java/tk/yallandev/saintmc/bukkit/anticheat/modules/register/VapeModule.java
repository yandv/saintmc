package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;

public class VapeModule extends Module implements PluginMessageListener {
	
	public VapeModule() {
		setMaxAlerts(1);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
	}

	@Override
	public void onPluginMessageReceived(String s, Player player, byte[] data) {
		alert(player);
	}
}
