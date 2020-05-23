package br.com.saintmc.hungergames.command;

import br.com.saintmc.hungergames.GameGeneral;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class GameCommand implements CommandClass {
	
	@Command(name = "tempo")
	public void tempoCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();
		
		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <tempo>§f para alterar o tempo do jogo!");
			return;
		}
		
		long time;
		
		try {
			time = DateUtils.parseDateDiff(args[0], true);
		} catch (Exception e) {
			sender.sendMessage(" §c* §fO formato de tempo não é v§lido.");
			return;
		}
		
		int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000);
		
		if (seconds >= 60*60*180)
			seconds = 60*60*180;
		
		sender.sendMessage(" §a* §fO tempo do jogo foi alterado para §a" + args[0] + "§f!");
		GameGeneral.getInstance().setTime(seconds);
	}

}
