package tk.yallandev.saintmc.kitpvp.command;

import tk.yallandev.saintmc.common.command.CommandClass;

public class PartyCommand implements CommandClass {

//	@Command(name = "party", aliases = { "evento" })
//	public void partyCommand(BukkitCommandArgs cmdArgs) {
//		String[] args = cmdArgs.getArgs();
//		CommandSender sender = cmdArgs.getSender();
//
//		if (args.length == 0) {
//			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + "§f para entrar no evento.");
//
//			if (Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS)) {
//				sender.sendMessage(
//						" §e* §fUse §a/" + cmdArgs.getLabel() + " iniciar/start <type>§f para iniciar um evento.");
//				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " settime <time>§f para iniciar um evento.");
//				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " stop§f para finalizar um evento.");
//			}
//
//			return;
//		}
//
//		switch (args[0].toLowerCase()) {
//		case "entrar": {
//			if (cmdArgs.isPlayer()) {
//				Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(sender.getUniqueId());
//
//				PlayerJoinWarpEvent warpEvent = new PlayerJoinWarpEvent(gamer.getPlayer(), WarpType.PARTY.getWarp());
//
//				Bukkit.getPluginManager().callEvent(warpEvent);
//
//				if (!warpEvent.isCancelled()) {
//					if (AdminMode.getInstance().isAdmin(cmdArgs.getPlayer())) {
//
//						sender.sendMessage(" §a* §fVocê entrou no evento como §5moderador§f");
//						cmdArgs.getPlayer().teleport(BukkitMain.getInstance().getLocationFromConfig("evento"));
//						cmdArgs.getPlayer().getInventory().clear();
//						return;
//					}
//
//					gamer.getWarpType().getWarp().leave(gamer.getPlayer());
//					gamer.setWarpType(WarpType.PARTY);
//					WarpType.PARTY.getWarp().join(gamer.getPlayer());
//				}
//			}
//			break;
//		}
//		case "iniciar":
//		case "start": {
//			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
//				return;
//
//			if (args.length < 2) {
//				sender.sendMessage(
//						" §e* §fUse §a/" + cmdArgs.getLabel() + " iniciar/start <type>§f para iniciar um evento.");
//				return;
//			}
//
//			PartyWarp partyWarp = (PartyWarp) WarpType.PARTY.getWarp();
//
//			if (partyWarp.getPartyType() != PartyType.NONE) {
//				sender.sendMessage(" §c* §fUm evento já está ocorrendo no momento!");
//				return;
//			}
//
//			PartyType partyType = PartyType.valueOf(args[1].toUpperCase());
//
//			partyWarp.start(partyType);
//			sender.sendMessage(" §c* §fVocê inicou um evento!");
//			break;
//		}
//		case "finalizar":
//		case "parar":
//		case "stop": {
//			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
//				return;
//
//			if (args.length < 2) {
//				sender.sendMessage(
//						" §e* §fUse §a/" + cmdArgs.getLabel() + " iniciar/start <type>§f para iniciar um evento.");
//				return;
//			}
//
//			PartyWarp partyWarp = (PartyWarp) WarpType.PARTY.getWarp();
//
//			if (partyWarp.getPartyType() != PartyType.NONE) {
//				sender.sendMessage(" §c* §fUm evento já está ocorrendo no momento!");
//				return;
//			}
//
//			PartyType partyType = PartyType.valueOf(args[1].toUpperCase());
//
//			partyWarp.start(partyType);
//			sender.sendMessage(" §c* §fVocê inicou um evento!");
//			break;
//		}
//		case "settime": {
//			if (!Member.hasGroupPermission(sender.getUniqueId(), Group.MODPLUS))
//				return;
//
//			if (args.length < 3) {
//				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " settime <time>§f para iniciar um evento.");
//				return;
//			}
//
//			PartyWarp partyWarp = (PartyWarp) WarpType.PARTY.getWarp();
//
//			if (partyWarp.getPartyType() == PartyType.NONE) {
//				sender.sendMessage(" §c* §fNenhum evento está ocorrendo no momento!");
//				return;
//			}
//
//			Integer time = null;
//
//			try {
//				time = Integer.valueOf(args[1]);
//			} catch (Exception ex) {
//				sender.sendMessage(" §c* §fFormato de tempo inválido!");
//				return;
//			}
//
//			partyWarp.setTime(time, sender);
//			sender.sendMessage(" §c* §fVocê alterou o tempo do evento para " + time + "!");
//			break;
//		}
//		}
//	}

}
