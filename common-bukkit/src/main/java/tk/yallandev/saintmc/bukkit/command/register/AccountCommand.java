
package tk.yallandev.saintmc.bukkit.command.register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.menu.account.AccountInventory;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.clan.enums.ClanDisplayType;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class AccountCommand implements CommandClass {

	@Command(name = "account", aliases = { "acc", "info", "perfil", "profile" }, runAsync = true)
	public void accountCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		Member player;

		if (args.length == 0) {
			player = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());
		} else {
			UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

			if (uuid == null) {
				sender.sendMessage("§cO jogador " + args[0] + " não existe!");
				return;
			}

			player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

			if (player == null) {
				try {
					MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

					if (loaded == null) {
						sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor!");
						return;
					}

					player = new MemberVoid(loaded);
				} catch (Exception e) {
					e.printStackTrace();
					sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + "!");
					return;
				}
			}

			if (!player.getUniqueId().equals(sender.getUniqueId()))
				if (!Member.hasGroupPermission(sender.getUniqueId(), Group.TRIAL)) {
					sender.sendMessage("§cVocê não pode ver o perfil de outros jogadores!");
					return;
				}
		}

		new AccountInventory(sender, player);
	}

	@Command(name = "tag", runAsync = true)
	public void tagCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		if (!BukkitMain.getInstance().isTagControl()) {
			cmdArgs.getSender().sendMessage("§cO comando não está ativado nesse servidor!");
			return;
		}

		BukkitMember player = (BukkitMember) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			TextComponent message = new TextComponent("§aSuas tags: ");

			int max = player.getTags().size() * 2;
			int i = max - 1;

			for (Tag t : player.getTags()) {
				if (i < max - 1) {
					message.addExtra(new TextComponent("§f, "));
					i -= 1;
				}

				message.addExtra(new MessageBuilder(t == Tag.MEMBRO ? "§7§lMEMBRO" : t.getPrefix())
						.setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
								new TextComponent[] { new TextComponent("§fExemplo: " + t.getPrefix()
										+ player.getPlayerName() + "\n\n§aClique para selecionar!") }))
						.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
								"/tag " + t.getName()))
						.create());
				i -= 1;
			}

			player.sendMessage(message);
			return;
		}

		if (args[0].equalsIgnoreCase("chroma")) {
			if (player.hasGroupPermission(Group.ADMIN) || player.hasPermission("tag.chroma")) {
				player.setChroma(!player.isChroma());
				player.setTag(player.getTag());
				player.sendMessage(
						player.isChroma() ? "§aO modo chroma foi ativado!" : "§cO modo chroma foi desativado!");
				return;
			}
		}

		if (args[0].equalsIgnoreCase("default") || args[0].equalsIgnoreCase("normal")) {
			if (player.setTag(player.getDefaultTag()))
				player.sendMessage("§aVocê voltou para sua tag padrão!");
			return;
		}

		Tag tag = Tag.getByName(args[0]);

		if (tag == null) {
			player.sendMessage("§cA tag " + args[0] + " não existe!");
			return;
		}

		if (player.hasTag(tag)) {
			if (!player.getTag().equals(tag)) {
				if (player.setTag(tag)) {
					player.sendMessage("§aVocê alterou sua tag para "
							+ (tag == Tag.MEMBRO ? "§7§lMEMBRO" : tag.getPrefix()) + "§a.");
				}
			} else {
				player.sendMessage("§cVocê já está usando essa tag!");
			}
		} else {
			player.sendMessage("§cVocê não tem permissão para usar essa tag!");
		}
	}

	@Command(name = "medal", aliases = { "medals", "medalha", "medalhas" })
	public void medalCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		String[] args = cmdArgs.getArgs();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		if (args.length == 0) {
			member.sendMessage("§cUso /" + cmdArgs.getLabel() + " <medalha:remove> para mudar de medalha!");

			if (member.getMedalList().isEmpty()) {
				member.sendMessage("§cVocê não possui nenhuma medalha!");
			} else {
				TextComponent textComponent = new MessageBuilder(" §e* §fMedalhas disponíveis: ").create();

				for (int x = 0; x < member.getMedalList().size(); x++) {
					Medal medal = member.getMedalList().get(x);

					if (medal == null || medal == Medal.NONE)
						continue;

					textComponent
							.addExtra(
									new MessageBuilder(medal.getChatColor() + medal.getMedalName())
											.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new ComponentBuilder(
															"" + medal.getChatColor() + medal.getMedalIcon()).create()))
											.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/medal " + medal.name()))
											.create());

					if (x + 1 != member.getMedalList().size()) {
						textComponent.addExtra("§f, ");
					}
				}

				member.sendMessage(textComponent);
			}
			return;
		}

		Medal medal = Medal.getMedalByName(args[0]);

		if (medal == null) {
			if (args[0].equalsIgnoreCase("remove")) {
				member.sendMessage("§aSua medalha foi removida!");
				member.setMedal(Medal.NONE);
				member.setTag(member.getTag());
				return;
			}

			member.sendMessage("§cA medalha " + args[0] + " não existe!");
			return;
		}

		if (member.getMedalList().contains(medal)) {
			member.sendMessage("§aSua medalha foi alterada para " + medal.getMedalName() + "!");
			member.setMedal(medal);
			member.setTag(member.getTag());
		} else
			member.sendMessage("§cVocê não possui essa medalha!");
	}

	@Command(name = "clandisplaytag", runAsync = true)
	public void clandisplaytagCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		String[] args = cmdArgs.getArgs();
		BukkitMember player = (BukkitMember) cmdArgs.getSender();

		if (args.length == 0) {
			player.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <"
					+ Joiner.on(':')
							.join(Arrays.asList(ClanDisplayType.values()).stream().map(cl -> cl.name().toLowerCase())
									.collect(Collectors.toList()))
					+ ">§f para mudar o estado da sua tag do clan no tab!");
			return;
		}

		ClanDisplayType clanDisplayType = null;

		try {
			clanDisplayType = ClanDisplayType.valueOf(args[0].toUpperCase());
		} catch (Exception ex) {
			player.sendMessage("§cO estado " + args[0] + " não foi encontrado. Tente: "
					+ Joiner.on(',').join(Arrays.asList(ClanDisplayType.values()).stream()
							.map(cl -> cl.name().toLowerCase()).collect(Collectors.toList())));
			return;
		}

		player.getAccountConfiguration().setClanDisplayType(clanDisplayType);
		player.sendMessage("§aO estado da sua tag de clan foi alterado para "
				+ NameUtils.formatString(clanDisplayType.name()) + "!");
		player.setTag(player.getTag());
	}

	@Command(name = "rank", aliases = { "ranks", "liga", "ligas" })
	public void rankCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		List<League> leagues = Arrays.asList(League.values());
		Collections.reverse(leagues);

		for (League league : leagues) {
			MessageBuilder messageBuilder = new MessageBuilder(
					league.getColor() + league.getSymbol() + " " + league.getName());

			if (player.getLeague() == league)
				messageBuilder.setHoverEvent(HoverEvent.Action.SHOW_TEXT, "§aO seu rank é esse!");

			player.sendMessage(messageBuilder.create());
		}

		player.sendMessage("");
		player.sendMessage("§eSeu rank atual é " + player.getLeague().getColor() + player.getLeague().getSymbol() + " "
				+ player.getLeague().getName());
		player.sendMessage("§eSeu xp §e" + player.getXp());

		if (player.getLeague() == League.CLOUTH) {
			player.sendMessage("");
			player.sendMessage("§aVocê está no maior rank do servidor");
			player.sendMessage("aContinue ganhando XP para ficar no topo do ranking");
		} else {
			player.sendMessage("");
			player.sendMessage("§aPróximo rank §e" + player.getLeague().getNextLeague().getColor()
					+ player.getLeague().getNextLeague().getSymbol() + " "
					+ player.getLeague().getNextLeague().getName());
			player.sendMessage(
					"§aXP necessário para o próximo rank §e" + (player.getLeague().getMaxXp() - player.getXp()));
		}
	}

	@Command(name = "money", aliases = { "coins" }, runAsync = true)
	public void moneyCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member member = (Member) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			member.sendMessage("§aVocê possui " + member.getMoney() + " coins!");
			return;
		}

		switch (args[0].toLowerCase()) {
		case "doar":
		case "give":

			if (args.length <= 2) {
				member.sendMessage("§eUse §a/" + cmdArgs.getLabel()
						+ " give <player> <money> para enviar money para algum jogador!");
			} else {
				UUID uuid = CommonGeneral.getInstance().getUuid(args[1]);

				if (uuid == null) {
					member.sendMessage("§cO jogador " + args[1] + " não existe!");
					return;
				}

				Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

				if (player == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

						if (loaded == null) {
							member.sendMessage("§cO jogador " + args[1] + " nunca entrou no servidor!");
							return;
						}

						player = new MemberVoid(loaded);
					} catch (Exception e) {
						e.printStackTrace();
						member.sendMessage("§cNão foi possível pegar as informações do jogador " + args[1] + "!");
						return;
					}
				}

				Integer money = null;

				try {
					money = Integer.valueOf(args[2]);
				} catch (NumberFormatException exception) {
					member.sendMessage("§cValor inválido!");
					return;
				}

				if (money <= 100) {
					member.sendMessage("§cVocê só pode enviar no minímo 100 coins!");
					return;
				}

				if (money > member.getMoney()) {
					member.sendMessage("§cVocê não possui " + money + " coins!");
					return;
				}

				player.addMoney(money);
				player.sendMessage("§aVocê recebeu " + money + " coins de " + member.getName() + "!");

				member.sendMessage("§aVocê deu " + money + " para o " + player.getName() + "!");
				member.removeMoney(money);
			}

			break;
		default:
			member.sendMessage("§aVocê possui " + member.getMoney() + " coins!");
			break;
		}
	}

	@Command(name = "youtuber")
	public void youtuberCommand(CommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage(new BaseComponent[] {
				new MessageBuilder("§bPara receber tag por um vídeo que você fez no servidor clique nesta mensagem!")
						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.YOUTUBER_FORM))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new MessageBuilder(CommonConst.YOUTUBER_FORM).create() }))
						.create() });
	}

	@Command(name = "aplicar")
	public void aplicarCommand(CommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage(new BaseComponent[] {
				new MessageBuilder("§dPara entrar na equipe de Trial, faça o formulário clicando nessa mensagem!")
						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.TRIAL_FORM))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new MessageBuilder(CommonConst.TRIAL_FORM).create() }))
						.create() });
		cmdArgs.getSender().sendMessage(new BaseComponent[] {
				new MessageBuilder("§9Para entrar na equipe de Helper, faça o formulário clicando nessa mensagem!")
						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.HELPER_FORM))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new MessageBuilder(CommonConst.HELPER_FORM).create() }))
						.create() });
	}

	@Command(name = "reply", usage = "/<command> <message>", aliases = { "r" })
	public void replyCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		BukkitMember sender = (BukkitMember) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fVocê deve utilizar §a/" + cmdArgs.getLabel()
					+ " <mensagem>§f, para enviar uma mensagem privada!");
			return;
		}

		if (sender.getLastTell() == null) {
			sender.sendMessage(" §c* §fVocê não tem tell para responder!");
			return;
		}

		Mute mute = sender.getPunishmentHistory().getActiveMute();

		if (mute != null) {
			sender.sendMessage("§4§l> §fVocê está mutado "
					+ (mute.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor!"
					+ (mute.isPermanent() ? "" : "\n §4§l> §fExpira em §e" + DateUtils.getTime(mute.getMuteExpire())));
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getLastTell().getUniqueId());

		if (member == null) {
			sender.sendMessage(" §c* §fO jogador §a" + sender.getLastTell().getPlayerName() + "§f está offline!");
			sender.setLastTell(null);
			return;
		}

		String message = "";

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");

		message = sb.toString();

		if (!member.getAccountConfiguration().isTellEnabled())
			sender.setLastTell(null);

		sender.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (member.isUsingFake() ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
		member.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (member.isUsingFake() ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
	}

	@Command(name = "givexp", usage = "/<command> <message>", groupToUse = Group.ADMIN)
	public void givexpCommand(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§ebobo");
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(args[0]);

		if (member == null) {
			sender.sendMessage("§cO jogador não está online!");
			return;
		}

		try {
			member.addXp(Integer.valueOf(args[1]));
			sender.sendMessage(
					"§aO jogador " + member.getPlayerName() + " recebeu " + Integer.valueOf(args[1]) + "xp!");
		} catch (Exception ex) {
			sender.sendMessage("§cFormato de xp inválido!");
		}
	}

	@Command(name = "removexp", usage = "/<command> <message>", groupToUse = Group.ADMIN)
	public void removexpCommand(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§ebobo");
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(args[0]);

		if (member == null) {
			sender.sendMessage("§cO jogador não está online!");
			return;
		}

		try {
			member.removeXp(Integer.valueOf(args[1]));
			sender.sendMessage(
					"§cO jogador " + member.getPlayerName() + " recebeu -" + Integer.valueOf(args[1]) + "xp!");
		} catch (Exception ex) {
			sender.sendMessage("§cFormato de xp inválido!");
		}
	}

	@Command(name = "tell", usage = "/<command> <message>", aliases = { "msg" })
	public void tellCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		BukkitMember sender = (BukkitMember) cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		Mute mute = sender.getPunishmentHistory().getActiveMute();

		if (mute != null) {
			sender.sendMessage("§4§l> §fVocê está mutado "
					+ (mute.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor!"
					+ (mute.isPermanent() ? "" : "\n §4§l> §fExpira em §e" + DateUtils.getTime(mute.getMuteExpire())));
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(" §e* §fVocê deve utilizar §a/" + cmdArgs.getLabel()
					+ " <mensagem>§f, para enviar uma mensagem privada!");
			return;
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("on")) {
				Member member = CommonGeneral.getInstance().getMemberManager()
						.getMember(cmdArgs.getPlayer().getUniqueId());

				if (member.getAccountConfiguration().isTellEnabled()) {
					sender.sendMessage(" §c* §fSeu tell já está §aativado§f!");
					return;
				}

				member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
				sender.sendMessage(" §a* §fVocê §aativou§f o seu tell!");
			} else if (args[0].equalsIgnoreCase("off")) {
				Member member = CommonGeneral.getInstance().getMemberManager()
						.getMember(cmdArgs.getPlayer().getUniqueId());

				if (!member.getAccountConfiguration().isTellEnabled()) {
					sender.sendMessage(" §c* §fSeu tell já está §cdesativado§f!");
					return;
				}

				member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
				sender.sendMessage(" §a* §fVocê §cdesativou§f o seu tell!");
			} else {
				sender.sendMessage(" §e* §fVocê deve utilizar §a/" + cmdArgs.getLabel()
						+ " <mensagem>§f, para enviar uma mensagem privada!");
			}

			return;
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMemberByFake(args[0]);

		if (member == null) {
			member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(args[0]);

			if (member != null && member.isUsingFake()
					&& !Member.hasGroupPermission(sender.getUniqueId(), Group.TRIAL)) {
				sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f está offline!");
				return;
			}
		}

		if (member == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f está offline!");
			return;
		}

		if (!member.getAccountConfiguration().isTellEnabled())
			if (!sender.hasGroupPermission(Group.TRIAL)) {
				sender.sendMessage("§cO tell desse jogador está desativado!");
				return;
			}

		boolean fake = false;

		if (member.isUsingFake())
			fake = true;

		String message = "";

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			sb.append(args[i]).append(" ");

		message = sb.toString();

		sender.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (fake ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
		member.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e"
				+ (fake ? member.getFakeName() : member.getPlayerName()) + "§7] §f" + message);
		member.setLastTell(Profile.fromMember(sender));
	}

}
