package tk.yallandev.saintmc.lobby;

import lombok.Getter;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;

public class LobbyMain extends LobbyPlatform {

	@Getter
	private static LobbyMain instance;

	@Override
	public void onLoad() {
		instance = this;

		super.onLoad();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
