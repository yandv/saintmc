package tk.yallandev.saintmc.bungee.listener;

import net.md_5.bungee.api.plugin.Listener;

public class StoreListener implements Listener {

//	@EventHandler
//	public void onItemPreProcess(ItemPreProcessEvent event) {
//		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getItemInfo().getPlayer());
//
//		if (member == null) {
//			event.setCancelled(true);
//			return;
//		}
//
//		try {
//			Group.valueOf(event.getItemInfo().getGrupo().toUpperCase().split("-")[0]);
//		} catch (Exception ex) {
//			event.setCancelled(true);
//			CommonGeneral.getInstance().debug("The group " + event.getItemInfo().getGrupo() + " doesnt exist!");
//			return;
//		}
//
//		CommonGeneral.getInstance().debug("The item " + event.getItemInfo().getPlayer() + " has been loaded!");
//	}
//
//	@EventHandler
//	public void onItemProcess(ItemProcessEvent event) {
//		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getItemInfo().getPlayer());
//
//		Group group = Group.valueOf(event.getItemInfo().getGrupo().toUpperCase().split("-")[0]);
//		int days = event.getItemInfo().getDias();
//
//		ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
//				"tempgroup " + member.getPlayerName() + " " + days + "d " + group.name());
//		member.sendMessage("§aO seu pedido " + event.getItemInfo().getIdEntrega() + " foi entregue!");
//		CommonGeneral.getInstance().debug("The product " + event.getItemInfo().getIdEntrega() + " has been actived!");
//	}

}
