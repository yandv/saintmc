package tk.yallandev.saintmc.bungee.command.register;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class GroupCommand implements CommandClass {

	@Command(name = "groupset", usage = "/<command> <player> <group>", groupToUse = Group.GERENTE, aliases = {
			"setargrupo" }, runAsync = true)
	public void groupsetCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length != 2) {
			sender.sendMessage(" §e* §fUse §a/groupset <player> <group>§f para setar um grupo.");
			return;
		}

		Group grupo = null;

		try {
			grupo = Group.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(" §c* §fO grupo §a" + args[1].toUpperCase() + "§f não existe!");
			return;
		}

		Group group = grupo;
		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer()) {
			Member battleSender = CommonGeneral.getInstance().getMemberManager()
					.getMember(cmdArgs.getPlayer().getUniqueId());
			playerGroup = battleSender.getServerGroup();
		} else {
			playerGroup = Group.DONO;
		}

		if (group != Group.TORNEIO && group.ordinal() < Group.YOUTUBER.ordinal()
				&& group.ordinal() >= Group.LIGHT.ordinal()) {
			sender.sendMessage(" §e* §fO grupo §a" + group.name() + "§f pode ser setado, somente, temporariamente.");
			return;
		}

		if (cmdArgs.isPlayer()) {
			switch (playerGroup) {
			case GERENTE: {
				if (group.ordinal() >= Group.MOD.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §5§lMOD§f ou inferior!");
					return;
				}
				break;
			}
			case ADMIN: {
				if (group.ordinal() >= Group.MODPLUS.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §5§lMOD+§f ou inferior!");
					return;
				}
				break;
			}
			case DIRETOR: {
				if (group.ordinal() >= Group.ADMIN.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §c§lADMIN§f ou inferior!");
					return;
				}
				break;
			}
			case DONO: {
				if (group.ordinal() > Group.DONO.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §4§lDIRETOR§f ou inferior!");
					sender.sendMessage(" §c* §fSó o console consegue manejar §4§lDONO§f!");
					return;
				}
				break;
			}
			default:
				break;
			}
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

		if (cmdArgs.isPlayer())
			if (playerGroup.ordinal() < player.getGroup().ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		Group actualGroup = player.getGroup();

		if (actualGroup == group) {
			sender.sendMessage(" §c* §fO jogador §a" + player.getPlayerName() + "§f já está nesse grupo!");
			return;
		}

		player.setGroup(group);
		sender.sendMessage(" §a* §fVocê alterou o grupo do §a" + player.getPlayerName() + "("
				+ player.getUniqueId().toString().replace("-", "") + ")" + "§f para §a" + group.name() + "§f!");
	}

	@Command(name = "permission", usage = "/<command> <player> <group>", groupToUse = Group.GERENTE, aliases = {
			"setargrupo" }, runAsync = true)
	public void addpermissionCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 2) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel()
					+ " <playerName> <add/remove> <permission>§f para adicionar ou remover um grupo.");
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

		if (args[1].equalsIgnoreCase("add")) {
			player.addPermission(args[2]);
			sender.sendMessage("§aVocê adicionou a permissão " + args[2] + " de " + player.getName());
		} else if (args[1].equalsIgnoreCase("remove")) {
			player.removePermission(args[2]);
			sender.sendMessage("§aVocê removeu a permissão " + args[2] + " de " + player.getName());
		}
	}

	@Command(name = "addmedal", groupToUse = Group.GERENTE, runAsync = true)
	public void addmedalCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		String[] args = cmdArgs.getArgs();
		CommandSender sender = cmdArgs.getSender();

		if (args.length == 0) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <player> <medalha>§f para dar medalha para alguém!");

			TextComponent textComponent = new MessageBuilder(" §e* §fMedalhas disponíveis: ").create();

			for (TextComponent txt : Arrays
					.asList(Medal.values()).stream().filter(
							medal -> medal != Medal.NONE)
					.map(medal -> new MessageBuilder(medal.getChatColor() + medal.getMedalName() + ", ")
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("" + medal.getChatColor() + medal.getMedalIcon()).create()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal " + medal.name()))
							.create())
					.collect(Collectors.toList())) {
				textComponent.addExtra(txt);
			}

			sender.sendMessage(textComponent);
			return;
		}

		Medal medal = Medal.getMedalByName(args[1]);

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

		if (player.getMedalList().contains(medal))
			sender.sendMessage("§cO jogador " + player.getPlayerName() + " já tem esta medalha!");
		else {
			player.addMedal(medal);
			player.sendMessage("§aVocê ganhou a medalha " + medal.getMedalName() + "!");
			sender.sendMessage("§aVocê deu a medalha " + medal.getMedalName() + " para o " + player.getName() + "!");
		}
	}

}
