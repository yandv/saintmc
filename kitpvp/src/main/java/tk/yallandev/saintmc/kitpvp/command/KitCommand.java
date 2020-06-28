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
			player.sendMessage("§cUse /kit <kitName> para selecionar um kit.");
			return;
		}

		if (!gamer.getWarp().getWarpSettings().isKitEnabled()) {
			player.sendMessage("§cVocê não pode selecionar kit nessa warp!");
			return;
		}

		Kit kit = GameMain.getInstance().getKitManager().getKit(args[0]);

		if (kit == null) {
			player.sendMessage("§cO kit " + args[0] + " não existe!");
			return;
		}

		if (gamer.hasKit()) {
			player.sendMessage("§cVocê não pode selecionar outro kit!");
			return;
		}

		if (!gamer.hasKitPermission(kit)) {
			player.sendMessage("§cVocê não possui permissão para selecionar esse kit!");
			return;
		}

		gamer.setKit(kit);
		GameMain.getInstance().getKitManager().selectKit(player, kit);
		player.sendMessage("§aVocê selecionou o kit " + kit.getKitName() + "!");
	}

}
