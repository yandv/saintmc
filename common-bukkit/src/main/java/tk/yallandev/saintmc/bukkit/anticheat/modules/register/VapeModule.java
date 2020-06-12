package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
import tk.yallandev.saintmc.common.server.ServerType;

public class VapeModule extends Module implements PluginMessageListener {

	public VapeModule() {
		setMaxAlerts(1);

		BukkitMain.getInstance().getServer().getMessenger().registerIncomingPluginChannel(BukkitMain.getInstance(),
				"LOLIMAHCKER", this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (CommonGeneral.getInstance().getServerType() == ServerType.LOBBY)
			event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
	}

	@Override
	public void onPluginMessageReceived(String s, Player player, byte[] data) {
		autoban(player, System.currentTimeMillis() + 5000l);
	}
}
