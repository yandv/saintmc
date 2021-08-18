package tk.yallandev.saintmc.login;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.command.manual.LoginCommand;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.tag.TagWrapper;
import tk.yallandev.saintmc.login.listener.CaptchaListener;
import tk.yallandev.saintmc.login.listener.LoginListener;
import tk.yallandev.saintmc.login.listener.PlayerListener;
import tk.yallandev.saintmc.login.listener.QueueListener;

public class LoginMain extends JavaPlugin implements Listener {

	@Getter
	private static LoginMain instance;

	public static final Tag ORIGINAL_TAG = TagWrapper.create("ORIGINAL", "§6§lORIGINAL§6", Group.PRO, 24)
			.setCustom(true);
	public static final Tag LOGGING_TAG = TagWrapper.create("LOGGING", "§8§lLOGANDO§8", Group.MEMBRO, 24)
			.setCustom(true);

	@Override
	public void onEnable() {
		instance = this;
		BukkitCommandFramework.INSTANCE.registerCommands(new LoginCommand());
		Tag.registerTag(LOGGING_TAG);
		Tag.registerTag(ORIGINAL_TAG);

		Bukkit.getPluginManager().registerEvents(new QueueListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new LoginListener(), this);
		Bukkit.getPluginManager().registerEvents(new CaptchaListener(), this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
