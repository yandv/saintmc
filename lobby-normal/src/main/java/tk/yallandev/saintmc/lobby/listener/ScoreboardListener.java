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
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.bukkit.event.server.PlayerChangeEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyPlatform;

public class ScoreboardListener implements Listener {

    public static final Scoreboard DEFAULT_SCOREBOARD;

    static {
        DEFAULT_SCOREBOARD = new SimpleScoreboard("§b§lPENTA");

        DEFAULT_SCOREBOARD.blankLine(7);
        DEFAULT_SCOREBOARD.setScore(6, new Score("Rank: §7Membro", "group"));
        DEFAULT_SCOREBOARD.blankLine(5);
        DEFAULT_SCOREBOARD.setScore(4, new Score("Lobby: §7#1", "lobby"));
        DEFAULT_SCOREBOARD.setScore(3, new Score("Jogadores: §b0", "online"));
        DEFAULT_SCOREBOARD.blankLine(2);
        DEFAULT_SCOREBOARD.setScore(1, new Score("§awww." + CommonConst.SITE, "site"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleScoreboard(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerChangeEvent event) {
        DEFAULT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + event.getTotalMembers(), "online"));
    }

    @EventHandler
    public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Group group = event.getGroup();

                DEFAULT_SCOREBOARD.updateScore(event.getPlayer(), new Score(
                        "Rank: " + (group == Group.MEMBRO ? "§7Membro" : Tag.valueOf(group.name()).getStrippedTag()),
                        "group"));
            }
        }.runTaskLater(LobbyPlatform.getInstance(), 10l);
    }

    @EventHandler
    public void onPlayerScoreboardState(PlayerScoreboardStateEvent event) {
        if (event.isScoreboardEnabled()) {
            handleScoreboard(event.getPlayer());
        }
    }

    private void handleScoreboard(Player player) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

        if (member == null) {
            player.kickPlayer("§cSua conta não foi carregada!");
            return;
        }

        DEFAULT_SCOREBOARD.createScoreboard(player);

        Group group = member.getGroup();
        League league = member.getLeague();

        DEFAULT_SCOREBOARD.updateScore(player, new Score(
                "Rank: " + (group == Group.MEMBRO ? "§7Membro" : Tag.valueOf(group.name()).getStrippedTag()), "group"));
    }
}
