package tk.yallandev.anticheat;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;

@Getter
public class AnticheatMain extends JavaPlugin {

	@Getter
	private static AnticheatMain instance;

	private AnticheatController controller;

	@Override
	public void onLoad() {
		instance = this;
		super.onLoad();
	}

	@Override
	public void onEnable() {

		controller = new AnticheatController(this);
		controller.registerModules();

		BukkitCommandFramework.INSTANCE.loadCommands(this.getClass(), "tk.yallandev.anticheat.command");

		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
