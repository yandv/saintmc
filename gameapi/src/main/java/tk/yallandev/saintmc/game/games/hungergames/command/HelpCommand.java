package tk.yallandev.saintmc.game.games.hungergames.command;

import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;

public class HelpCommand implements CommandClass {
	
	@Command(name = "help")
	public void asd(CommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage("§fSala: §a#1");
	}

}
