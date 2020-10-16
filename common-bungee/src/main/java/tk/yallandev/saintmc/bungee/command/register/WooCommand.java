package tk.yallandev.saintmc.bungee.command.register;

import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;

public class WooCommand implements CommandClass {

	@Command(name = "woo", groupToUse = Group.ADMIN, runAsync = true)
	public void wooCommand(CommandArgs cmdArgs) {
		try {
			cmdArgs.getSender().sendMessage("§aEstou verificando os pedidos...");

			BungeeMain.getInstance().getStoreController().check(cmdArgs.getSender());
		} catch (Exception ex) {
			cmdArgs.getSender().sendMessage("§cOcorreu um erro durante verificavamos!");
			ex.printStackTrace();
		}
	}

}
