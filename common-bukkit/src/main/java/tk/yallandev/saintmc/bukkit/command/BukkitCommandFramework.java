package tk.yallandev.saintmc.bukkit.command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.timings.TimingsManager;
import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework;
import tk.yallandev.saintmc.common.permission.Group;

public class BukkitCommandFramework implements CommandFramework {

	public static final BukkitCommandFramework INSTANCE = new BukkitCommandFramework(BukkitMain.getInstance());

	private Plugin plugin;
	private final Map<String, Entry<Method, Object>> commandMap = new HashMap<String, Entry<Method, Object>>();
	private CommandMap map;

	private Map<String, org.bukkit.command.Command> knownCommands;

	/**
	 * 
	 * @param plugin
	 * @deprecated use the Instance
	 */

	public BukkitCommandFramework(Plugin plugin) {
		this.plugin = plugin;

		if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
			SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();

			try {
				Field field = SimplePluginManager.class.getDeclaredField("commandMap");
				field.setAccessible(true);
				map = (CommandMap) field.get(manager);
			} catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
				e.printStackTrace();
			}

			try {
				Field field = map.getClass().getDeclaredField("knownCommands");

				field.setAccessible(true);
				knownCommands = (HashMap<String, org.bukkit.command.Command>) field.get(map);
			} catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean handleCommand(CommandSender sender, String label, org.bukkit.command.Command cmd, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(label.toLowerCase());

			for (int x = 0; x < i; x++) {
				buffer.append(".").append(args[x].toLowerCase());
			}

			String cmdLabel = buffer.toString();

			if (commandMap.containsKey(cmdLabel)) {
				Entry<Method, Object> entry = commandMap.get(cmdLabel);
				Command command = entry.getKey().getAnnotation(Command.class);

				if (command.runAsync() && Bukkit.isPrimaryThread()) {
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								entry.getKey().invoke(entry.getValue(),
										new BukkitCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
							} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}.runTaskAsynchronously(plugin);
				} else {
					try {
						entry.getKey().invoke(entry.getValue(),
								new BukkitCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
					} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		}

		sender.sendMessage(" ");
		sender.sendMessage(" §c* §fO comando está inacessível no momento!");
		sender.sendMessage(" ");
		return true;
	}

	@Override
	public void registerCommands(CommandClass commandClass) {
		for (Method m : commandClass.getClass().getMethods()) {
			if (m.getAnnotation(Command.class) != null) {
				Command command = m.getAnnotation(Command.class);
				if (m.getParameterTypes().length > 1 || m.getParameterTypes().length <= 0
						|| !CommandArgs.class.isAssignableFrom(m.getParameterTypes()[0])) {
					System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
					continue;
				}
				registerCommand(command, command.name(), m, commandClass);

				for (String alias : command.aliases()) {
					registerCommand(command, alias, m, commandClass);
				}
			}
		}

		for (Method m : commandClass.getClass().getMethods()) {
			if (m.getAnnotation(Completer.class) != null) {
				Completer comp = m.getAnnotation(Completer.class);
				if (m.getParameterTypes().length > 1 || m.getParameterTypes().length == 0
						|| m.getParameterTypes()[0] != CommandArgs.class) {
					System.out.println(
							"Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
					continue;
				}

				if (m.getReturnType() != List.class) {
					System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
					continue;
				}

				registerCompleter(comp.name(), m, commandClass);

				for (String alias : comp.aliases()) {
					registerCompleter(alias, m, commandClass);
				}
			}
		}
	}

	public void registerHelp() {
		Set<HelpTopic> help = new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance());

		for (String s : commandMap.keySet()) {
			if (!s.contains(".")) {
				org.bukkit.command.Command cmd = map.getCommand(s);
				HelpTopic topic = new GenericCommandHelpTopic(cmd);
				help.add(topic);
			}
		}

		IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help,
				"Below is a list of all " + plugin.getName() + " commands:");
		Bukkit.getServer().getHelpMap().addTopic(topic);
	}

	private void registerCommand(Command command, String label, Method m, Object obj) {
		Entry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, obj);
		commandMap.put(label.toLowerCase(), entry);
		String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();

