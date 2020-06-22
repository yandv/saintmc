package tk.yallandev.saintmc.bungee.listener;

import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.account.BungeeMember;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

public class ChatListener implements Listener {

	private List<String> allowedCommands = Arrays.asList("login", "register", "registrar", "logar");
	private List<String> blockedCommands = Arrays.asList("lobby", "go", "ir", "connect", "play", "l", "hud");

	@EventHandler
	public void onChat(ChatEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getSender() instanceof ProxiedPlayer))
			return;

		BungeeMember player = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(((ProxiedPlayer) event.getSender()).getUniqueId());

		if (player == null) {
			event.setCancelled(true);
			return;
		}

		if (event.isCommand()) {
			if (player.isScreensharing()) {
				if (event.isCommand()) {
					String command = event.getMessage().split(" ")[0].replace("/", "");

					if (blockedCommands.contains(command.toLowerCase())) {
						event.setCancelled(true);
						player.sendMessage(
								"§4§l> §fVocê não pode §cexecutar§f esse comando enquanto estiver na §escreenshare§f!");
					}
				}

			}

			if (!player.getLoginConfiguration().isLogged()) {

				String command = event.getMessage().split(" ")[0].replace("/", "");

				if (!allowedCommands.contains(command.toLowerCase())) {
					event.setCancelled(true);
					player.sendMessage("§4§l> §fVocê não pode §cexecutar§f esse comando enquanto não estiver logado!");
					return;
				}
			}
		}

		if (!event.isCommand())
			if (player.getAccountConfiguration().isStaffChatEnabled()) {
				if (player.hasGroupPermission(Group.HELPER)) {

					if (!player.getAccountConfiguration().isSeeingStaffchat()) {
						player.sendMessage("§aVocê estava falando no staffchat sem pode ver ele!");
						player.sendMessage("§aVocê foi retirado do staffchat, use /staffchat on para pode ver o staffchat");
						player.getAccountConfiguration().setStaffChatEnabled(false);
						event.setCancelled(true);
						return;
					}

					CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(
							m -> m.hasGroupPermission(Group.HELPER) && m.getAccountConfiguration().isSeeingStaffchat())
							.forEach(m -> m
									.sendMessage("§e§l[STAFF] " + Tag.getByName(player.getGroup().toString()).getPrefix()
											+ " " + player.getPlayerName() + "§f: " + event.getMessage()));

					event.setCancelled(true);
				} else
					player.getAccountConfiguration().setStaffChatEnabled(false);
			}
	}

}
