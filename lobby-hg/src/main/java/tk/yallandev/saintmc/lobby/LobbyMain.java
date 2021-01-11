package tk.yallandev.saintmc.lobby;

import java.io.File;

import org.bukkit.Bukkit;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.update.UpdatePlugin;

public class LobbyMain extends LobbyPlatform {

	@Getter
	private static LobbyMain instance;

	@Override
	public void onLoad() {

		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(new File(LobbyMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"LobbyHG", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		instance = this;

		super.onLoad();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
