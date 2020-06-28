package tk.yallandev.saintmc.kitpvp.command;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;

public class FeastCommand implements CommandClass {

	@Command(name = "feast")
	public void feastCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		((BukkitMember) cmdArgs.getSender()).getPlayer()
				.setCompassTarget(BukkitMain.getInstance().getLocationFromConfig("enchantment-table"));
		cmdArgs.getSender().sendMessage("§aSua bussola está apontando para o feast!");
	}
}
