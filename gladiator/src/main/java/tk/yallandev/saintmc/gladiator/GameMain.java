package tk.yallandev.saintmc.gladiator;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class GameMain extends JavaPlugin {
	
	@Getter
	private static GameMain instance;
	
	private GameGeneral gameGeneral;
	
	@Override
	public void onLoad() {
		
		gameGeneral = new GameGeneral();
		
		gameGeneral.onLoad();
		
		super.onLoad();
	}
	
	@Override
	public void onEnable() {
		
		gameGeneral.onEnable();
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		
		gameGeneral.onDisable();
		
		super.onDisable();
	}
	
}
