package tk.yallandev.saintmc.bungee.command.register;

import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;

public class WooCommand implements CommandClass {

    @Command(name = "woo", groupToUse = Group.GERENTE, runAsync = true)
    public void wooCommand(CommandArgs cmdArgs) {
    	String[] args = cmdArgs.getArgs();
    	
    	if (args.length == 0) {
    		cmdArgs.getSender().sendMessage(" §a* §fUse §a/woo check§f para verificar!");
    		return;
    	}
    	
    	if (args[0].equalsIgnoreCase("check")) {
    		try {
    			cmdArgs.getSender().sendMessage(" §a* §fEstamos verificando...");
    			
    			BungeeMain.getInstance().getStoreManager().check();
    		} catch (Exception ex) {
    			cmdArgs.getSender().sendMessage(" §c* §fOcorreu um erro durante verificavamos!");
    			ex.printStackTrace();
    		}
    	}
    }

}
