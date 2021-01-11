package tk.yallandev.saintmc.lobby;

import java.io.File;

import org.bukkit.Bukkit;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory;
import tk.yallandev.saintmc.update.UpdatePlugin;

public class LobbyMain extends LobbyPlatform {

	@Getter
	private static LobbyMain instance;

	@Override
	public void onLoad() {
		super.onLoad();
		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(new File(LobbyMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"Lobby", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		instance = this;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		ServerInventory.LOBBY_HG = false;
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
