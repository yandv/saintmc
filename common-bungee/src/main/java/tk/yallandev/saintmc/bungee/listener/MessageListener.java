package tk.yallandev.saintmc.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.BattleServer;

public class MessageListener implements Listener {
	
	private ServerManager manager;

	public MessageListener(ServerManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onPluginMessageEvent(PluginMessageEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer))
			return;

		ProxiedPlayer p = (ProxiedPlayer) e.getSender();

		if ((e.getTag().equalsIgnoreCase("WDL|INIT")) || (e.getTag().equalsIgnoreCase("PERMISSIONSREPL")
				&& new String(e.getData()).contains("mod.worlddownloader"))) {
			p.disconnect(new TextComponent("§4§l" + CommonConst.KICK_PREFIX
					+ "\n§f\n§cVocê está bloqueado de entrar no servidor!\n§fMotivo: §eClient/Mod não permitido\n§fEncontrado: §eWorldDownloader"
					+ "\n§f\n§6Acesse nosso discord para mais informações:\n§b" + CommonConst.DISCORD));
			return;
		}
	}
	
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord"))
            return;
        
        if (!(event.getSender() instanceof Server))
            return;
        
        if (!(event.getReceiver() instanceof ProxiedPlayer))
            return;
        
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getReceiver();
        Member player = CommonGeneral.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        
        switch (subChannel) {
        case "Hungergames": {
            event.setCancelled(true);
            if (!searchServer(player, proxiedPlayer, ServerType.HUNGERGAMES)) {
                player.sendMessage("");
                player.sendMessage("§c§l> §fNenhum servidor de §c§l" + ServerType.HUNGERGAMES.getServerName() + "§f encontrado!");
                player.sendMessage("");
            }
            break;
        }
        case "PVPFulliron": {
            event.setCancelled(true);
            if (!searchServer(player, proxiedPlayer, ServerType.FULLIRON)) {
                player.sendMessage("");
                player.sendMessage("§c§l> §fNenhum servidor de §c§l" + ServerType.FULLIRON.getServerName() + "§f encontrado!");
                player.sendMessage("");
            }
            break;
        }
        case "PVPSimulator": {
            event.setCancelled(true);
            
            if (!searchServer(player, proxiedPlayer, ServerType.SIMULATOR)) {
                player.sendMessage("");
                player.sendMessage("§c§l> §fNenhum servidor de §c§l" + ServerType.SIMULATOR.getServerName() + "§f encontrado!");
                player.sendMessage("");
            }
            break;
        }
        case "PVP": {
            event.setCancelled(true);
            
            if (!searchServer(player, proxiedPlayer, ServerType.FULLIRON))
                if (!searchServer(player, proxiedPlayer, ServerType.SIMULATOR)) 
                	player.sendMessage("");
                    player.sendMessage("§c§l> §fNenhum servidor de §c§lPVP§f encontrado!");
                    player.sendMessage("");
            break;
        }
        case "Lobby": {
            event.setCancelled(true);
            
            if (!searchServer(player, proxiedPlayer, ServerType.LOBBY)) {
                player.sendMessage("");
                player.sendMessage("§c§l> §fNenhum servidor de §c§l" + ServerType.LOBBY.getServerName() + "§f encontrado!");
                player.sendMessage("");
            }
            
            break;
        }
        default:
            break;
    }
    }
    
    public boolean searchServer(Member player, ProxiedPlayer proxiedPlayer, ServerType serverType) {
        BattleServer server = manager.getBalancer(serverType).next();
        
        if (server != null && server.getServerInfo() != null) {
            if (!server.isFull() || (server.isFull() && player.hasGroupPermission(Group.SAINT))) {
                player.sendMessage("");
                player.sendMessage("§a§l> §fConectando-se ao servidor §a" + server.getServerId().toLowerCase() + "§f!");
                player.sendMessage("");
                proxiedPlayer.connect(server.getServerInfo());
                return true;
            }
        }
        
        return false;
    }

}
