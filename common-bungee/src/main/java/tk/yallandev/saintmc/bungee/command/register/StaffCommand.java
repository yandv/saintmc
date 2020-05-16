package tk.yallandev.saintmc.bungee.command.register;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.account.BungeeMember;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.BattleServer;

public class StaffCommand implements CommandClass {

	@Command(name = "glist", groupToUse = Group.GERENTE, usage = "/<command>", aliases = { "onlines", "online" })
	public void glist(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		sender.sendMessage(" §a* §fJogadores online no servidor: §a" + ProxyServer.getInstance().getOnlineCount()
				+ " jogadores! §7(No total)");
		sender.sendMessage("");
		ProxyServer.getInstance().getServers().forEach((map, info) -> sender
				.sendMessage(" §a * §f" + info.getName() + " -§a " + info.getPlayers().size() + " jogadores"));
		sender.sendMessage("");
	}

	@Command(name = "broadcast", groupToUse = Group.GERENTE, usage = "/<command> <mesage>", aliases = { "bc", "aviso",
			"alert" })
	public void broadcast(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <mensagem>§f para mandar uma mensagem para todos.");
			return;
		}

		String msg = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");
		msg = sb.toString();

		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§c§lAVISO §f" + msg.replace("&", "§")));
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
	}

	@Command(name = "staffchat", groupToUse = Group.YOUTUBERPLUS, usage = "/<command>", aliases = { "sc" })
	public void staffchat(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getPlayer().getUniqueId());
		member.getAccountConfiguration().setStaffChatEnabled(!member.getAccountConfiguration().isStaffChatEnabled());
		sender.sendMessage(
				" §a* §fVocê " + (member.getAccountConfiguration().isStaffChatEnabled() ? "entrou no" : "saiu do")
						+ " §astaff-chat§f!");
	}

	@Command(name = "fakelist", runAsync = true, groupToUse = Group.MODGC, usage = "/<command> <player> <server>")
	public void fakelistCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aJogadores usando fake: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(member -> member.isUsingFake())
				.forEach(member -> sender.sendMessage(" §a* §fO jogador §a" + member.getPlayerName()
						+ "§f está usando o fake §e" + member.getFakeName() + "§f!"));
	}

	@Command(name = "find", groupToUse = Group.MODGC, usage = "/<command> <player>")
	public void findCommand(BungeeCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/find <player>§f para localizar algum jogador.");
			return;
		}

		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f está offline!");
			return;
		}

		TextComponent txt = new TextComponent(" §a* §fO jogador §a" + target.getName() + " §festá no servidor §a"
				+ target.getServer().getInfo().getName().toUpperCase() + "§f!");
		TextComponent text = new TextComponent(" §7(Clique aqui para teletransportar-se)");

		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClique aqui!")));

		sender.sendMessage(new BaseComponent[] { txt, text });
	}

	@Command(name = "screenshare", groupToUse = Group.MODGC, usage = "/<command> <player> <server>")
	public void screenshareCommand(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/screenshare <player>§f para enviar alguém para screenshare.");
			return;
		}

		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[0]);

		if (proxiedPlayer == null) {
			sender.sendMessage(" §a* §fO jogador §c" + args[0] + "§f está offline!");
			return;
		}

		BungeeMember member = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(proxiedPlayer.getUniqueId());

		if (member == null) {
			sender.sendMessage(" §a* §fO jogador §c" + args[0] + "§f está offline!");
			return;
		}

		if (!member.getLoginConfiguration().isLogged()) {
			sender.sendMessage(" §a* §fO jogador não está logado no servidor!");
			return;
		}

		if (member.isScreensharing()) {
			if (member.getScreenshareStaff().equals(sender.getUniqueId())) {
				sender.sendMessage(" §a* §fVocê liberou o jogador da §aScreenshare§f!");

				member.setScreensharing(false);
				member.setScreenshareStaff(null);

				if (member.getLastServerId().isEmpty()) {
					proxiedPlayer.connect(BungeeMain.getPlugin().getServerManager().getBalancer(ServerType.LOBBY).next()
							.getServerInfo());
				} else {
					proxiedPlayer.connect(BungeeMain.getPlugin().getProxy().getServerInfo(member.getLastServerId()));
				}

				return;
			}

			sender.sendMessage(" §c* §fVocê não pode retirar esse jogador da §aScreenshare§f!");
		} else {
			BattleServer server = BungeeMain.getPlugin().getServerManager().getBalancer(ServerType.SCREENSHARE).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§c§l> §fNenhuma sala de §aScreenshare§f está disponível no momento!");
				return;
			}

			member.setScreenshareStaff(sender.getUniqueId());
			member.setScreensharing(true);

			proxiedPlayer.connect(server.getServerInfo());
			member.getProxiedPlayer().connect(server.getServerInfo());

			sender.sendMessage(" §a* §fVocê enviou o jogador §a" + member.getPlayerName() + "§f para §aScreenshare§f!");
		}
	}

}
