package tk.yallandev.saintmc.game;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.event.game.GameStageChangeEvent;
import tk.yallandev.saintmc.game.event.game.GameStartEvent;
import tk.yallandev.saintmc.game.event.game.GameTimerEvent;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.games.skywars.SkyWarsMode;
import tk.yallandev.saintmc.game.listener.DamagerFixer;
import tk.yallandev.saintmc.game.listener.EventListener;
import tk.yallandev.saintmc.game.listener.JoinListener;
import tk.yallandev.saintmc.game.listener.QuitListener;
import tk.yallandev.saintmc.game.listener.SpectatorListener;
import tk.yallandev.saintmc.game.manager.AbilityManager;
import tk.yallandev.saintmc.game.manager.GameEventManager;
import tk.yallandev.saintmc.game.manager.GamerManager;
import tk.yallandev.saintmc.game.manager.KitManager;
import tk.yallandev.saintmc.game.manager.SchedulerManager;
import tk.yallandev.saintmc.game.manager.SimpleKitManager;
import tk.yallandev.saintmc.game.scheduler.SchedulerListener;
import tk.yallandev.saintmc.game.serverinfo.ServerInfoInjector;
import tk.yallandev.saintmc.game.stage.CounterType;
import tk.yallandev.saintmc.game.stage.GameStage;

public class GameMain extends JavaPlugin {
	
	private int timer;
	private CounterType timerType = CounterType.STOP;
	private int minimumPlayers = 5;
	private int totalPlayers;
	private GameType gameType = GameType.NONE;
	private GameStage gameStage = GameStage.NONE;

	private GameMode gameMode;

	// MANAGERS
	private AbilityManager abilityManager;
	private GameEventManager gameEventManager = new GameEventManager();
	private GamerManager gamerManager = new GamerManager();
	private KitManager kitManager;
	private SimpleKitManager simpleKitManager;
	private SchedulerManager schedulerManager = new SchedulerManager(this);

	public static long enabled;

	private static GameMain plugin;

	{
		plugin = this;
	}

	@Override
	public void onLoad() {
		loadConfiguration();
		loadGamemode();
		getLogger().info("Gamemode carregado > " + gameType.toString());
		gameMode.onLoad();
		ServerInfoInjector.inject(BukkitMain.getInstance());
		
		enabled = System.currentTimeMillis() + (1000l * 1000l);
	}

	@Override
	public void onEnable() {
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		new CommandLoader(new BukkitCommandFramework(this)).loadCommandsFromPackage("tk.yallandev.saintmc.game.command");
		loadListeners();
		gameMode.onEnable();
		abilityManager = new AbilityManager();
		kitManager = new KitManager();
		simpleKitManager = new SimpleKitManager();
		totalPlayers = Bukkit.getMaxPlayers();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				enabled = System.currentTimeMillis() + 1000l;
			}
		}.runTaskLater(this, 5l);
	}

	@Override
	public void onDisable() {
		gameMode.onDisable();
	}

	public void startGame() {
		gameMode.startGame();
		abilityManager.registerAbilityListeners();
		getServer().getPluginManager().callEvent(new GameStartEvent());
		totalPlayers = playersLeft();
	}

	private void loadGamemode() {
		try {
			gameType = GameType.valueOf(getConfig().getString("gameType", GameType.HUNGERGAMES.toString()).toUpperCase());
		} catch (Exception e) {
			gameType = GameType.NONE;
		}
		
		
		switch (gameType) {
		case HUNGERGAMES: {
			gameMode = new HungerGamesMode(this);
			break;
		}
		case SKYWARS: {
			gameMode = new SkyWarsMode(this);
			break;
		}
		default:
			break;
		}
	}

	private void loadConfiguration() {
		saveDefaultConfig();
	}

	private void loadListeners() {
		getServer().getPluginManager().registerEvents(new DamagerFixer(), this);
		getServer().getPluginManager().registerEvents(new SchedulerListener(this), this);
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		getServer().getPluginManager().registerEvents(new JoinListener(this), this);
		getServer().getPluginManager().registerEvents(new QuitListener(this), this);
		getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
	}

	public GameType getGameType() {
		return gameType;
	}

	public GameStage getGameStage() {
		return gameStage;
	}

	public int getTotalPlayers() {
		return totalPlayers;
	}

	public void setGameStage(GameStage gameStage) {
		getServer().getPluginManager().callEvent(new GameStageChangeEvent(this.gameStage, gameStage));
		this.gameStage = gameStage;
		setTimerType(getGameStage().getDefaultType());
		setTimer(getGameStage().getDefaultTimer());
	}
	
	public void setGameStage(GameStage gameStage, int time) {
		getServer().getPluginManager().callEvent(new GameStageChangeEvent(this.gameStage, gameStage));
		this.gameStage = gameStage;
		setTimerType(getGameStage().getDefaultType());
		setTimer(getGameStage().getDefaultTimer());
	}

	public void setMinimumPlayers(int minimumPlayers) {
		this.minimumPlayers = minimumPlayers;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public AbilityManager getAbilityManager() {
		return abilityManager;
	}

	public GameEventManager getGameEventManager() {
		return gameEventManager;
	}

	public GamerManager getGamerManager() {
		return gamerManager;
	}

	public KitManager getKitManager() {
		return kitManager;
	}
	
	public SimpleKitManager getSimpleKitManager() {
		return simpleKitManager;
	}

	public SchedulerManager getSchedulerManager() {
		return schedulerManager;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		getServer().getPluginManager().callEvent(new GameTimerEvent());
		this.timer = timer;
	}

	public void setTimerType(CounterType timerType) {
		this.timerType = timerType;
	}

	public void count() {
		switch (timerType) {
		case COUNTDOWN:
			setTimer(timer - 1);
			break;
		case COUNT_UP:
			setTimer(timer + 1);
			break;
		default:
			break;
		}
	}

	public void checkTimer() {
		int i = minimumPlayers;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			Gamer gamer = Gamer.getGamer(p);
			
			if (gamer == null)
				continue;
			
//			if (gamer.isGamemaker())
//				continue;
			
			if (gamer.isSpectator())
				continue;
			
			i--;
		}
		
		if (getGameStage() == GameStage.WAITING) {
			if (i <= 0) {
				setGameStage(GameStage.PREGAME);
			}
		} else if (getGameStage() == GameStage.PREGAME || getGameStage() == GameStage.STARTING) {
			if (i > 0) {
				setGameStage(GameStage.WAITING);
				
				if (getTimer() < 60)
					setTimer(60);
				else if (getTimer() < 120)
					setTimer(120);
				else if (getTimer() < 180)
					setTimer(180);
			}
		}
	}

	public int playersLeft() {
		int i = 0;
		
		for (Player p : getServer().getOnlinePlayers()) {
			if (AdminMode.getInstance().isAdmin(p))
				continue;
			
			Gamer gamer = getGamerManager().getGamer(p.getUniqueId());
			
			if (gamer == null)
				continue;
			
			if (gamer.isGamemaker())
				continue;
			
			if (gamer.isSpectator())
				continue;
			
			if (!p.isOnline())
				continue;
			
			i++;
		}
		
		return i;
	}
	
	public void sendPlayerToLobby(Player p)  {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
	    DataOutputStream out = new DataOutputStream(b);
	    
	    try {
	      out.writeUTF("Lobby");
	    } catch (Exception e)  {
	      e.printStackTrace(System.out);
	    }
	    
	    p.sendPluginMessage(getPlugin(), "BungeeCord", b.toByteArray());
	}

	public static GameMain getPlugin() {
		return plugin;
	}
	
}
