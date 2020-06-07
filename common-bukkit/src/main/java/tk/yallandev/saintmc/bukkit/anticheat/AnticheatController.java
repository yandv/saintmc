package tk.yallandev.saintmc.bukkit.anticheat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.AutoclickModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.AutosoupModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.FastbowModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.FlyModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.ForcefieldModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.GlideModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.HitboxModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.MacroModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.SpeedModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.VapeModule;
import tk.yallandev.saintmc.bukkit.anticheat.modules.register.VelocityModule;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.networking.packet.AnticheatBanPacket;
import tk.yallandev.saintmc.common.permission.Group;

public class AnticheatController {

	private Map<UUID, Autoban> banMap;

	private AutobanListener autobanListener = new AutobanListener();

	public AnticheatController() {
		banMap = new HashMap<>();
	}

	public void registerModules() {
		Bukkit.getPluginManager().registerEvents(new VapeModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new AutoclickModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new MacroModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new AutosoupModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new FastbowModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new ForcefieldModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new GlideModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new SpeedModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new FlyModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new VelocityModule(), BukkitMain.getInstance());
		Bukkit.getPluginManager().registerEvents(new HitboxModule(), BukkitMain.getInstance());

		Bukkit.getPluginManager().registerEvents(new AnticheatListener(), BukkitMain.getInstance());
	}

	public void autoban(Player player, String hackName) {
		if (banMap.containsKey(player.getUniqueId()))
			return;

		banMap.put(player.getUniqueId(), new Autoban(player.getName(), hackName));
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

		public void ban() {
			Bukkit.broadcastMessage("§cUm jogador de sua sala foi banido usando trapaças!");

			Player player = Bukkit.getPlayer(playerName);
			BukkitMain.getInstance().getPacketController().sendPacket(
					new AnticheatBanPacket(hackType, System.currentTimeMillis() + (24 * 60 * 60 * 1000 * 14)), player);
		}

	}

	public class AutobanListener extends ManualRegisterableListener {

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			Player player = event.getPlayer();

			if (banMap.containsKey(player.getUniqueId())) {
				banMap.get(player.getUniqueId()).ban();
				banMap.remove(player.getUniqueId());
			}
		}

		@EventHandler
		public void onUpdate(UpdateEvent event) {
			if (event.getType() == UpdateType.SECOND) {
				Iterator<Entry<UUID, Autoban>> iterator = banMap.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<UUID, Autoban> entry = iterator.next();

					if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
						entry.getValue().ban();
						iterator.remove();
						continue;
					}

					int time = (int) ((entry.getValue().getExpireTime() - System.currentTimeMillis()) / 1000);

					if (time == 30 || time == 15 || time == 5) {
						CommonGeneral.getInstance().getMemberManager().getMembers().stream()
								.filter(member -> member.hasGroupPermission(Group.TRIAL)
										&& member.getAccountConfiguration().isAnticheatEnabled())
								.forEach(member -> member.sendMessage("§cO jogador " + entry.getValue().getPlayerName()
										+ " será banido em " + time + " segundos!"));
					}
				}

				if (banMap.isEmpty())
					unregisterListener();
			}
		}

	}

}
