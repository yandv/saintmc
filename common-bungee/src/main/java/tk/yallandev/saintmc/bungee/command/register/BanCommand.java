package tk.yallandev.saintmc.bungee.command.register;

import java.util.UUID;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.ban.constructor.Ban.UnbanReason;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.ban.constructor.Warn;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class BanCommand implements CommandClass {

	@Command(name = "ban", aliases = { "banir" }, runAsync = true, groupToUse = Group.TRIAL)
	public void banCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/ban <player> <motivo>§f para banir um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer())
			playerGroup = Member.getGroup(cmdArgs.getSender().getUniqueId());
		else
			playerGroup = Group.DONO;

		if (cmdArgs.isPlayer())
			if (playerGroup.ordinal() < player.getGroup().ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			sb.append(args[i]).append(" ");

		Ban ban = new Ban(player.getUniqueId(), cmdArgs.isPlayer() ? cmdArgs.getSender().getName() : "CONSOLE",
				sender.getUniqueId(), args.length == 1 ? "Sem motivo" : sb.toString().trim(), -1l);

		if (BungeeMain.getInstance().getPunishManager().ban(player, ban)) {
			sender.sendMessage(
					" §a* §fVocê baniu o jogador §a" + player.getPlayerName() + "§f por §a" + ban.getReason() + "§f!");
		} else {
			sender.sendMessage("§cO jogador já está banido!");
		}
	}

	@Command(name = "tempban", runAsync = true, groupToUse = Group.HELPER)
	public void tempbanCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/tempban <player> <tempo> <motivo>§f para banir um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer())
			playerGroup = Member.getGroup(cmdArgs.getPlayer().getUniqueId());
		else
			playerGroup = Group.DONO;

		if (cmdArgs.isPlayer())
			if (playerGroup.ordinal() < player.getGroup().ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		long expiresCheck;

		try {
			expiresCheck = DateUtils.parseDateDiff(args[1], true);
		} catch (Exception e1) {
			sender.sendMessage(" §c* §fFormato de tempo invalido");
			return;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 2; i < args.length; i++)
			sb.append(args[i]).append(" ");

		Ban ban = new Ban(player.getUniqueId(), cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE",
				sender.getUniqueId(), args.length == 1 ? "Sem motivo" : sb.toString().trim(), expiresCheck);

		if (BungeeMain.getInstance().getPunishManager().ban(player, ban)) {
			sender.sendMessage(
					" §a* §fVocê baniu o jogador §a" + player.getPlayerName() + "§f por §a" + ban.getReason() + "§f!");
		} else {
			sender.sendMessage("§cO jogador já está banido!");
		}
	}

	@Command(name = "unban", aliases = { "desbanir" }, runAsync = true, groupToUse = Group.GERENTE)
	public void unbanCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/unban <player> <unbanReason>§f para desbanir um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		UnbanReason unbanReason = UnbanReason.OTHER;

		try {
			unbanReason = UnbanReason.valueOf(args[1].toUpperCase());
		} catch (Exception ex) {
			unbanReason = UnbanReason.OTHER;
		}

		if (BungeeMain.getInstance().getPunishManager().unban(player, sender.getUniqueId(),
				cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE", unbanReason)) {
			sender.sendMessage(" §a* §fVocê desbaniu o jogador §a" + player.getPlayerName() + "§f!");
		} else {
			sender.sendMessage(" §c* §fNão foi possível banir o jogador!");
		}
	}

	@Command(name = "mute", aliases = { "silenciar" }, runAsync = true, groupToUse = Group.HELPER)
	public void muteCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/" + cmdArgs.getLabel() + " <player> <motivo>§f para banir um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer())
			playerGroup = Member.getGroup(cmdArgs.getPlayer().getUniqueId());
		else
			playerGroup = Group.DONO;

		if (cmdArgs.isPlayer())
			if (playerGroup.ordinal() < player.getGroup().ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			sb.append(args[i]).append(" ");

		Mute mute = new Mute(player.getUniqueId(), cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE",
				sender.getUniqueId(), args.length == 1 ? "Sem motivo" : sb.toString().trim(), -1l);

		if (BungeeMain.getInstance().getPunishManager().mute(player, mute)) {
			sender.sendMessage(
					" §a* §fVocê mutou o jogador §a" + player.getPlayerName() + "§f por §a" + mute.getReason() + "§f!");
		} else {
			sender.sendMessage(" §c* §fNão foi possível mutar o jogador!");
		}
	}

	@Command(name = "tempmute", runAsync = true, groupToUse = Group.HELPER)
	public void tempmuteCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/tempban <player> <tempo> <motivo>§f para banir um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer())
			playerGroup = Member.getGroup(cmdArgs.getPlayer().getUniqueId());
		else
			playerGroup = Group.DONO;

		if (cmdArgs.isPlayer())
			if (playerGroup.ordinal() < player.getGroup().ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		long expiresCheck;

		try {
			expiresCheck = DateUtils.parseDateDiff(args[1], true);
		} catch (Exception e1) {
			sender.sendMessage(" §c* §fFormato de tempo invalido");
			return;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 2; i < args.length; i++)
			sb.append(args[i]).append(" ");

		Mute mute = new Mute(player.getUniqueId(), cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE",
				sender.getUniqueId(), args.length == 1 ? "Sem motivo" : sb.toString().trim(), expiresCheck);

		if (BungeeMain.getInstance().getPunishManager().mute(player, mute)) {
			sender.sendMessage(
					" §a* §fVocê mutou o jogador §a" + player.getPlayerName() + "§f por §a" + mute.getReason() + "§f!");
		} else {
			sender.sendMessage(" §c* §fNão foi possível mutar o jogador!");
		}
	}

	@Command(name = "unmute", runAsync = true, groupToUse = Group.TRIAL)
	public void unmuteCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/unmute <player>§f para desmutar um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		if (BungeeMain.getInstance().getPunishManager().unmute(player, sender.getUniqueId(),
				cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE")) {
			sender.sendMessage(" §a* §fVocê desmutar o jogador §a" + player.getPlayerName() + "§f!");
		} else {
			sender.sendMessage(" §c* §fNão foi possível desmutar o jogador!");
		}
	}

	@Command(name = "warn", aliases = { "avisar" }, runAsync = true, groupToUse = Group.HELPER)
	public void warnCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §c* §fUse §a/warn <player> <motivo>§f para avisar um jogador!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer())
			playerGroup = Member.getGroup(cmdArgs.getPlayer().getUniqueId());
		else
			playerGroup = Group.DONO;

		if (cmdArgs.isPlayer())
			if (playerGroup.ordinal() < player.getGroup().ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			sb.append(args[i]).append(" ");

		int id = CommonGeneral.getInstance().getPunishData().getTotalWarn() + 1;

		Warn warn = new Warn(uuid, id, cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE",
				sender.getUniqueId(), sb.toString().trim(), System.currentTimeMillis() + (1000 * 60 * 60 * 12));

		if (BungeeMain.getInstance().getPunishManager().warn(player, warn)) {
			sender.sendMessage(" §a* §fVocê alertou o jogador §a" + player.getPlayerName() + "§f por §a"
					+ warn.getReason() + "§f!");
		} else {
			sender.sendMessage(" §c* §fNão foi possível alertar o jogador!");
		}
	}
}
