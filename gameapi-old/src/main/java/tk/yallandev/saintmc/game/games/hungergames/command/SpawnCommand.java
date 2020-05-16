package tk.yallandev.saintmc.game.games.hungergames.command;

import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.stage.GameStage;

public class SpawnCommand implements CommandClass {

	@Command(name = "spawn")
	public void spawnCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;
		GameStage stage = GameMain.getPlugin().getGameStage();
		Gamer gamer = Gamer.getGamer(args.getPlayer());
		
		if (!GameStage.isPregame(stage)) {
			if (!gamer.isGamemaker() && !gamer.isSpectator()) {
				args.getPlayer().sendMessage("§%command-spawn-no-access%§");
				return;
			}
		}
		
		args.getPlayer().sendMessage("§%command-spawn-teleported%§");
		HungerGamesMode.teleportToSpawn(args.getPlayer());
	}

}
