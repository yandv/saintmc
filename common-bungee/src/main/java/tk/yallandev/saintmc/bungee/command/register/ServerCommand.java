package tk.yallandev.saintmc.bungee.command.register;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.bungee.event.BlockAddressEvent;
import tk.yallandev.saintmc.bungee.event.ClearVerifyingEvent;
import tk.yallandev.saintmc.bungee.event.UnblockAddressEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class ServerCommand implements CommandClass {

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

	@Command(name = "ping")
	public void pingCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		if (cmdArgs.getArgs().length == 0) {
			if (cmdArgs.isPlayer()) {
				sender.sendMessage("§aO seu ping é de " + cmdArgs.getPlayer().getPing() + "ms.");
			}
			return;
		}

		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(cmdArgs.getArgs()[0]);

		if (proxiedPlayer == null) {
			sender.sendMessage("§cO jogador " + cmdArgs.getArgs()[0] + " está offline!");
			return;
		}

		sender.sendMessage("§aO ping do " + proxiedPlayer.getName() + " é de " + proxiedPlayer.getPing() + "ms.");
	}

	@Command(name = "ip")
	public void ipCommand(BungeeCommandArgs cmdArgs) {
		if (cmdArgs.isPlayer()) {
			cmdArgs.getSender()
					.sendMessage("§aVocê está no servidor " + cmdArgs.getPlayer().getServer().getInfo().getName());
		} else {
			CommandSender sender = cmdArgs.getSender();
			String[] args = cmdArgs.getArgs();

			if (args.length <= 1) {
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <ip>§f para desbloquear um ip.");
				return;
			}

			if (args[0].equalsIgnoreCase("add")) {
				ProxyServer.getInstance().getPluginManager().callEvent(new BlockAddressEvent(args[1]));
				sender.sendMessage("§aO ip " + args[1] + " foi bloqueado!");
			} else if (args[0].equalsIgnoreCase("remove")) {
				ProxyServer.getInstance().getPluginManager().callEvent(new UnblockAddressEvent(args[1]));
				sender.sendMessage("§aO ip " + args[1] + " foi desbloqueado!");
			} else if (args[0].equalsIgnoreCase("clear")) {
				ProxyServer.getInstance().getPluginManager().callEvent(new ClearVerifyingEvent());
				sender.sendMessage("§aOs ips foram limpos!");
			}
		}
	}

	@Command(name = "send", groupToUse = Group.ADMIN, usage = "/<command> <player> <server>")
	public void sendCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel()
					+ " <all:current:player>§f para enviar alguém a algum servidor.");
			return;
		}

		if (args[0].equalsIgnoreCase("all")) {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getServerByName(args[1]);

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage(" §c* §fO servidor §a" + args[1] + "§f não existe!");
				return;
			}

			ProxyServer.getInstance().getPlayers().forEach(a -> a.connect(server.getServerInfo()));
			sender.sendMessage(
					" §a* §fVocê enviou §atodos jogadores §fpara o servidor §a" + server.getServerId() + "§f!");
			return;
		} else if (args[0].equalsIgnoreCase("current")) {
			if (cmdArgs.isPlayer()) {
				ProxiedServer server = BungeeMain.getInstance().getServerManager().getServerByName(args[1]);

				if (server == null || server.getServerInfo() == null) {
					sender.sendMessage(" §c* §fO servidor §a" + args[1] + "§f não existe!");
					return;
				}

				cmdArgs.getPlayer().getServer().getInfo().getPlayers().forEach(a -> a.connect(server.getServerInfo()));
				sender.sendMessage(
						" §a* §fVocê enviou §atodos jogadores §fpara o servidor §a" + server.getServerId() + "§f!");
			} else {
				sender.sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			}

			return;
		}

		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f está offline!");
			return;
		}

		ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(args[1]);

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage(" §c* §fO servidor §a" + args[1] + "§f não existe!");
			return;
		}

		sender.sendMessage(" §a* §fVocê enviou o jogador §a" + target.getName() + " §fpara o servidor §a"
				+ server.getServerId() + "§f!");
		target.connect(server.getServerInfo());
	}

	@Command(name = "lobby", aliases = { "hub", "l" }, usage = "/<command> <player> <server>")
	public void lobbyCommand(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member sender = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (sender.isOnCooldown("connect-command")) {
			sender.sendMessage(
					"§cEspere mais " + DateUtils.formatTime(sender.getCooldown("connect-command"), DECIMAL_FORMAT)
							+ "s para se conectar novamente!");
			return;
		}

		ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next();

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage("§cNenhum servidor de lobby disponivel!");
			return;
		}

		cmdArgs.getPlayer().connect(server.getServerInfo());
		sender.setCooldown("connect-command", 4);
	}

	@Command(name = "clanxclan", aliases = { "cxc" }, usage = "/evento")
	public void clanCommand(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member sender = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (sender.isOnCooldown("connect-command")) {
			sender.sendMessage(
					"§cEspere mais " + DateUtils.formatTime(sender.getCooldown("connect-command"), DECIMAL_FORMAT)
							+ "s para se conectar novamente!");
			return;
		}

		ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.CLANXCLAN).next();

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage("§cNenhum servidor de Clan x Clan disponivel!");
			return;
		}

		cmdArgs.getPlayer().connect(server.getServerInfo());
		sender.setCooldown("connect-command", 4);
	}

	@Command(name = "server", usage = "/<command> <player> <server>", aliases = { "go", "connect", "ir" })
	public void serverCommand(BungeeCommandArgs cmdArgs) {
		Member sender = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (!cmdArgs.isPlayer()) {
			sender.sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		if (sender.isOnCooldown("connect-command")) {
			sender.sendMessage(
					"§cEspere mais " + DateUtils.formatTime(sender.getCooldown("connect-command"), DECIMAL_FORMAT)
							+ "s para se conectar novamente!");
			return;
		}

		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <server>§f para conectar-se a algum servidor.");
			return;
		}

		String serverId = args[0].endsWith(CommonConst.IP_END) ? args[0] : args[0] + "." + CommonConst.IP_END;

		ProxiedServer server = BungeeMain.getInstance().getServerManager().getServerByName(serverId);

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage(" §c* §fO servidor §a" + serverId + "§f não existe!");
			return;
		}

		cmdArgs.getPlayer().connect(server.getServerInfo());
		sender.setCooldown("connect-command", 2);
	}

	@Command(name = "play", usage = "/<command> <player> <server>", aliases = { "jogar" })
	public void playCommand(BungeeCommandArgs cmdArgs) {
		Member sender = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (sender.isOnCooldown("connect-command")) {
			sender.sendMessage(
					"§cEspere mais " + DateUtils.formatTime(sender.getCooldown("connect-command"), DECIMAL_FORMAT)
							+ "s para se conectar novamente!");
			return;
		}

		String[] args = cmdArgs.getArgs();

		if (!cmdArgs.isPlayer()) {
			sender.sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <type>§f para entrar em algum servidor.");
			return;
		}

		sender.setCooldown("connect-command", 4);

		switch (args[0].toLowerCase()) {
		case "hg":
		case "hungergames": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.HUNGERGAMES)
					.next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Hungergames disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "lobby": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Lobby disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "gladiator": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.GLADIATOR).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Gladiator disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "clanxclan":
		case "cxc": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.CLANXCLAN).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Clan x Clan disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "skywars-solo":
		case "sw-solo":
		case "sw": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.SW_SOLO).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Skywars disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "skywars-team":
		case "sw-team": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.SW_TEAM).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Skywars Team disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "skywars-s":
		case "sw-squad": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.SW_SQUAD).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de Skywars Squad disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "pvp": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.SIMULATOR).next();

			if (server == null || server.getServerInfo() == null) {
				server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.FULLIRON).next();

				if (server == null || server.getServerInfo() == null) {
					sender.sendMessage("§cNenhum servidor de KitPvP disponivel!");
					return;
				}
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "fulliron":
		case "kitpvp": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.FULLIRON).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de KitPvP disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "simulator": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.SIMULATOR).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de KitPvP disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		default: {
			cmdArgs.getPlayer().sendMessage("§cO servidor " + args[0] + " não existe!");
			break;
		}
		}
	}

	@Completer(name = "server", aliases = { "go", "connect", "ir" })
	public List<String> serverCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> serverList = new ArrayList<>();

			if (cmdArgs.getArgs()[0].isEmpty()) {
				for (ProxiedServer server : BungeeMain.getInstance().getServerManager().getServers().stream()
						.sorted((o1, o2) -> o1.getServerType().compareTo(o2.getServerType()))
						.collect(Collectors.toList()))
					serverList.add(server.getServerId());
			} else {
				for (ProxiedServer server : BungeeMain.getInstance().getServerManager().getServers().stream()
						.sorted((o1, o2) -> o1.getServerType().compareTo(o2.getServerType()))
						.collect(Collectors.toList()))
					if (server.getServerId().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						serverList.add(server.getServerId());
			}

			return serverList;
		}

		return new ArrayList<>();
	}
}
