package net.saintmc.anticheat.check.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import net.saintmc.anticheat.check.CheckClass;
import tk.yallandev.saintmc.bukkit.BukkitMain;

public class VapeCheck implements CheckClass, PluginMessageListener {

	public VapeCheck() {
		BukkitMain.getInstance().getServer().getMessenger().registerIncomingPluginChannel(BukkitMain.getInstance(),
				"LOLIMAHCKER", this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
	}

	@Override
	public void onPluginMessageReceived(String s, Player player, byte[] data) {
		BukkitMain.getInstance().getAnticheatController().getAlertController().autoban(player, 1);
	}

}
