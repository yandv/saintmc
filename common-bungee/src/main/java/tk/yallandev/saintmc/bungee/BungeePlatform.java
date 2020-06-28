package tk.yallandev.saintmc.bungee;

import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.bungee.command.BungeeCommandSender;
import tk.yallandev.saintmc.common.command.CommandSender;

public class BungeePlatform implements CommonPlatform {
	
	@Getter
	private CommandSender consoleSender;
	
	public BungeePlatform() {
		this.consoleSender = new BungeeCommandSender(ProxyServer.getInstance().getConsole());
	}
	
	@Override
	public UUID getUuid(String playerName) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
		return player != null ? player.getUniqueId() : null;
	}

	@Override
	public <T> T getPlayerByName(String playerName, Class<T> clazz) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
		return player != null ? clazz.cast(player) : null;
	}
	
	@Override
	public <T> T getExactPlayerByName(String playerName, Class<T> clazz) {
		ProxiedPlayer p = null;
		
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (player.getName().equals(playerName)) {
				p = player;
				break;
			}
		}
		
		return p != null ? clazz.cast(p) : null;
	}

	@Override
	public <T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);
		return player != null ? clazz.cast(player) : null;
	}
	
	@Override
	public void runAsync(Runnable runnable) {
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getInstance(), runnable);
	}

}
