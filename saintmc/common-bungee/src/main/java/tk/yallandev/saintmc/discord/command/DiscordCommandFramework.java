package tk.yallandev.saintmc.discord.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;
import tk.yallandev.saintmc.discord.utils.MessageUtils;

public class DiscordCommandFramework implements CommandFramework {

	private final Map<String, Entry<Method, Object>> commandMap = new HashMap<String, Entry<Method, Object>>();
	private DiscordMain manager;

	public DiscordCommandFramework(DiscordMain manager) {
		this.manager = manager;
		this.manager.getJda().addEventListener(new CommandRegister());
	}

	public boolean handleCommand(DiscordCommandSender sender, String label, String[] args, MessageChannel textChannel,
			Guild guild) {

		for (int i = args.length; i >= 0; i--) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(label.toLowerCase());

			for (int x = 0; x < i; x++) {
				buffer.append(".").append(args[x].toLowerCase());
			}

			String cmdLabel = buffer.toString();
			String commandPrefix = "/";

			if (commandMap.containsKey(cmdLabel.replace(commandPrefix, ""))) {
				Entry<Method, Object> entry = commandMap.get(cmdLabel.replace(commandPrefix, ""));
				Command command = entry.getKey().getAnnotation(Command.class);

				GuildConfiguration config = DiscordMain.getInstance().getGuildManager().getGuild(guild.getIdLong());

				if (config.getChatMap().containsKey("command")) {
					if (config.getChatMap().get("command") != textChannel.getIdLong()) {
						MessageUtils.sendMessage(textChannel,
								"Você não só pode executar comandos no canal ``" + textChannel.getName() + "``.", 5);
						return true;
					}
				}

				if (command.runAsync()) {
					BungeeMain.getInstance().getProxy().getScheduler().runAsync(BungeeMain.getInstance(),
							new Runnable() {

								@Override
								public void run() {
									try {
										entry.getKey().invoke(entry.getValue(), new DiscordCommandArgs(sender, label,
												args, cmdLabel.split("\\.").length - 1, textChannel, guild));
									} catch (IllegalArgumentException | InvocationTargetException
											| IllegalAccessException e) {
										e.printStackTrace();
									}
								}

							});
				} else {
					try {
						entry.getKey().invoke(entry.getValue(), new DiscordCommandArgs(sender, label, args,
								cmdLabel.split("\\.").length - 1, textChannel, guild));
					} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}

				return true;
			}
		}

		defaultCommand(new DiscordCommandArgs(sender, label, args, 0, textChannel, guild));
		return true;
	}

	public void registerCommands(CommandClass cls) {
		for (Method m : cls.getClass().getMethods()) {
			if (m.getAnnotation(Command.class) != null) {
				Command command = m.getAnnotation(Command.class);

				if (m.getParameterTypes().length > 1 || m.getParameterTypes().length <= 0
						|| !DiscordCommandArgs.class.isAssignableFrom(m.getParameterTypes()[0])) {
					System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
					continue;
				}

				registerCommand(command, command.name(), m, cls);

				for (String alias : command.aliases()) {
					registerCommand(command, alias, m, cls);
				}
			}
		}
	}

	private void registerCommand(Command command, String label, Method m, Object obj) {
		Entry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, obj);
		commandMap.put(label.toLowerCase(), entry);
		System.out.println("The command " + label + " (" + command.name() + ") has been registred!");
	}

	private void defaultCommand(DiscordCommandArgs args) {
		// args.getSender().reply("o comando " + args.getLabel() + " não existe!", 5);
	}

	class CommandRegister extends ListenerAdapter {

		@Override
		public void onMessageReceived(MessageReceivedEvent event) {
			if (manager.getJda().getSelfUser().getId().equals(event.getAuthor().getId()))
				return;

			String commandPrefix = "/";

			if (!event.getMessage().getContentDisplay().startsWith(commandPrefix))
				return;

			if (event.getChannelType() == ChannelType.PRIVATE)
				return;

			String[] txt = event.getMessage().getContentDisplay().trim().split(" ");
			String[] args = new String[txt.length - 1];

			for (int i = 1; i < txt.length; i++) {
				args[i - 1] = txt[i];
			}

			handleCommand(new DiscordCommandSender(event.getAuthor(), event.getTextChannel(), event.getGuild()), txt[0],
					args, event.getTextChannel(), event.getGuild());
			super.onMessageReceived(event);
		}
	}

	@Override
	public Class<?> getJarClass() {
		return DiscordMain.getInstance().getClass();
	}
}