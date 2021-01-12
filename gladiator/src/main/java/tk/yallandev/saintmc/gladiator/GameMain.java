package tk.yallandev.saintmc.gladiator;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.listener.register.CombatListener;
import tk.yallandev.saintmc.gladiator.command.DefaultCommand;
import tk.yallandev.saintmc.gladiator.command.SpectatorCommand;
import tk.yallandev.saintmc.gladiator.controller.GamerManager;
import tk.yallandev.saintmc.gladiator.listener.ArenaListener;
import tk.yallandev.saintmc.gladiator.listener.GladiatorListener;
import tk.yallandev.saintmc.gladiator.listener.PlayerListener;
import tk.yallandev.saintmc.gladiator.listener.RankingListener;
import tk.yallandev.saintmc.gladiator.listener.ScoreboardListener;
import tk.yallandev.saintmc.gladiator.listener.SpectatorListener;
import tk.yallandev.saintmc.gladiator.listener.StatusListener;
import tk.yallandev.saintmc.gladiator.listener.WorldListener;
import tk.yallandev.saintmc.update.UpdatePlugin;

/**
 * 
 * Testando nesse plugin um modelo aberto Onde cada classe tem sua função bem
 * definida e as outras não podem interferir
 * 
 * @author Allan
 *
 */

@Getter
public class GameMain extends JavaPlugin {

	@Getter
	private static GameMain instance;

	private GameGeneral gameGeneral;

	private GamerManager gamerManager;

	@Override
	public void onLoad() {
		instance = this;
		gameGeneral = new GameGeneral();

		gameGeneral.onLoad();
		super.onLoad();
	}

	@Override
	public void onEnable() {
		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(new File(GameMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"Gladiator", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		loadListener();
		gameGeneral.onEnable();

		gamerManager = new GamerManager();

		BukkitCommandFramework.INSTANCE.registerCommands(new DefaultCommand());
		BukkitCommandFramework.INSTANCE.registerCommands(new SpectatorCommand());

		super.onEnable();
	}

	@Override
	public void onDisable() {

		gameGeneral.onDisable();

		super.onDisable();
	}

	public void loadListener() {
		Bukkit.getPluginManager().registerEvents(new ArenaListener(), this);
		Bukkit.getPluginManager().registerEvents(new GladiatorListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new CombatListener(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
		Bukkit.getPluginManager().registerEvents(new StatusListener(), this);
		Bukkit.getPluginManager().registerEvents(new RankingListener(), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
	}

}
