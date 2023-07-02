package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.tablist.Tablist;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyPlatform;

public class TabListener implements Listener {

    private Tablist tablist;

    public TabListener() {
        tablist = new Tablist("§f\n§b§l" + CommonConst.SERVER_NAME.toUpperCase() + "\n§f",
                              "\n§bLoja: §f" + CommonConst.SITE + "\n§bDiscord: §f" +
                              CommonConst.DISCORD.replace("http://", "") + "\n§f ") {

            @Override
            public String[] replace(Player player, String header, String footer) {
                Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                header = header.replace("%group%", member.getGroup() == Group.MEMBRO ? "§7§lMEMBRO" :
                                                   Tag.valueOf(member.getGroup().name()).getPrefix());
                header = header.replace("%name%", member.getPlayerName());

                footer = footer.replace("%name%", member.getPlayerName());
                footer = footer.replace(".br/", "");

                return new String[]{header, footer};
            }
        };
    }

    @EventHandler
    public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                tablist.updateTab(event.getPlayer());
            }
        }.runTaskLater(LobbyPlatform.getInstance(), 10l);
    }

    @EventHandler
    public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                tablist.updateTab(event.getPlayer());
            }
        }.runTaskLater(LobbyPlatform.getInstance(), 10l);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tablist.addViewer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        tablist.removeViewer(e.getPlayer());
    }
}
