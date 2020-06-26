package tk.yallandev.saintmc.discord.command.register;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.discord.command.DiscordCommandArgs;
import tk.yallandev.saintmc.discord.command.DiscordCommandSender;

public class SayCommand implements CommandClass {

	@Command(name = "say", runAsync = true)
	public void sayCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;

		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage("Use /" + cmdArgs.getLabel() + " <textChannel> <message> para enviar uma mensagem");
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

		textChannel.sendMessage(
				new EmbedBuilder().setColor(Color.YELLOW).appendDescription(stringBuilder.toString().trim()).build())
				.complete();
	}

}
