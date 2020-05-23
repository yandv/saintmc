package tk.yallandev.saintmc.game.games.skywars;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.GameMode;
import tk.yallandev.saintmc.game.GameType;
import tk.yallandev.saintmc.game.gameevents.GameEventType;
import tk.yallandev.saintmc.game.stage.GameStage;

public class SkyWarsMode extends GameMode {
	
	public SkyWarsMode(GameMain main) {
		super(main, GameType.SKYWARS);
	}
	
	@Override
	public void onLoad() {
		getGameMain().setGameStage(GameStage.WAITING);
		getGameMain().setMinimumPlayers(4);
		
		//TODO minimumPlayers
	}
	
	@Override
	public void onEnable() {
		getGameMain().getGameEventManager().newEvent(GameEventType.SERVER_START);
		
		new CommandLoader(new BukkitCommandFramework(getGameMain())).loadCommandsFromPackage("br.com.battlebits.game.games.hungergames.command");
		loadListeners();
		
		getGameMain().getServer().getScheduler().scheduleSyncDelayedTask(getGameMain(), new Runnable() {
			public void run() {
				World world = getServer().getWorld("world");
				world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0), 0);
				
				world.setDifficulty(Difficulty.NORMAL);
				
				if (world.hasStorm())
					world.setStorm(false);
				
				world.setWeatherDuration(999999999);
				world.setGameRuleValue("doDaylightCycle", "false");
				org.bukkit.WorldBorder border = world.getWorldBorder();
				border.setCenter(0, 0);
				border.setSize(800);
				
				for (Entity e : world.getEntities()) {
					e.remove();
				}
			}
		});
		
		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING, GameMain.getPlugin().getTimer());
			}
		}.runTaskLaterAsynchronously(getGameMain(), 1);
	}
	
	private void loadListeners() {
//		getGameMain().getServer().getPluginManager().registerEvents(new GameStageChangeListener(getGameMain()),getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new PregameListener(getGameMain()), getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new InventoryListener(getGameMain()),getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new BorderListener(getGameMain()), getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new ScoreboardListener(getGameMain(), this),getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new SelectKitListener(), getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new UpdateListener(getGameMain()), getGameMain());
//		getGameMain().getServer().getPluginManager().registerEvents(new SpectatorListener(getGameMain()),getGameMain());
	}

	@Override
	public void startGame() {
		
	}

}
