package tk.yallandev.saintmc.bungee.listener;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.checkerframework.checker.units.qual.C;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.ban.Category;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

public class MessageListener implements Listener {

    private ServerManager manager;

    public MessageListener(ServerManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();

        if (event.getTag().equalsIgnoreCase("WDL|INIT") || (event.getTag().equalsIgnoreCase("PERMISSIONSREPL") &&
                                                            (new String(event.getData())).contains(
                                                                    "mod.worlddownloader"))) {
            proxiedPlayer.disconnect("§cConexão cancelada!");
            return;
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        if (!(event.getSender() instanceof Server)) {
            return;
        }

        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getReceiver();
        Member player = CommonGeneral.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();

        switch (subChannel) {
        case "SearchServer": {

            String server = in.readUTF();

            if (server.contains("-")) {

                String[] split = server.split("-");

                for (String s : split) {
                    try {
                        ServerType serverType = ServerType.valueOf(s);

                        if (!searchServer(player, proxiedPlayer, serverType)) {
                            return;
                        }
                    } catch (Exception ex) {
                    }
                }

                player.sendMessage("§cNenhum servidor encontrado!");
            } else {
                ServerType serverType = null;

                try {
                    serverType = ServerType.valueOf(server);
                } catch (Exception ex) {
                    return;
                }

                if (!searchServer(player, proxiedPlayer, serverType)) {
                    player.sendMessage("§cNenhum servidor encontrado!");
                }
            }
            break;
        }
        case "HandleBan": {
            BungeeMain.getInstance().getPunishManager().ban(player, new Ban(Category.CHEATING, player.getUniqueId(),
                                                                            player.getPlayerName(), "CONSOLE",
                                                                            UUID.randomUUID(), "Autoban - Cheating",
                                                                            -1));
            break;
        }
        case "1v1": {
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.ONEXONE)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }
            break;
        }
        case "Hungergames":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.HUNGERGAMES)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "Event":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.EVENTO)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "PVPFulliron":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.FULLIRON)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "PVPSimulator":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.SIMULATOR)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "PVP":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.FULLIRON)) {
                if (!searchServer(player, proxiedPlayer, ServerType.SIMULATOR)) {
                    player.sendMessage("§cNenhum servidor encontrado!");
                }
            }

            break;
        case "Gladiator":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.GLADIATOR)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "SWSolo":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.SW_SOLO)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "SWTeam":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.SW_TEAM)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "SWSquad":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.SW_SQUAD)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "Lobby":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer,
                              manager.getServer(proxiedPlayer.getServer().getInfo().getName()).getServerType()
                                     .getServerLobby())) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "LobbyHG":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.LOBBY_HG)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "LobbySW":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.LOBBY_SKYWARS)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        case "LobbyBW":
            event.setCancelled(true);

            if (!searchServer(player, proxiedPlayer, ServerType.LOBBY_BEDWARS)) {
                player.sendMessage("§cNenhum servidor encontrado!");
            }

            break;
        default:
            break;
        }
    }

    public boolean searchServer(Member player, ProxiedPlayer proxiedPlayer, ServerType serverType) {
        ProxiedServer server = manager.getBalancer(serverType).next();

        if (server == null || server.getServerInfo() == null) {
            return false;
        }

        if (server.isFull() && !player.hasGroupPermission(Group.PENTA)) {
            proxiedPlayer.sendMessage("§cO servidor está cheio!");
            return true;
        }

        if (!server.canBeSelected() && !player.hasGroupPermission(Group.TRIAL)) {
            proxiedPlayer.sendMessage("§cO servidor não está disponível para membros!");
            return true;
        }

        proxiedPlayer.connect(server.getServerInfo());
        return true;
    }
}
