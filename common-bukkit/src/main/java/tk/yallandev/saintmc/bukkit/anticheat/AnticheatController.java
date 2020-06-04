package tk.yallandev.saintmc.bukkit.anticheat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
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

public class AnticheatController {
	
	private Map<UUID, Autoban> banMap;

	public AnticheatController() {
		banMap = new HashMap<>();
	}

	public void registerModules() {
		VapeModule vapeModule = new VapeModule();

		BukkitMain.getInstance().getServer().getMessenger().registerIncomingPluginChannel(BukkitMain.getInstance(),
				"LOLIMAHCKER", vapeModule);
		Bukkit.getPluginManager().registerEvents(vapeModule, BukkitMain.getInstance());
		
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
	
	public void autoban(Player player) {
		if (banMap.containsKey(player.getUniqueId()))
			return;
		
		banMap.put(player.getUniqueId(), new Autoban());
	}
	
	public Map<UUID, Autoban> getBanMap() {
		return banMap;
	}
	
	@Getter
	public class Autoban {
		
		private long expireTime = System.currentTimeMillis();
		
	}
	
}
