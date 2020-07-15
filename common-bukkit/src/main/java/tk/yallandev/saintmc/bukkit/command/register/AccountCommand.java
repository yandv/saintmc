
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
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.menu.account.AccountInventory;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.clan.enums.ClanDisplayType;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.medals.Medal;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class AccountCommand implements CommandClass {

	@Command(name = "account", aliases = { "acc", "info" }, runAsync = true)
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
				sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
				return;
			}

			player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

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
		}

		new AccountInventory(sender, player);
	}

	@Command(name = "medal", aliases = { "medals", "medalha", "medalhas" })
	public void medalCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		String[] args = cmdArgs.getArgs();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		if (args.length == 0) {
			member.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <medalha:remove>§f para mudar de medalha!");

			TextComponent textComponent = new MessageBuilder(" §e* §fMedalhas disponíveis: ").create();

			for (int x = 0; x < member.getMedalList().size(); x++) {
				Medal medal = member.getMedalList().get(x);

				if (medal == Medal.NONE)
					continue;

				textComponent
						.addExtra(new MessageBuilder(medal.getChatColor() + medal.getMedalName())
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("" + medal.getChatColor() + medal.getMedalIcon())
												.create()))
								.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal " + medal.name()))
								.create());

				if (x + 1 != member.getMedalList().size()) {
					textComponent.addExtra("§f, ");
				}
			}

			member.sendMessage(textComponent);
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

	@Command(name = "addmedal", groupToUse = Group.GERENTE)
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
			sender.sendMessage("§aVocê deu a medalha ");
		}
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
			if (player.getLeague() == league) {
				TextComponent text = new TextComponent(league.getColor() + league.getSymbol() + " " + league.name());

				text.setHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aSeu rank!")));

				player.sendMessage(text);
			} else {
				player.sendMessage(league.getColor() + league.getSymbol() + " " + league.name());
			}
		}

		player.sendMessage("");
		player.sendMessage("§a§l> §fSeu rank atual é " + player.getLeague().getColor() + player.getLeague().getSymbol()
				+ " " + player.getLeague().getName());
		player.sendMessage("§a§l> §fSeu xp §e" + player.getXp());

		if (player.getLeague() == League.CHALLENGER) {
			player.sendMessage("");
			player.sendMessage("§a§l> §fVocê está no maior rank do servidor");
			player.sendMessage("§a§l> §fContinue ganhando XP para ficar no topo do ranking");
		} else {
			player.sendMessage("");
			player.sendMessage("§a§l> §fPróximo rank §e" + player.getLeague().getNextLeague().getColor()
					+ player.getLeague().getNextLeague().getSymbol() + " "
					+ player.getLeague().getNextLeague().getName());
			player.sendMessage(
					"§a§l> §fXP necessário para o próximo rank §e" + (player.getLeague().getMaxXp() - player.getXp()));
		}
	}

	@Command(name = "youtuber")
	public void youtuberCommand(BukkitCommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage(new BaseComponent[] {
				new MessageBuilder("§bPara receber tag por um vídeo que você fez no servidor clique nesta mensagem!")
						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.YOUTUBER_FORM))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new MessageBuilder(CommonConst.YOUTUBER_FORM).create() }))
						.create() });
	}

	@Command(name = "aplicar")
	public void aplicarCommand(BukkitCommandArgs cmdArgs) {
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

	@Command(name = "site", aliases = { "website", "discord" })
	public void siteCommand(BukkitCommandArgs cmdArgs) {
		cmdArgs.getSender()
				.sendMessage(
						new BaseComponent[] { new MessageBuilder("§aClique aqui para acessar o nosso site!")
								.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.WEBSITE))
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new BaseComponent[] { new MessageBuilder(CommonConst.WEBSITE).create() }))
								.create() });
		cmdArgs.getSender()
				.sendMessage(
						new BaseComponent[] { new MessageBuilder("§bClique aqui para entrar em nosso discord!")
								.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.DISCORD))
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new BaseComponent[] { new MessageBuilder(CommonConst.DISCORD).create() }))
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
