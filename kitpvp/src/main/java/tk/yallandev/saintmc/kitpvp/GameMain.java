package tk.yallandev.saintmc.kitpvp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.ClassGetter;
import tk.yallandev.saintmc.kitpvp.hologram.RankingHologram;
import tk.yallandev.saintmc.kitpvp.manager.GamerManager;
import tk.yallandev.saintmc.kitpvp.manager.KitManager;
import tk.yallandev.saintmc.kitpvp.manager.WarpManager;

@Getter
public class GameMain extends JavaPlugin {

	private static GameMain instance;

	private GamerManager gamerManager;
	private KitManager kitManager;
	private WarpManager warpManager;

	private RankingHologram rankingHologram;

	public static final Map<Group, List<String>> KITROTATE;

	static {
		KITROTATE = new HashMap<>();
		KITROTATE.put(Group.MEMBRO, Arrays.asList("pvp", "monk", "viper", "snail", "ninja", "antitower"));
		KITROTATE.put(Group.PRO, Arrays.asList("hulk", "anchor", "thor", "gladiator", "timelord"));
		KITROTATE.put(Group.PRO, Arrays.asList("switcher", "boxer", "stomper", "endermage", "ultimato"));
		KITROTATE.put(Group.ELITE,
				Arrays.asList("viper", "grappler", "viking", "hotpotato", "timelord", "launcher"));
	}

	@Override
	public void onLoad() {
		instance = this;
		super.onLoad();
	}

	@Override
	public void onEnable() {
		gamerManager = new GamerManager();
		kitManager = new KitManager();
		warpManager = new WarpManager();
		warpManager.load();
		Bukkit.getPluginManager().registerEvents(rankingHologram = new RankingHologram(), this);

		BukkitCommandFramework.INSTANCE.loadCommands(this.getClass(), "tk.yallandev.saintmc.kitpvp.command");

		for (Class<?> classes : ClassGetter.getClassesForPackage(getClass(), "tk.yallandev.saintmc.kitpvp.listener")) {
			if (Listener.class.isAssignableFrom(classes)) {

				try {
					Listener listener = (Listener) classes.newInstance();
					Bukkit.getPluginManager().registerEvents(listener, this);
				} catch (Exception e) {
					e.printStackTrace();
					CommonGeneral.getInstance().getLogger()
							.warning("Couldn't load " + classes.getSimpleName() + " listener!");
				}
			}
		}

		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	public void registerProtectionInConfig(double protection, String config) {
		BukkitMain.getInstance().getConfig().set(config + ".protection", protection);
		BukkitMain.getInstance().saveConfig();
	}

	public double getProtectionFromConfig(String config) {
		return BukkitMain.getInstance().getConfig().getDouble(config + ".protection", 5D);
	}

	public static boolean isFulliron() {
		return CommonGeneral.getInstance().getServerType() == ServerType.FULLIRON;
	}

	public static GameMain getInstance() {
		return instance;
	}

	public static GameMain getPlugin() {
		return instance;
	}

}
