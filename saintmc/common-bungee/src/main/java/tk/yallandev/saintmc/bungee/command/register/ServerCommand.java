package tk.yallandev.saintmc.bungee.command.register;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.BattleServer;

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
		
		sender.sendMessage(" §e* §fO ping de §a" + proxiedPlayer.getName() + "§f é de §a" + proxiedPlayer.getPing() + "ms§f.");
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
			BattleServer server = BungeeMain.getInstance().getServerManager().getServer(args[1]);

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
				BattleServer server = BungeeMain.getInstance().getServerManager().getServer(args[1]);

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

		BattleServer server = BungeeMain.getInstance().getServerManager().getServer(args[1]);

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage(" §c* §fO servidor §a" + args[1] + "§f não existe!");
			return;
		}

		sender.sendMessage(" §a* §fVocê enviou o jogador §a" + target.getName() + " §fpara o servidor §a"
				+ server.getServerId() + "§f!");
		target.connect(server.getServerInfo());
	}

	@Command(name = "lobby", usage = "/<command> <player> <server>")
	public void lobbyCommand(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		BattleServer server = BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next();

		if (server == null || server.getServerInfo() == null)
			return;

		cmdArgs.getSender().sendMessage("");
		cmdArgs.getSender().sendMessage("§a§l> §fConectando-se ao servidor §a" + server.getServerId().toLowerCase() + "§f!");
		cmdArgs.getSender().sendMessage("");
		cmdArgs.getPlayer().connect(server.getServerInfo());
	}

	@Command(name = "server", usage = "/<command> <player> <server>", aliases = { "go",
			"connect", "ir" })
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

		BattleServer server = BungeeMain.getInstance().getServerManager().getServer(args[0]);

		if (server == null || server.getServerInfo() == null) {
			sender.sendMessage(" §c* §fO servidor §a" + args[0] + "§f não existe!");
			return;
		}

		sender.sendMessage("");
		sender.sendMessage("§a§l> §fConectando-se ao servidor §a" + server.getServerId().toLowerCase() + "§f!");
		sender.sendMessage("");
		cmdArgs.getPlayer().connect(server.getServerInfo());
	}

}
