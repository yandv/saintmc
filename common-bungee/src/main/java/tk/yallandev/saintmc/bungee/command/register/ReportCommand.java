package tk.yallandev.saintmc.bungee.command.register;

import java.awt.Color;
import java.util.Arrays;
import java.util.UUID;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.discord.utils.MessageUtils;

public class ReportCommand implements CommandClass {

	@Command(name = "report", aliases = { "reportar", "rp" }, runAsync = true)
	public void report(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (args.length >= 1) {
			if (Arrays.asList("on", "off").contains(args[0].toLowerCase())
					&& Member.hasGroupPermission(sender.getUniqueId(), Group.YOUTUBERPLUS)) {
				Member member = args.length == 1
						? CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId())
						: CommonGeneral.getInstance().getMemberManager().getMember(args[1]);

				if (member == null) {
					return;
				}

				if (args[0].equalsIgnoreCase("on")) {

					if (member.getAccountConfiguration().isReportEnabled()) {
						member.sendMessage("§cOs reports já estão ativados!");
					} else {
						member.getAccountConfiguration().setReportEnabled(true);
						member.sendMessage("§aVocê agora vê os reports!");
					}

					return;
				} else if (args[0].equalsIgnoreCase("off")) {

					if (!member.getAccountConfiguration().isReportEnabled()) {
						member.sendMessage("§cOs reports já estão desativados!");
					} else {
						member.getAccountConfiguration().setReportEnabled(false);
						member.sendMessage("§cVocê agora não vê mais os reports!");
					}

					return;
				}
			}
		}

		if (args.length <= 1) {
			sender.sendMessage(" §e* §fUse §a/report <player> <report>§f para reportar um jogador!");
			return;
		}

		if (player.isOnCooldown("report-command")) {
			sender.sendMessage(" §c* §fVocê precisa esperar §e"
					+ DateUtils.getTime(player.getCooldown("report-command")) + "§f para reportar novamente!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member m = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (m == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				m = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		if (sender.getUniqueId().equals(m.getUniqueId())) {
			sender.sendMessage(" §c* §fVocê não pode se reportar!");
			return;
		}

		Member target = m;

		if (!target.isOnline()) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Report rp = CommonGeneral.getInstance().getReportManager().getReport(uuid);

		if (rp == null) {
			rp = new Report(target.getUniqueId(), target.getPlayerName());
			CommonGeneral.getInstance().getReportManager().loadReport(rp);
			CommonGeneral.getInstance().getReportData().saveReport(rp);
		}

		final Report report = rp;

		StringBuilder builder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			String espaco = " ";

			if (i >= args.length - 1)
				espaco = "";

			builder.append(args[i] + espaco);
		}

		if (report.addReport(sender.getUniqueId(), cmdArgs.getPlayer().getName(),
				CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId()).getReputation(),
				builder.toString())) {
			sender.sendMessage(" §a* §fVocê reportou o jogador §a" + target.getPlayerName() + "§f por §a"
					+ builder.toString().trim() + "§f!");

			TextComponent text = new TextComponent(TextComponent.fromLegacyText("§a(Clique para se conectar)"));

			text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + report.getPlayerName()));
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("§aClique para se teletransportar!").create()));

			CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> member.hasGroupPermission(Group.HELPER)
							&& member.getAccountConfiguration().isReportEnabled())
					.forEach(member -> {
						member.sendMessage("§c§lREPORT");
						member.sendMessage(" ");
						member.sendMessage("§fSuspeito: §7" + report.getPlayerName());
						member.sendMessage("§fReportado por: §7" + cmdArgs.getPlayer().getName());
						member.sendMessage("§fMotivo: §7" + builder.toString().trim());
						member.sendMessage("§fServidor: §a" + target.getServerId());
						member.sendMessage(" ");

						member.sendMessage(text);
					});

			MessageUtils.sendMessage(702705594997407787l,
					new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.RED).setAuthor("Report")
							.addField("Suspeito: ", report.getPlayerName(), true)
							.addField("Reportado por: ", cmdArgs.getPlayer().getName(), true)
							.addField("Motivo: ", builder.toString().trim(), false).build()).build());
			player.setCooldown("report-command", System.currentTimeMillis() + 120000l);
		} else {
			sender.sendMessage(" §a* §fVocê reportou o jogador §a" + target.getPlayerName() + "§f por §a"
					+ builder.toString().trim() + "§f!");
		}
	}

}
