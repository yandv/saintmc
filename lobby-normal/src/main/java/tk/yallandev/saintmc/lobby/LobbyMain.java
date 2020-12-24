package tk.yallandev.saintmc.lobby;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.update.UpdatePlugin;

public class LobbyMain extends JavaPlugin {

	@Getter
	private static LobbyMain instance;

	private LobbyPlatform platform;

	@Override
	public void onLoad() {

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

		super.onLoad();
	}

	@Override
	public void onEnable() {
		platform = new LobbyPlatform(this);

		platform.onEnable();
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		platform.onDisable();
		super.onDisable();
	}

}
