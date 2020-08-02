package tk.yallandev.saintmc.kitpvp.command;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KitCommand implements CommandClass {

	@Command(name = "kit")
	public void kit(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (args.length == 0) {
			player.sendMessage(" §e* §fUse §a/kit <kitName>§f para selecionar um kit.");
			return;
		}

		if (!gamer.getWarp().getWarpSettings().isKitEnabled()) {
			player.sendMessage(" §c* §fEssa warp não permite kit!");
			return;
		}

		Kit kit = GameMain.getInstance().getKitManager().getKit(args[0]);

		if (kit == null) {
			player.sendMessage(" §c* §fO kit §a" + args[0] + "§f não existe!");
			return;
		}

		if (gamer.hasKit()) {
			player.sendMessage(" §c* §fVocê já está §ausando§f um kit!");
			return;
		}

		if (!gamer.hasKitPermission(kit)) {
			player.sendMessage(" §c* §fVocê não possui este kit!");
			return;
		}

		gamer.setKit(kit);
		GameMain.getInstance().getKitManager().selectKit(player, kit);
		player.sendMessage(" §a* §fVocê selecionou o kit §a" + kit.getKitName() + "§f!");
	}

}
