package tk.yallandev.saintmc.bukkit.permission;

import org.bukkit.Server;
import org.bukkit.event.Listener;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.permission.injector.PermissionMatcher;
import tk.yallandev.saintmc.bukkit.permission.injector.RegExpMatcher;
import tk.yallandev.saintmc.bukkit.permission.injector.regexperm.RegexPermissions;
import tk.yallandev.saintmc.bukkit.permission.listener.PermissionListener;
import tk.yallandev.saintmc.common.server.ServerType;

public class PermissionManager {
	
	private BukkitMain main;

	private RegexPermissions regexPerms;
	protected PermissionMatcher matcher = new RegExpMatcher();
	protected PermissionListener superms;

	public PermissionManager(BukkitMain main) {
		this.main = main;
	}

	public void onEnable() {
		registerListener(superms = new PermissionListener(this));
		regexPerms = new RegexPermissions(this);
	}

	public void onDisable() {
		if (this.regexPerms != null) {
			this.regexPerms.onDisable();
			this.regexPerms = null;
		}
		
		if (this.superms != null) {
			this.superms.onDisable();
			this.superms = null;
		}
	}
	
	public BukkitMain getPlugin() {
		return main;
	}

	public Server getServer() {
		return main.getServer();
	}
	
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, main);
	}

	public RegexPermissions getRegexPerms() {
		return regexPerms;
	}

	public ServerType getServerType() {
		return ServerType.NETWORK;
	}

	public PermissionMatcher getPermissionMatcher() {
		return this.matcher;
	}
}
