package tk.yallandev.saintmc.bungee.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ClientListener implements Listener {

	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		if (!event.getTag().equals("BungeeCord"))
			return;

		if (!(event.getSender() instanceof Server))
			return;

		if (!(event.getReceiver() instanceof ProxiedPlayer))
			return;

//		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getReceiver();
//		
//		Member player = CommonGeneral.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());
//		
//		ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
//		String subChannel = in.readUTF();
	}

}
