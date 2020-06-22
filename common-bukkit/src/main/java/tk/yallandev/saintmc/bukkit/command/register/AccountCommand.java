
package tk.yallandev.saintmc.bukkit.command.register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.menu.account.AccountInventory;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

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

	@Command(name = "rank", aliases = { "ranks", "liga", "ligas" })
	public void rankCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		List<League> leagues = Arrays.asList(League.values());
		Collections.reverse(leagues);

		for (League league : leagues) {
			if (player.getLeague() == league) {
				TextComponent text = new TextComponent(league.getColor() + league.getSymbol() + " " + league.name());

				text.setHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aSeu rank!")));

				player.sendMessage(text);
			} else {
				p.sendMessage(league.getColor() + league.getSymbol() + " " + league.name());
			}
		}

		p.sendMessage("");
		p.sendMessage("§a§l> §fSeu rank atual é " + player.getLeague().getColor() + player.getLeague().getSymbol() + " "
				+ player.getLeague().getName());
		p.sendMessage("§a§l> §fSeu xp §e" + player.getXp());

		if (player.getLeague() == League.CHALLENGER) {
			p.sendMessage("");
			p.sendMessage("§a§l> §fVocê está no maior rank do servidor");
			p.sendMessage("§a§l> §fContinue ganhando XP para ficar no topo do ranking");
		} else {
			p.sendMessage("");
			p.sendMessage("§a§l> §fPróximo rank §e" + player.getLeague().getNextLeague().getColor()
					+ player.getLeague().getNextLeague().getSymbol() + " "
					+ player.getLeague().getNextLeague().getName());
			p.sendMessage(
					"§a§l> §fXP necessário para o próximo rank §e" + (player.getLeague().getMaxXp() - player.getXp()));
		}
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
										new BaseComponent[] { new MessageBuilder(CommonConst.WEBSITE).create() }))
								.create() });
	}

	@Command(name = "reply", usage = "/<command> <message>", aliases = { "r" })
	public void replyCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fVocê deve utilizar §a/" + cmdArgs.getLabel()
					+ " <mensagem>§f, para enviar uma mensagem privada!");
			return;
		}

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		if (player.getLastTell() == null) {
			sender.sendMessage(" §c* §fVocê não tem tell para responder!");
			return;
		}

		Mute mute = player.getPunishmentHistory().getActiveMute();

		if (mute != null) {
			player.sendMessage("§4§l> §fVocê está mutado "
					+ (mute.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor!"
					+ (mute.isPermanent() ? "" : "\n §4§l> §fExpira em §e" + DateUtils.getTime(mute.getMuteExpire())));
			return;
		}

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getLastTell());

		if (member == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f está offline!");
			return;
		}

		String message = "";

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");

		message = sb.toString();

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

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		Mute mute = player.getPunishmentHistory().getActiveMute();

		if (mute != null) {
			player.sendMessage("§4§l> §fVocê está mutado "
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
				sender.sendMessage(" §a* §fVocê §aativou fo seu tell!");
			} else if (args[0].equalsIgnoreCase("off")) {

				Member member = CommonGeneral.getInstance().getMemberManager()
						.getMember(cmdArgs.getPlayer().getUniqueId());

				if (!member.getAccountConfiguration().isTellEnabled()) {
					sender.sendMessage(" §c* §fSeu tell já está §cdesativado§f!");
					return;
				}

				member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
				sender.sendMessage(" §a* §fVocê §cdesativou fo seu tell!");
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
		member.setLastTell(sender.getUniqueId());
	}

}
