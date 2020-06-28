package tk.yallandev.saintmc.kitpvp.command;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.party.PartyType;
import tk.yallandev.saintmc.kitpvp.warp.types.PartyWarp;

public class PartyCommand implements CommandClass {

	@Command(name = "evento", aliases = { "event", "party" })
	public void partyCommand(BukkitCommandArgs cmdArgs) {
		String[] args = cmdArgs.getArgs();
		CommandSender sender = cmdArgs.getSender();

		if (args.length == 0) {
			sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " para entrar no evento.");
			sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " hg para ir ao evento do §aHungerGames§f.");

			if (Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS)) {
				sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " iniciar/start <type> para iniciar um evento.");
				sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " settime <time> para iniciar um evento.");
				sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " stop para finalizar um evento.");
			}

			return;
		}

		switch (args[0].toLowerCase()) {
		case "hg": {
			Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

			if (player.isOnCooldown("connect-command")) {
				player.sendMessage("§cEspere mais "
						+ DateUtils.formatTime(player.getCooldown("connect-command"), CommonConst.DECIMAL_FORMAT)
						+ "s para se conectar novamente!");
				return;
			}

			player.setCooldown("connect-command", 4);
			break;
		}
		case "entrar": {
			if (cmdArgs.isPlayer()) {
				Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(sender.getUniqueId());

				PartyWarp warp = (PartyWarp) GameMain.getInstance().getWarpManager().getWarpByName("party");

				if (warp.getPartyType() == PartyType.NONE) {
					sender.sendMessage("§cNenhum evento está acontecendo no momento!");
					return;
				}

				gamer.setWarp(warp);
				gamer.setKit(null);
				warp.getParty().join(gamer);
			}
			break;
		}
		case "iniciar":
		case "start": {
			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
				return;

			if (args.length < 2) {
				sender.sendMessage(
						" §e* §fUse §a/" + cmdArgs.getLabel() + " iniciar/start <type>§f para iniciar um evento.");
				return;
			}

			PartyWarp partyWarp = (PartyWarp) GameMain.getInstance().getWarpManager().getWarpByName("party");

			if (partyWarp.getPartyType() != PartyType.NONE) {
				sender.sendMessage("§cJá há um evento em andamento!");
				return;
			}

			PartyType partyType = PartyType.NONE;

			try {
				partyType = PartyType.valueOf(args[1].toUpperCase());
			} catch (Exception ex) {
			}

			if (partyType == PartyType.NONE) {
				sender.sendMessage("§cO evento " + args[1] + " não existe!");
				return;
			}

			partyWarp.setPartyType(partyType);
			sender.sendMessage("§aVocê iniciou o evento " + partyWarp.getName() + "!");
			break;
		}
		case "finalizar":
		case "parar":
		case "stop": {
			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
				return;

			if (args.length <= 1) {
				sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " stop para iniciar um evento.");
				return;
			}

			PartyWarp partyWarp = (PartyWarp) GameMain.getInstance().getWarpManager().getWarpByName("party");

			if (partyWarp.getPartyType() == PartyType.NONE) {
				sender.sendMessage("§cNão há evento no momento!");
				return;
			}

			partyWarp.getParty().forceEnd(null);
			partyWarp.setPartyType(null);
			break;
		}
		case "settime": {
			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
				return;

			if (args.length < 2) {
				sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " settime <time> para iniciar um evento.");
				return;
			}

			PartyWarp partyWarp = (PartyWarp) GameMain.getInstance().getWarpManager().getWarpByName("party");

			if (partyWarp.getPartyType() == PartyType.NONE) {
				sender.sendMessage("§cNenhum evento está ocorrendo no momento!");
				return;
			}

			Integer time = null;

			try {
				time = Integer.valueOf(args[1]);
			} catch (Exception ex) {
				sender.sendMessage("§cFormato de tempo inválido!");
				return;
			}

			partyWarp.getParty().setTime(time);
			sender.sendMessage("§aVocê alterou o tempo do evento para " + time + "!");
			break;
		}
		}
	}

}
