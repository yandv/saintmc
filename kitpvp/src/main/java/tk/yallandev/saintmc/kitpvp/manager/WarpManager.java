package tk.yallandev.saintmc.kitpvp.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.FpsWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.LavaWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.MainWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.PartyWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.SpawnWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.SumoWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.VoidWarp;

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
		loadWarp(new VoidWarp());

		loadWarp(new PartyWarp());
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

	public void setWarp(Player player, String warpName, boolean forced) {
		Warp warp = getWarpByName(warpName);

		if (warp == null) {
			CommonGeneral.getInstance().getLogger().info("The warp " + warpName + " doesnt exist!");
			return;
		}

		Entity entity = player.getPassenger();

		if (entity != null)
			entity.leaveVehicle();

		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer == null)
			return;

		if (gamer.getWarp() != null) {
			gamer.getWarp().getPlayers().remove(gamer.getUuid());
			Bukkit.getPluginManager().callEvent(new PlayerWarpQuitEvent(gamer.getPlayer(), gamer.getWarp()));
			CommonGeneral.getInstance().debug(gamer.getPlayer().getName() + " leave from " + gamer.getWarp().getName());
		}

		warp.registerListener();

		if (warp.getScoreboard() != null)
			warp.getScoreboard().register();

		gamer.setWarp(warp);
		gamer.setKit(null);
		gamer.getPlayer().teleport(warp.getSpawnLocation());
		warp.getPlayers().add(player.getUniqueId());

		player.setFireTicks(0);

		Bukkit.getPluginManager().callEvent(new PlayerWarpJoinEvent(gamer.getPlayer(), warp));
		CommonGeneral.getInstance().debug(gamer.getPlayer().getName() + " join in " + warp.getName());
	}

	public void removeWarp(Gamer gamer) {
		gamer.getWarp().getPlayers().remove(gamer.getUuid());
		Bukkit.getPluginManager().callEvent(new PlayerWarpQuitEvent(gamer.getPlayer(), gamer.getWarp()));

		gamer.setWarp(null);
	}

	public void teleport(Player player, Warp warp, int time) {
		setWarp(player, warp.getName(), false);
		player.sendMessage("§aVocê entrou na warp " + warp.getName() + "!");
	}

}
