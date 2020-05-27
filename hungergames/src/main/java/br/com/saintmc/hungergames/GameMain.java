package br.com.saintmc.hungergames;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.saintmc.hungergames.game.Game;
import br.com.saintmc.hungergames.listener.register.BorderListener;
import br.com.saintmc.hungergames.listener.register.GamerListener;
import br.com.saintmc.hungergames.listener.register.KitListener;
import br.com.saintmc.hungergames.listener.register.ScoreboardListener;
import br.com.saintmc.hungergames.scheduler.SchedulerListener;
import br.com.saintmc.hungergames.utils.MapUtils;
import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class GameMain extends JavaPlugin {
	
	public static final boolean SPECTATOR = true;
	
	public static final Game GAME = new Game(0, 30);
	
	public static boolean canJoin = false;
	
	public static final Group SPECTATOR_GROUP = Group.LIGHT;
	public static final Group RESPAWN_GROUP = Group.BLIZZARD;
	public static final Group KIT_SPAWN_GROUP = Group.SAINT;
	
	public static final boolean DOUBLEKIT = true;
	
	public static final HashMap<Group, List<String>> KITROTATE;

	static {
		KITROTATE = new HashMap<>();
		KITROTATE.put(Group.MEMBRO, Arrays.asList("surprise", "snail", "thor", "timelord", "reaper", "worm"));
		KITROTATE.put(Group.LIGHT, Arrays.asList("kangaroo", "boxer", "ninja", "stomper", "endermage", "cannibal"));
		KITROTATE.put(Group.BLIZZARD, Arrays.asList("viper", "grappler", "viking", "hotpotato", "", ""));
	}

	@Getter
	private static GameMain instance;

	private GameGeneral general;
	private String roomId;

	@Override
	public void onLoad() {

		instance = this;
		
		MapUtils.deleteWorld("world");

		general = new GameGeneral();
		general.onLoad();
		
		super.onLoad();
	}

	@Override
	public void onEnable() {

		new CommandLoader(new BukkitCommandFramework(getInstance())).loadCommandsFromPackage("br.com.saintmc.hungergames.command");
		BukkitMain.getInstance().setRemovePlayerDat(false);
		
		loadListener();
		general.onEnable();
		
		if (roomId == null) {
			String[] split = CommonGeneral.getInstance().getServerId().split("//.");
			
			if (split.length > 1) {
				roomId = split[0];
			} else {
				roomId = CommonGeneral.getInstance().getServerId();
			}
		}
		
		getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {
			public void run() {
				World world = getServer().getWorld("world");
				world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0), 0);

				for (int x = -30; x <= 30; x++)
					for (int z = -30; z <= 30; z++)
						world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();

				world.setDifficulty(Difficulty.NORMAL);

				if (world.hasStorm())
					world.setStorm(false);
				
				world.setTime(0l);
				world.setWeatherDuration(999999999);
				world.setGameRuleValue("doDaylightCycle", "false");
				org.bukkit.WorldBorder border = world.getWorldBorder();
				border.setCenter(0, 0);
				border.setSize(1000);

				for (Entity e : world.getEntities()) {
					e.remove();
				}
				
				canJoin = true;
			}
		});

		super.onEnable();
	}

	@Override
	public void onDisable() {

		general.onDisable();

		super.onDisable();
	}

	public void loadListener() {
		Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
		Bukkit.getPluginManager().registerEvents(new KitListener(), this);
		Bukkit.getPluginManager().registerEvents(new SchedulerListener(getGeneral()), this);
	}
	
	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, getInstance());
	}

	public void sendPlayerToLobby(Player p)  {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
	    DataOutputStream out = new DataOutputStream(b);
	    
	    try {
	      out.writeUTF("Lobby");
	    } catch (Exception e)  {
	      e.printStackTrace(System.out);
	    }
	    
	    p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}

}
