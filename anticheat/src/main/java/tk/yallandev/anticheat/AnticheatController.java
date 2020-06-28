package tk.yallandev.anticheat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import lombok.Getter;
import tk.yallandev.anticheat.check.register.HitboxHack;
import tk.yallandev.anticheat.check.register.ReachHack;
import tk.yallandev.anticheat.listener.AnticheatListener;
import tk.yallandev.anticheat.listener.AutobanListener;
import tk.yallandev.anticheat.stats.PlayerStatsController;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.networking.packet.AnticheatBanPacket;

@Getter
public class AnticheatController {

	@Getter
	private static AnticheatController instance = new AnticheatController(BukkitMain.getInstance());

	private Map<UUID, Autoban> banMap;
	private AutobanListener autobanListener;

	private PlayerStatsController playerStatsController;
	private Plugin plugin;

	public AnticheatController(Plugin plugin) {
		banMap = new HashMap<>();
		autobanListener = new AutobanListener(this);
		playerStatsController = new PlayerStatsController();
	}

	public void registerModules() {
//		Bukkit.getPluginManager().registerEvents(new VapeModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new AutoclickModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new MacroModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new AutosoupModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new FastbowModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new ForcefieldModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new GlideModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new SpeedModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new FlyModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new VelocityModule(), BukkitMain.getInstance());
//		Bukkit.getPluginManager().registerEvents(new HitboxModule(), BukkitMain.getInstance());
		System.out.println("1");

		Bukkit.getPluginManager().registerEvents(new HitboxHack(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new ReachHack(), BukkitMain.getInstance());

		Bukkit.getPluginManager().registerEvents(new AnticheatListener(), BukkitMain.getInstance());
	}

	public void autoban(Player player, String hackName) {
		if (banMap.containsKey(player.getUniqueId()))
			return;

		banMap.put(player.getUniqueId(), new Autoban(player.getName(), hackName));
		autobanListener.registerListener();
	}

	public void autoban(Player player, String hackName, long time) {
		banMap.put(player.getUniqueId(), new Autoban(player.getName(), hackName, time));
		autobanListener.registerListener();
	}

	public Map<UUID, Autoban> getBanMap() {
		return banMap;
	}

	@Getter
	public class Autoban {

		private String playerName;
		private String hackType;
		private long expireTime = System.currentTimeMillis() + 60000l;

		public Autoban(String playerName, String hackType) {
			this.playerName = playerName;
			this.hackType = hackType;
		}

		public Autoban(String playerName, String hackType, long expireTime) {
			this.playerName = playerName;
			this.hackType = hackType;
			this.expireTime = expireTime;
		}

		public void ban() {
			Bukkit.broadcastMessage("§cUm jogador de sua sala foi banido usando trapaças!");

			Player player = Bukkit.getPlayer(playerName);
			BukkitMain.getInstance().getPacketController().sendPacket(
					new AnticheatBanPacket(hackType, System.currentTimeMillis() + (24 * 60 * 60 * 1000 * 14)), player);
		}

	}

	public PlayerStatsController getPlayerController() {
		return playerStatsController;
	}

}
