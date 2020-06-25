package tk.yallandev.saintmc.gladiator;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.gladiator.listener.GladiatorListener;
import tk.yallandev.saintmc.gladiator.listener.PlayerListener;
import tk.yallandev.saintmc.gladiator.listener.WorldListener;

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
		
		loadListener();
		gameGeneral.onEnable();
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		
		gameGeneral.onDisable();
		
		super.onDisable();
	}
	
	public void loadListener() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new GladiatorListener(), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
	}
	
}
