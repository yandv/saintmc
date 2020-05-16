package tk.yallandev.saintmc.game.command;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.stage.GameStage;

public class StartCommand implements CommandClass {

	@Command(name = "start", aliases = { "comecar", "iniciar" }, groupToUse = Group.MODPLUS)
	public void time(CommandArgs args) {
		if (GameStage.isPregame(GameMain.getPlugin().getGameStage())) {
			args.getSender().sendMessage(" §a* §fVocê iniciou o jogo!");
			GameMain.getPlugin().startGame();
			CommonGeneral.getInstance().getMemberManager().broadcast("§7[INFO] O " + args.getSender(), Group.TRIAL);
		}
	}

}