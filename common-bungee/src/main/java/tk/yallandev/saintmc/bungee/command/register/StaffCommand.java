package tk.yallandev.saintmc.bungee.command.register;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.tag.Tag;

public class StaffCommand implements CommandClass {

	@Command(name = "glist", groupToUse = Group.TRIAL, usage = "/<command>", aliases = { "onlines", "online" })
	public void glistCommand(CommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage(" §a* §fJogadores online no servidor: §a"
				+ ProxyServer.getInstance().getOnlineCount() + " jogadores! §7(No total)");
		cmdArgs.getSender().sendMessage("");
		ProxyServer.getInstance().getServers().values().stream()
				.sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).forEach(info -> cmdArgs.getSender()
						.sendMessage(" §a * §f" + info.getName() + " -§a " + info.getPlayers().size() + " jogadores"));
		cmdArgs.getSender().sendMessage("");
	}

	@Command(name = "broadcast", groupToUse = Group.MODPLUS, usage = "/<command> <mesage>", aliases = { "bc", "aviso",
			"alert" })
	public void broadcastCommand(CommandArgs cmdArgs) {
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
		CommonGeneral.getInstance().getMemberManager()
				.broadcast("§7O " + sender.getName() + " enviou uma mensagem global!", Group.TRIAL);
	}

	@Command(name = "maintence", aliases = { "manutencao" }, groupToUse = Group.DEVELOPER)
	public void maintenceCommand(CommandArgs cmdArgs) {
		BungeeMain.getInstance().setMaintenceMode(!BungeeMain.getInstance().isMaintenceMode());
		ProxyServer.getInstance()
				.broadcast(BungeeMain.getInstance().isMaintenceMode() ? "§aO servidor entrou em modo manutenção!"
						: "§cO servidor saiu do modo manutenção!");

		if (BungeeMain.getInstance().isMaintenceMode()) {
			for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> !member.hasGroupPermission(Group.YOUTUBER)).collect(Collectors.toList())) {
				((BungeeMember) member).getProxiedPlayer().disconnect("§cO servidor entrou em modo manutenção!");
			}
		}
	}

	@Command(name = "staffchat", groupToUse = Group.TRIAL, usage = "/<command>", aliases = { "sc" })
	public void staffchatCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (cmdArgs.getArgs().length == 1) {
			if (cmdArgs.getArgs()[0].equalsIgnoreCase("on")) {
				if (member.getAccountConfiguration().isSeeingStaffchat()) {
					member.sendMessage("§cO staffchat já está ativado!");
				} else {
					member.getAccountConfiguration().setSeeingStaffchat(true);
					member.sendMessage("§aVocê agora vê o staffchat!");
				}

				return;
			} else if (cmdArgs.getArgs()[0].equalsIgnoreCase("off")) {
				if (!member.getAccountConfiguration().isSeeingStaffchat()) {
					member.sendMessage("§cO staffchat já está desativado!");
				} else {
					member.getAccountConfiguration().setSeeingStaffchat(false);
					member.sendMessage("§cVocê agora não vê mais o staffchat!");
				}

				return;
			}
		}

		member.getAccountConfiguration().setStaffChatEnabled(!member.getAccountConfiguration().isStaffChatEnabled());
		sender.sendMessage(
				" §a* §fVocê " + (member.getAccountConfiguration().isStaffChatEnabled() ? "entrou no" : "saiu do")
						+ " §astaff-chat§f!");

		if (member.getAccountConfiguration().isStaffChatEnabled())
			if (!member.getAccountConfiguration().isSeeingStaffchat())
				member.getAccountConfiguration().setSeeingStaffchat(true);
	}

	@Command(name = "stafflog", groupToUse = Group.TRIAL, usage = "/<command>", aliases = { "sl" })
	public void stafflogCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (cmdArgs.getArgs().length == 0) {
			member.getAccountConfiguration().setSeeingStafflog(!member.getAccountConfiguration().isSeeingStafflog());
			member.sendMessage(
					" §a* §fAgora você " + (member.getAccountConfiguration().isSeeingStafflog() ? "vê" : "não vê mais")
							+ " §aas logs dos staffs§f!");
			return;
		}
	}

	@Command(name = "fakelist", runAsync = true, groupToUse = Group.TRIAL, usage = "/<command> <player> <server>")
	public void fakelistCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aJogadores usando fake: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(member -> member.isUsingFake())
				.forEach(member -> sender.sendMessage(" §a* §fO jogador §a" + member.getPlayerName()
						+ "§f está usando o fake §e" + member.getFakeName() + "§f!"));
	}

	@Command(name = "stafflist", runAsync = true, groupToUse = Group.MOD, usage = "/<command> <player> <server>")
	public void stafflistCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aStaff online: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(member -> member.hasGroupPermission(Group.TRIAL))
				.sorted((o1, o2) -> o1.getServerGroup().compareTo(o2.getServerGroup()))
				.forEach(member -> sender.sendMessage("§7" + member.getPlayerName() + " §8- §f"
						+ Tag.valueOf(member.getServerGroup().name()).getPrefix()));
	}

	@Command(name = "find", groupToUse = Group.TRIAL, usage = "/<command> <player>")
	public void findCommand(CommandArgs cmdArgs) {
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

	@Command(name = "screenshare", aliases = {
			"ss" }, groupToUse = Group.MODPLUS, usage = "/<command> <player> <server>")
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

				cmdArgs.getPlayer().connect(
						BungeeMain.getPlugin().getServerManager().getBalancer(ServerType.LOBBY).next().getServerInfo());

				if (member.getLastServerId().isEmpty())
					proxiedPlayer.connect(BungeeMain.getPlugin().getServerManager().getBalancer(ServerType.LOBBY).next()
							.getServerInfo());
				else
					proxiedPlayer.connect(BungeeMain.getPlugin().getProxy().getServerInfo(member.getLastServerId()));

				return;
			}

			sender.sendMessage(" §c* §fVocê não pode retirar esse jogador da §aScreenshare§f!");
		} else {
			ProxiedServer server = BungeeMain.getPlugin().getServerManager().getBalancer(ServerType.SCREENSHARE).next();

			if (server == null || server.getServerInfo() == null) {
				sender.sendMessage("§c§l> §fNenhuma sala de §aScreenshare§f está disponível no momento!");
				return;
			}

			member.setScreenshareStaff(sender.getUniqueId());
			member.setScreensharing(true);

			cmdArgs.getPlayer().connect(server.getServerInfo());
			proxiedPlayer.connect(server.getServerInfo());
			member.getProxiedPlayer().connect(server.getServerInfo());

			sender.sendMessage(" §a* §fVocê enviou o jogador §a" + member.getPlayerName() + "§f para §aScreenshare§f!");
		}
	}

	@Completer(name = "find", aliases = { "report" })
	public List<String> serverCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> playerList = new ArrayList<>();

			try {
				for (String playerName : ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName)
						.collect(Collectors.toList()))
					if (cmdArgs.getArgs()[0].toLowerCase().startsWith(playerName))
						playerList.add(cmdArgs.getArgs()[0]);
			} catch (Exception ex) {
				for (String playerName : ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName)
						.collect(Collectors.toList()))
					playerList.add(playerName);
			}

			return playerList;
		}

		return new ArrayList<>();
	}

}
