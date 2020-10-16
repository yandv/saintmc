package tk.yallandev.saintmc.discord.command.register;

import java.awt.Color;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.discord.command.DiscordCommandArgs;
import tk.yallandev.saintmc.discord.command.DiscordCommandSender;
import tk.yallandev.saintmc.discord.utils.MessageUtils;

public class SayCommand implements CommandClass {

	@Command(name = "say", runAsync = true)
	public void sayCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;

		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage("Use " + cmdArgs.getLabel() + " <textChannel> <message> para enviar uma mensagem");
			return;
		}

		TextChannel textChannel = cmdArgs.getGuild().getTextChannelsByName(args[0], true).stream().findFirst()
				.orElse(null);

		if (textChannel == null) {
			try {
				textChannel = cmdArgs.getGuild().getTextChannelById(Long.valueOf(args[0]));
			} catch (Exception ex) {
			}

			if (textChannel == null) {
				sender.sendMessage("O canal de texto " + args[0] + " não existe!");
				return;
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			stringBuilder.append(args[i]).append(" ");

		String avatarUrl = "";
		String userName = cmdArgs.getSender().getAsMember().getEffectiveName();

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

		if (member == null) {
			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
					.loadMember(sender.getUser().getIdLong());

			if (memberModel != null) {
				member = new MemberVoid(memberModel);
			}

			if (member == null) {
				avatarUrl = cmdArgs.getSender().getAsMember().getUser().getAvatarUrl();
			} else {
				avatarUrl = "https://mc-heads.net/avatar/" + member.getPlayerName();
				userName = member.getPlayerName();
			}
		}

		RestAction<Message> restAction = textChannel
				.sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setFooter("Enviado por " + userName, avatarUrl)
						.setTimestamp(Instant.now()).appendDescription(stringBuilder.toString().trim()).build());

		if (textChannel.isNews())
			restAction.flatMap(Message::crosspost).complete();
		else
			restAction.complete();
	}

	@Command(name = "anuncio", runAsync = true)
	public void anuncioCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;

		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage("Use " + cmdArgs.getLabel() + " <textChannel> <message> para enviar uma mensagem");
			return;
		}

		TextChannel textChannel = cmdArgs.getGuild().getTextChannelsByName(args[0], true).stream().findFirst()
				.orElse(null);

		if (textChannel == null) {
			try {
				textChannel = cmdArgs.getGuild().getTextChannelById(Long.valueOf(args[0]));
			} catch (Exception ex) {
			}

			if (textChannel == null) {
				sender.sendMessage("O canal de texto " + args[0] + " não existe!");
				return;
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			stringBuilder.append(args[i]).append(" ");

		String avatarUrl = cmdArgs.getSender().getAsMember().getUser().getAvatarUrl();
		String userName = cmdArgs.getSender().getAsMember().getEffectiveName();

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

		if (member == null) {
			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
					.loadMember(sender.getUser().getIdLong());

			if (memberModel != null) {
				member = new MemberVoid(memberModel);
			}

			if (member != null) {
				avatarUrl = "https://mc-heads.net/avatar/" + member.getPlayerName();
				userName = member.getPlayerName();
			}
		}

		RestAction<Message> restAction = textChannel.sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setAuthor(
				"Anuncio - SaintMC", "https://saintmc.net/",
				"https://images-ext-1.discordapp.net/external/U5hoGi1CnPwEv32Y7zs6r7xV2K0R_maVFO-J5-eTsKY/%3Fv%3D1/https/cdn.discordapp.com/emojis/506833797367595037.gif")
				.setThumbnail("https://cdn.discordapp.com/attachments/700661469032874014/744615850111270992/logo.png")
				.setFooter("Atenciosamente, " + userName, avatarUrl).setTimestamp(Instant.now())
				.appendDescription(stringBuilder.toString().trim()).build());

		if (textChannel.isNews())
			restAction.flatMap(Message::crosspost).complete();
		else
			restAction.complete();
		MessageUtils.sendMessage(textChannel, "@everyone", 3);
	}

	@Command(name = "evento", runAsync = true)
	public void eventoCommand(DiscordCommandArgs cmdArgs) {
		DiscordCommandSender sender = cmdArgs.getSender();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

		if (member == null) {
			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
					.loadMember(sender.getUser().getIdLong());

			if (memberModel != null) {
				member = new MemberVoid(memberModel);
			}

			if (member == null) {
				sender.sendMessage("Você precisa ter a sua conta vinculada com o discord para executar esse comando!");
				return;
			}
		}

		if (!member.hasGroupPermission(Group.MODPLUS)) {
			sender.sendMessage("Você não tem permissão para executar esse comando!");
			return;
		}

		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage("Use " + cmdArgs.getLabel() + " <textChannel> <message> para enviar uma mensagem");
			return;
		}

		TextChannel textChannel = cmdArgs.getGuild().getTextChannelsByName(args[0], true).stream().findFirst()
				.orElse(null);

		if (textChannel == null) {
			try {
				textChannel = cmdArgs.getGuild().getTextChannelById(Long.valueOf(args[0]));
			} catch (Exception ex) {
			}

			if (textChannel == null) {
				sender.sendMessage("O canal de texto " + args[0] + " não existe!");
				return;
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 1; i < args.length; i++)
			stringBuilder.append(args[i]).append(" ");

		String avatarUrl = "https://mc-heads.net/avatar/" + member.getPlayerName();
		String userName = member.getPlayerName();

		Role role = cmdArgs.getGuild().getRoleById(708384460776931329l);

		RestAction<Message> restAction = textChannel.sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setAuthor(
				"Evento - SaintMC", "https://saintmc.net/",
				"https://images-ext-1.discordapp.net/external/U5hoGi1CnPwEv32Y7zs6r7xV2K0R_maVFO-J5-eTsKY/%3Fv%3D1/https/cdn.discordapp.com/emojis/506833797367595037.gif")
				.setThumbnail("https://cdn.discordapp.com/attachments/700661469032874014/744615850111270992/logo.png")
				.setFooter("Atenciosamente, " + userName, avatarUrl).setTimestamp(Instant.now())
				.appendDescription(stringBuilder.toString().trim()).build()).mention(role);

		if (textChannel.isNews())
			restAction.flatMap(Message::crosspost).complete();
		else
			restAction.complete();

		if (role == null)
			MessageUtils.sendMessage(textChannel, "<@&708384460776931329>", 3);
	}

}
