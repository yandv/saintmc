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
		cmdArgs.getSender().sendMessage("§aTemos " + ProxyServer.getInstance().getOnlineCount() + " jogadores online!");
		cmdArgs.getSender().sendMessage("");
		ProxyServer.getInstance().getServers().values().stream()
				.sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).forEach(info -> cmdArgs.getSender()
						.sendMessage("§9" + info.getName() + " §7-§a " + info.getPlayers().size() + " online"));
		cmdArgs.getSender().sendMessage("");
	}

	@Command(name = "broadcast", groupToUse = Group.MODPLUS, usage = "/<command> <mesage>", aliases = { "bc", "aviso",
			"alert" })
	public void broadcastCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§aUso /" + cmdArgs.getLabel() + " <mensagem> para mandar uma mensagem para todos.");
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

	@Command(name = "maintence", aliases = { "manutencao" }, groupToUse = Group.ADMIN)
	public void maintenceCommand(CommandArgs cmdArgs) {
		BungeeMain.getInstance().setMaintenceMode(!BungeeMain.getInstance().isMaintenceMode());
		ProxyServer.getInstance()
				.broadcast(BungeeMain.getInstance().isMaintenceMode() ? "§aO servidor entrou em modo manutenção."
						: "§cO servidor saiu do modo manutenção.");

		if (BungeeMain.getInstance().isMaintenceMode()) {
			for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers().stream()
					.filter(member -> !member.hasGroupPermission(Group.YOUTUBER)).collect(Collectors.toList())) {
				((BungeeMember) member).getProxiedPlayer().disconnect("§cO servidor entrou em modo manutenção.");
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
				" §aVocê " + (member.getAccountConfiguration().isStaffChatEnabled() ? "entrou no" : "saiu do")
						+ " §astaff-chat§a.");

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
					" §aAgora você " + (member.getAccountConfiguration().isSeeingStafflog() ? "vê" : "não vê mais")
							+ " §aas logs dos staffs§a.");
			return;
		}
	}

	@Command(name = "fakelist", runAsync = true, groupToUse = Group.TRIAL, usage = "/<command> <player> <server>")
	public void fakelistCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aJogadores usando fake: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(member -> member.isUsingFake())
				.forEach(member -> sender.sendMessage(" §fO jogador §a" + member.getPlayerName()
						+ "§f está usando o fake §e" + member.getFakeName() + "§f."));
	}

	@Command(name = "stafflist", runAsync = true, groupToUse = Group.MOD, usage = "/<command> <player> <server>")
	public void stafflistCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();

		sender.sendMessage("§aStaff online: ");
		sender.sendMessage(" ");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(member -> member.hasGroupPermission(Group.TRIAL))
				.sorted((o1, o2) -> o1.getServerGroup().compareTo(o2.getServerGroup()))
				.forEach(member -> sender.sendMessage("§a" + member.getPlayerName() + " §8- §f"
						+ Tag.valueOf(member.getServerGroup().name()).getPrefix()));
	}

	@Command(name = "find", groupToUse = Group.TRIAL, usage = "/<command> <player>")
	public void findCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §cUse /find <player> para localizar algum jogador.");
			return;
		}

		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(" §cO jogador §c" + args[0] + "§c está offline.");
			return;
		}

		TextComponent txt = new TextComponent(" §aO jogador §a" + target.getName() + " §aestá no servidor §a"
				+ target.getServer().getInfo().getName().toUpperCase() + "§a.");
		TextComponent text = new TextComponent(" §7(Clique aqui para teletransportar-se)");

		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + target.getName()));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClique aqui.")));

		sender.sendMessage(new BaseComponent[] { txt, text });
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
