package tk.yallandev.saintmc.kitpvp.command;

import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.types.PartyWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.party.PartyType;

public class PartyCommand implements CommandClass {

	@Command(name = "party", aliases = { "evento" })
	public void partyCommand(BukkitCommandArgs cmdArgs) {
		String[] args = cmdArgs.getArgs();
		CommandSender sender = cmdArgs.getSender();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + "§f para entrar no evento.");

			if (Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS)) {
				sender.sendMessage(
						" §e* §fUse §a/" + cmdArgs.getLabel() + " iniciar/start <type>§f para iniciar um evento.");
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " settime <time>§f para iniciar um evento.");
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " stop§f para finalizar um evento.");
			}

			return;
		}

		switch (args[0].toLowerCase()) {
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
				sender.sendMessage(" §c* §fJá há um evento em andamento!");
				return;
			}
			
			PartyType partyType = PartyType.NONE;
			
			try {
				partyType = PartyType.valueOf(args[1].toUpperCase());
			} catch (Exception ex) {
			}
			
			if (partyType == PartyType.NONE) {
				sender.sendMessage(" §c* §fO evento " + args[1] + " não existe!");
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
				sender.sendMessage(
						" §e* §fUse §a/" + cmdArgs.getLabel() + " stop§f para iniciar um evento.");
				return;
			}
			
			PartyWarp partyWarp = (PartyWarp) GameMain.getInstance().getWarpManager().getWarpByName("party");
			
			if (partyWarp.getPartyType() == PartyType.NONE) {
				sender.sendMessage(" §c* §fNão há evento no momento!");
				return;
			}
			
			partyWarp.getParty().forceEnd(null);
			break;
		}
		case "settime": {
			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
				return;

			if (args.length < 3) {
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " settime <time>§f para iniciar um evento.");
				return;
			}

			PartyWarp partyWarp = (PartyWarp) GameMain.getInstance().getWarpManager().getWarpByName("party");

			if (partyWarp.getPartyType() == PartyType.NONE) {
				sender.sendMessage(" §c* §fNenhum evento está ocorrendo no momento!");
				return;
			}

			Integer time = null;

			try {
				time = Integer.valueOf(args[1]);
			} catch (Exception ex) {
				sender.sendMessage(" §c* §fFormato de tempo inválido!");
				return;
			}
			
			partyWarp.getParty().setTime(time);
			sender.sendMessage(" §c* §fVocê alterou o tempo do evento para " + time + "!");
			break;
		}
		}
	}

}
