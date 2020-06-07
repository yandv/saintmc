package tk.yallandev.saintmc.bungee.listener;

import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

public class ChatListener implements Listener {

	private List<String> allowedCommands = Arrays.asList("login", "register", "registrar", "logar");

	@EventHandler
	public void onChat(ChatEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getSender() instanceof ProxiedPlayer))
			return;

		Member player = CommonGeneral.getInstance().getMemberManager()
				.getMember(((ProxiedPlayer) event.getSender()).getUniqueId());

		if (player == null) {
			event.setCancelled(true);
			return;
		}

		if (event.isCommand())
			if (!player.getLoginConfiguration().isLogged()) {

				String command = event.getMessage().split(" ")[0].replace("/", "");

				if (!allowedCommands.contains(command.toLowerCase())) {
					event.setCancelled(true);
					player.sendMessage("§4§l> §fVocê não pode §cexecutar§f esse comando enquanto não estiver logado!");
					return;
				}
			}

		if (!event.isCommand())
			if (player.getAccountConfiguration().isStaffChatEnabled()) {
				if (player.hasGroupPermission(Group.YOUTUBERPLUS)) {
					CommonGeneral.getInstance().getMemberManager().getMembers().stream()
							.filter(m -> m.hasGroupPermission(Group.YOUTUBERPLUS))
							.forEach(m -> m
									.sendMessage("§e§l[SC] " + Tag.getByName(player.getGroup().toString()).getPrefix()
											+ " " + player.getPlayerName() + "§f: " + event.getMessage()));

					event.setCancelled(true);
				} else
					player.getAccountConfiguration().setStaffChatEnabled(false);
			}
	}

}
