package tk.yallandev.saintmc.bungee.listener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.common.account.Member;
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

		String[] message = event.getMessage().trim().split(" ");
		String command = message[0].replace("/", "").toLowerCase();

		if (event.isCommand())
			if (command.startsWith("teleport") || command.startsWith("tp")) {
				Member player = CommonGeneral.getInstance().getMemberManager()
						.getMember(((ProxiedPlayer) event.getSender()).getUniqueId());

				if (!player.hasGroupPermission(Group.YOUTUBERPLUS))
					return;

				String[] args = new String[message.length - 1];

				for (int i = 1; i < message.length; i++) {
					args[i - 1] = message[i];
				}

				if (args.length == 1) {
					String target = args[0];
					ProxiedPlayer targetPlayer;

					if (target.length() == 32 || target.length() == 36) {
						targetPlayer = BungeeMain.getPlugin().getProxy()
								.getPlayer(CommonGeneral.getInstance().getUuid(target));
					} else {
						targetPlayer = BungeeMain.getPlugin().getProxy().getPlayer(target);
					}

					if (targetPlayer == null)
						return;

					if (targetPlayer.getServer() == null || targetPlayer.getServer().getInfo() == null)
						return;

					if (targetPlayer.getServer().getInfo().getName()
							.equals(((ProxiedPlayer) event.getSender()).getServer().getInfo().getName()))
						return;

					event.setCancelled(true);

					((ProxiedPlayer) event.getSender()).connect(BungeeMain.getPlugin().getProxy()
							.getServerInfo(targetPlayer.getServer().getInfo().getName()));

					ProxyServer.getInstance().getScheduler().schedule(BungeeMain.getPlugin(), () -> {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("BungeeTeleport");
						out.writeUTF(targetPlayer.getUniqueId().toString());
						((ProxiedPlayer) event.getSender()).getServer().sendData("BungeeCord", out.toByteArray());
					}, 300, TimeUnit.MILLISECONDS);
					return;
				}
			}

		BungeeMember player = (BungeeMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(((ProxiedPlayer) event.getSender()).getUniqueId());

		if (player == null) {
			event.setCancelled(true);
			return;
		}

		if (event.isCommand()) {
			if (player.isScreensharing()) {
				if (event.isCommand()) {
					if (blockedCommands.contains(command)) {
						event.setCancelled(true);
						player.sendMessage("§cVocê não pode executar esse comando na Screenshare!");
					}
				}
			}

			if (!player.getLoginConfiguration().isLogged()) {
				if (!allowedCommands.contains(command)) {
					event.setCancelled(true);
					player.sendMessage("§cVocê não pode executar esse comando antes de se logar!");
					return;
				}
			}
		}

		if (!event.isCommand())
			if (player.getAccountConfiguration().isStaffChatEnabled()) {
				if (player.hasGroupPermission(Group.AJUDANTE)) {
					if (!player.getAccountConfiguration().isSeeingStaffchat()) {
						player.sendMessage("§cVocê estava falando no staffchat sem pode ver ele!");
						player.sendMessage(
								"§cVocê foi retirado do staffchat, use /staffchat on para pode ver o staffchat");
						player.getAccountConfiguration().setStaffChatEnabled(false);
						event.setCancelled(true);
						return;
					}

					CommonGeneral.getInstance().getMemberManager().getMembers().stream().filter(
							m -> m.hasGroupPermission(Group.AJUDANTE) && m.getAccountConfiguration().isSeeingStaffchat())
							.forEach(m -> m.sendMessage(
									"§e§l[STAFF] " + Tag.getByName(player.getGroup().toString()).getPrefix() + " "
											+ player.getPlayerName() + "§f: " + event.getMessage()));

					event.setCancelled(true);
				} else
					player.getAccountConfiguration().setStaffChatEnabled(false);
			}
	}

}
