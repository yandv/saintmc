package tk.yallandev.saintmc.kitpvp.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.FpsWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.LavaWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.MainWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.SpawnWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.SumoWarp;

public class WarpManager {

	private Map<String, Warp> warpMap;

	public WarpManager() {
		warpMap = new HashMap<>();
	}

	public void load() {
		loadWarp(new SpawnWarp());
		loadWarp(new FpsWarp());
		loadWarp(new LavaWarp());
		loadWarp(new MainWarp());
		loadWarp(new ShadowWarp());
		loadWarp(new SumoWarp());
	}

	/*
	 * Warp Controller
	 */
	
	public Warp getWarpByName(String warpName) {
		return warpMap.get(warpName.toLowerCase());
	}
	
	public Collection<Warp> getWarps() {
		return warpMap.values();
	}

	public void loadWarp(Warp warp) {
		warpMap.put(warp.getName().toLowerCase(), warp);
//        Bukkit.getPluginManager().registerEvents(warp, GameMain.getInstance());
	}
	
	/*
	 * Gamer Controller
	 */

	public void setWarp(Gamer gamer, String warpName, boolean forced) {
		Warp warp = getWarpByName(warpName);

		if (warp == null) {
			CommonGeneral.getInstance().getLogger().info("The warp " + warpName + " doesnt exist!");
			return;
		}
		
        if (GameMain.getInstance().getGamerManager().getGamers().stream().filter(g -> warp.inWarp(g.getPlayer())).collect(Collectors.toList()).isEmpty()) {
        	GameMain.getInstance().getServer().getPluginManager().registerEvents(warp, GameMain.getInstance());
        }
		
		Warp lastWarp = gamer.getWarp();

		gamer.setWarp(warp);
		gamer.setKit(null);
		gamer.getPlayer().teleport(warp.getSpawnLocation());
		
		Bukkit.getPluginManager().callEvent(new PlayerWarpJoinEvent(gamer.getPlayer(), warp));
		Bukkit.getPluginManager().callEvent(new PlayerWarpQuitEvent(gamer.getPlayer(), lastWarp));
	}

	public void removeWarp(Gamer gamer) {
		Bukkit.getPluginManager().callEvent(new PlayerWarpQuitEvent(gamer.getPlayer(), gamer.getWarp()));
		
		gamer.setWarp(null);
	}

	public void teleport(Gamer gamer, Warp warp, int time) {
		setWarp(gamer, warp.getName(), false);
		gamer.getPlayer().sendMessage("§a§l> §fVocê foi teletransportado para a warp §a%warpName%§f!".replace("%warpName%", warp.getName()));
	}

}