		if (map.getCommand(cmdLabel) == null) {
			org.bukkit.command.Command cmd = new BukkitCommand(command.name(), cmdLabel, plugin, command.groupToUse());
			knownCommands.put(cmdLabel, cmd);
		} else {

			/*
			 * Já que alguns comandos não carregam primeiramente aqui e eles ficam sem o
			 * grupo do servidor, mas ele é necessário!
			 */

			if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
				BukkitCommand bukkitCommand = (BukkitCommand) map.getCommand(cmdLabel);
				bukkitCommand.setGroup(command.groupToUse());
			}
		}

		if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
			map.getCommand(cmdLabel).setDescription(command.description());
		}

		if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
			map.getCommand(cmdLabel).setUsage(command.usage());
		}
	}

	private void registerCompleter(String label, Method m, Object obj) {
		String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();

		if (map.getCommand(cmdLabel) == null) {
			org.bukkit.command.Command command = new BukkitCommand(cmdLabel, cmdLabel, plugin, Group.MEMBRO);
			knownCommands.put(cmdLabel, command);
		}

		if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
			BukkitCommand command = (BukkitCommand) map.getCommand(cmdLabel);

			if (command.getCompleter() == null) {
				command.setCompleter(new BukkitCompleter());
			}

			command.getCompleter().addCompleter(label, m, obj);
		} else if (map.getCommand(cmdLabel) instanceof PluginCommand) {
			try {
				Object command = map.getCommand(cmdLabel);
				Field field = command.getClass().getDeclaredField("completer");
				field.setAccessible(true);
				if (field.get(command) == null) {
					BukkitCompleter completer = new BukkitCompleter();
					completer.addCompleter(label, m, obj);
					field.set(command, completer);
				} else if (field.get(command) instanceof BukkitCompleter) {
					BukkitCompleter completer = (BukkitCompleter) field.get(command);
					completer.addCompleter(label, m, obj);
				} else {
					System.out.println("Unable to register tab completer " + m.getName()
							+ ". A tab completer is already registered for that command!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public Class<?> getJarClass() {
		return plugin.getClass();
	}

	public class BukkitCommand extends org.bukkit.command.Command {

		private Plugin owningPlugin;
		private CommandExecutor executor;

		@Setter
		@Getter
		private BukkitCompleter completer;

		@Setter
		private Group group;

		public BukkitCommand(String fallbackPrefix, String label, Plugin owner, Group group) {
			super(label);
			this.executor = owner;
			this.owningPlugin = owner;
			this.usageMessage = "";
			this.group = group;
			this.timings = TimingsManager.getCommandTiming(fallbackPrefix, this);
		}

		@Override
		public boolean execute(CommandSender sender, String commandLabel, String[] args) {
			boolean success = false;

			if (!owningPlugin.isEnabled()) {
				return false;
			}

			if (!testPermission(sender)) {
				return true;
			}

			try {
				success = handleCommand(sender, commandLabel, this, args);
			} catch (Throwable ex) {
				throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin "
						+ owningPlugin.getDescription().getFullName(), ex);
			}

			if (!success && usageMessage.length() > 0) {
				for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
					sender.sendMessage(line);
				}
			}

			return success;
		}

		@Override
		public List<String> tabComplete(CommandSender sender, String alias, String[] args)
				throws CommandException, IllegalArgumentException {
			Validate.notNull(sender, "Sender cannot be null");
			Validate.notNull(args, "Arguments cannot be null");
			Validate.notNull(alias, "Alias cannot be null");

			List<String> completions = null;
			try {
				if (completer != null) {
					completions = completer.onTabComplete(sender, this, alias, args);
				}
				if (completions == null && executor instanceof TabCompleter) {
					completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
				}
			} catch (Throwable ex) {
				StringBuilder message = new StringBuilder();
				message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');

				for (String arg : args) {
					message.append(arg).append(' ');
				}

				message.deleteCharAt(message.length() - 1).append("' in plugin ")
						.append(owningPlugin.getDescription().getFullName());
				throw new CommandException(message.toString(), ex);
			}

			if (completions == null) {
				return super.tabComplete(sender, alias, args);
			}
			return completions;
		}

		@Override
		public boolean testPermission(CommandSender target) {
			if (testPermissionSilent(target)) {
				return true;
			}

			target.sendMessage("");
			target.sendMessage("§c* §fVocê não tem §cpermissão§f para executar esse comando!");
			target.sendMessage("");
			return false;
		}

		@Override
		public boolean testPermissionSilent(CommandSender target) {
			if (target instanceof Player) {
				Player p = (Player) target;
				Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

				if (member == null) {
					p.kickPlayer("ERRO");
					return false;
				}

				return member.hasGroupPermission(group);
			}

			return true;
		}
	}

	public class BukkitCompleter implements TabCompleter {

		private final Map<String, Entry<Method, Object>> completers = new HashMap<String, Entry<Method, Object>>();

		public void addCompleter(String label, Method m, Object obj) {
			completers.put(label, new AbstractMap.SimpleEntry<Method, Object>(m, obj));
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label,
				String[] args) {
			for (int i = args.length; i >= 0; i--) {
				StringBuilder buffer = new StringBuilder();
				buffer.append(label.toLowerCase());
				for (int x = 0; x < i; x++) {
					if (!args[x].equals("") && !args[x].equals(" ")) {
						buffer.append(".").append(args[x].toLowerCase());
					}
				}
				String cmdLabel = buffer.toString();
				if (completers.containsKey(cmdLabel)) {
					Entry<Method, Object> entry = completers.get(cmdLabel);
					try {
						return (List<String>) entry.getKey().invoke(entry.getValue(),
								new BukkitCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}

}