package tk.yallandev.saintmc.bungee.command.register;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.bungee.event.BlockAddressEvent;
import tk.yallandev.saintmc.bungee.event.UnblockAddressEvent;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

public class ServerCommand implements CommandClass {

	@Command(name = "ping")
	public void pingCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		if (cmdArgs.getArgs().length == 0) {
			if (cmdArgs.isPlayer()) {
				sender.sendMessage(" §e* §fSeu ping no servidor é §a" + cmdArgs.getPlayer().getPing() + "ms§f.");
			}
			return;
		}

		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(cmdArgs.getArgs()[0]);

		if (proxiedPlayer == null) {
			sender.sendMessage(" §c* §fO jogador §c" + cmdArgs.getArgs()[0] + "§f está offline!");
			return;
		}

		sender.sendMessage(
				" §e* §fO ping de §a" + proxiedPlayer.getName() + "§f é de §a" + proxiedPlayer.getPing() + "ms§f.");
	}

	@Command(name = "ip")
	public void ipCommand(BungeeCommandArgs cmdArgs) {
		if (cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§aVocê está no servidor " + cmdArgs.getPlayer().getServer().getInfo().getName());
		} else {
			CommandSender sender = cmdArgs.getSender();
			String[] args = cmdArgs.getArgs();
			
			if (args.length <= 1) {
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <ip>§f para desbloquear um ip.");
				return;
			}
			
			if (args[0].equalsIgnoreCase("add")) {
				ProxyServer.getInstance().getPluginManager().callEvent(new BlockAddressEvent(args[0]));
				sender.sendMessage("§aO ip " + args[0] + " foi bloqueado!");
			} else if (args[0].equalsIgnoreCase("remove")) {
				ProxyServer.getInstance().getPluginManager().callEvent(new UnblockAddressEvent(args[0]));
				sender.sendMessage("§aO ip " + args[0] + " foi desbloqueado!");
			}
		}
	}

	@Command(name = "send", groupToUse = Group.ADMIN, usage = "/<command> <player> <server>")
	public void sendCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <server>§f para enviar a algum servidor.");
			return;
		}

		if (args[0].equalsIgnoreCase("all")) {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(args[1]);

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage(" §c* §fO servidor §a" + args[1] + "§f não existe!");
				return;
			}

			ProxyServer.getInstance().getPlayers().forEach(a -> a.connect(server.getServerInfo()));
			sender.sendMessage(
					" §a* §fVocê enviou §atodos jogadores §fpara o servidor §a" + server.getServerId() + "§f!");
			return;
		}

		if (args[0].equalsIgnoreCase("current")) {
			if (cmdArgs.isPlayer()) {
				ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(args[1]);

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

		CommandSender sender = cmdArgs.getSender();
		ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next();

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage("§cNenhum servidor de lobby disponivel!");
			return;
		}

		cmdArgs.getPlayer().connect(server.getServerInfo());
	}

	@Command(name = "server", usage = "/<command> <player> <server>", aliases = { "go", "connect", "ir" })
	public void serverCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (!cmdArgs.isPlayer()) {
			sender.sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <player>§f para conectar-se a algum servidor.");
			return;
		}

		ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(args[0]);

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage(" §c* §fO servidor §a" + args[0] + "§f não existe!");
			return;
		}

		cmdArgs.getPlayer().connect(server.getServerInfo());
	}

	@Command(name = "play", usage = "/<command> <player> <server>", aliases = { "jogar" })
	public void playCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (!cmdArgs.isPlayer()) {
			sender.sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <type>§f para entrar em algum servidor.");
			return;
		}

		switch (args[0].toLowerCase()) {
		case "hg":
		case "hungergames": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.HUNGERGAMES)
					.next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de lobby disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "lobby": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de lobby disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "peak": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.PRIVATE_SERVER).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de hungergames disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "fulliron":
		case "kitpvp": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.FULLIRON).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de lobby disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		case "simulator": {
			ProxiedServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.SIMULATOR).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§cNenhum servidor de lobby disponivel!");
				return;
			}

			cmdArgs.getPlayer().connect(server.getServerInfo());
			break;
		}
		}
	}

	@Completer(name = "server", aliases = { "go", "connect", "ir" })
	public List<String> serverCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> tagList = new ArrayList<>();

			for (ProxiedServer server : BungeeMain.getInstance().getServerManager().getServers())
				tagList.add(server.getServerId());

			return tagList;
		}

		return new ArrayList<>();
	}
}
