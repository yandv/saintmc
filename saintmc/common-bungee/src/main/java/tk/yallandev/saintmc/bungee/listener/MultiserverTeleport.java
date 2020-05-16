package tk.yallandev.saintmc.bungee.listener;

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
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

public class MultiserverTeleport implements Listener {

	@EventHandler
	public void onChat(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer))
			return;
		
		String[] message = event.getMessage().trim().split(" ");
		String command = message[0].toLowerCase();
			
		if (!command.startsWith("/teleport") && !command.startsWith("/tp"))
			return;
		
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(((ProxiedPlayer) event.getSender()).getUniqueId());
		
		if (!player.hasGroupPermission(Group.YOUTUBERPLUS))
			return;
		
		String[] args = new String[message.length - 1];
		
		for (int i = 1; i < message.length; i++) {
			args[i - 1] = message[i];
		}
		
		if (args.length != 1)
			return;
		
		String target = args[0];
		ProxiedPlayer targetPlayer;
		
		if (target.length() == 32 || target.length() == 36) {
			targetPlayer = BungeeMain.getPlugin().getProxy().getPlayer(CommonGeneral.getInstance().getUuid(target));
		} else {
			targetPlayer = BungeeMain.getPlugin().getProxy().getPlayer(target);
		}
		
		if (targetPlayer == null)
			return;
		
		if (targetPlayer.getServer() == null || targetPlayer.getServer().getInfo() == null)
			return;
		
		if (targetPlayer.getServer().getInfo().getName().equals(((ProxiedPlayer) event.getSender()).getServer().getInfo().getName()))
			return;
		
		event.setCancelled(true);
		
		((ProxiedPlayer) event.getSender()).connect(BungeeMain.getPlugin().getProxy().getServerInfo(targetPlayer.getServer().getInfo().getName()));
		
		ProxyServer.getInstance().getScheduler().schedule(BungeeMain.getPlugin(), () -> {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("BungeeTeleport");
			out.writeUTF(targetPlayer.getUniqueId().toString());
			((ProxiedPlayer) event.getSender()).getServer().sendData("BungeeCord", out.toByteArray());
		}, 1000, TimeUnit.MILLISECONDS);
	}
}
