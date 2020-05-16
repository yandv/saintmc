package tk.yallandev.saintmc.game.games.hungergames.command;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.game.games.hungergames.schedule.GameScheduler;

public class FeastCommand implements CommandClass {

	@Command(name = "feast")
	public void feastCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;
		
		Player player = args.getPlayer();
		
		if (GameScheduler.getFeastManager().getFeastLocation() == null) {
			player.sendMessage("§7O feast ainda não §cspawnou§7!");
		} else {
			player.sendMessage("§7Bussola apontando para o feast!");
			player.setCompassTarget(GameScheduler.getFeastManager().getFeastLocation());
		}
	}

}
