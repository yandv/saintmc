package tk.yallandev.saintmc.discord.command;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

@Getter
public class DiscordCommandArgs {

	private final DiscordCommandSender sender;
	private final String label;
	private final String[] args;
	private final MessageChannel textChannel;
	private final Guild guild;

	protected DiscordCommandArgs(DiscordCommandSender sender, String label, String[] args, int subCommand, MessageChannel textChannel, Guild guild) {
		String[] modArgs = new String[args.length - subCommand];
		System.arraycopy(args, subCommand, modArgs, 0, args.length - subCommand);

		StringBuilder buffer = new StringBuilder();
		buffer.append(label);

		for (int x = 0; x < subCommand; x++) {
			buffer.append(".").append(args[x]);
		}

		String cmdLabel = buffer.toString();
		this.sender = sender;
		this.label = cmdLabel;
		this.args = modArgs;
		this.textChannel = textChannel;
		this.guild = guild;
	}

	public boolean isPlayer() {
		return !((DiscordCommandSender) sender).getUser().isBot();
	}

}
