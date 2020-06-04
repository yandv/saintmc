package tk.yallandev.saintmc.bungee.listener;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.common.networking.PacketHandler;

public class PacketListener implements Listener {

	@EventHandler(priority = -128)
	public void onPluginMessage(PluginMessageEvent event) {
		if (!event.getTag().equals("core:Packet"))
			return;
		
		try {
			PacketHandler.firePacket(PacketHandler.decodePacket(event.getData()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
