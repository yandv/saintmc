package tk.yallandev.saintmc.screenshare;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.update.UpdatePlugin;

public class ScreenshareMain extends JavaPlugin implements Listener {

	private static Scoreboard scoreboard;

	{
		scoreboard = new SimpleScoreboard("§4§lSCREENSHARE");

		scoreboard.blankLine(5);
		scoreboard.setScore(4, new Score("§fVocê está na screenshare", "2"));
		scoreboard.setScore(3, new Score("§fObedeça o staff", "1"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§c" + CommonConst.WEBSITE, "site"));
	}

	@Override
	public void onEnable() {
		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(
				new File(ScreenshareMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"Screenshare", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		Bukkit.getPluginManager().registerEvents(this, this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.sendMessage("§f");
		player.sendMessage("§f");
		player.sendMessage("§aVocê está na screenshare!");
		player.sendMessage("§aBaixe o Anydesk em http://anydesk.pt/");
		player.sendMessage("§aCaso saia poderá ser banido do servidor!");
		player.sendMessage("§aApós o procedimento do staff, você será liberado!");
		player.sendMessage("§f");
		event.setJoinMessage(null);

		player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
		scoreboard.createScoreboard(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage("§c" + event.getPlayer().getName() + " saiu!");
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.VOID)
			event.getEntity().teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

}
