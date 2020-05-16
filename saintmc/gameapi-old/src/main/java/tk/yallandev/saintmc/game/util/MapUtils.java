package tk.yallandev.saintmc.game.util;

import java.io.File;

import org.bukkit.Bukkit;

public class MapUtils {

	public static void deleteWorld(String world) {
		Bukkit.getServer().unloadWorld(world, false);
		deleteDir(new File(world));
		Bukkit.getLogger().info("Apagando mundo '" + world + "'...");
	}

	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));
			}
		}
		dir.delete();
	}
}
