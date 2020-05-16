package tk.yallandev.saintmc.game.command;

import org.bukkit.Bukkit;

import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameTimerEvent;

public class TimeCommand implements CommandClass {

	@Command(name = "time", aliases = { "tempo" }, groupToUse = Group.MOD)
	public void time(CommandArgs args) {
		if (args.getArgs().length != 1) {
			args.getSender().sendMessage(" §e* §fUse §a/tempo <player>§f para setar um grupo.");
			return;
		}
		
		long time;
		
		try {
			time = DateUtils.parseDateDiff(args.getArgs()[0], true);
		} catch (Exception e) {
			args.getSender().sendMessage(" §c* §fO formato de tempo não é v§lido.");
			return;
		}
		
		int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000);
		
		if (seconds > 1*60*60) {
			return;
		}
		
		GameMain.getPlugin().setTimer(seconds);
		args.getSender().sendMessage(" §c* §fO tempo foi alterado para §a%time%§f!.".replace("%time%", DateUtils.formatDifference(seconds)).replace("%stage%", GameMain.getPlugin().getGameStage().toString()));
		Bukkit.getPluginManager().callEvent(new GameTimerEvent());
	}

}