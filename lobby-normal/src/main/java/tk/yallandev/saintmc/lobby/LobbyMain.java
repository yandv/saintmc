package tk.yallandev.saintmc.lobby;

import lombok.Getter;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory;

public class LobbyMain extends LobbyPlatform {

	@Getter
	private static LobbyMain instance;

	@Override
	public void onLoad() {
		super.onLoad();
		instance = this;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		ServerInventory.LOBBY_HG = true;
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
