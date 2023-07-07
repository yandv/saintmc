package tk.yallandev.saintmc.gladiator.listener;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.combat.CombatStatus;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.gladiator.GameMain;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;
import tk.yallandev.saintmc.gladiator.event.GladiatorFinishEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorPulseEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorSpectatorEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorSpectatorEvent.Action;
import tk.yallandev.saintmc.gladiator.event.GladiatorStartEvent;

public class ScoreboardListener implements Listener {

    private static final Scoreboard SCOREBOARD = new SimpleScoreboard("§b§lGLADIATOR");
    private static final Scoreboard FIGHT_SCOREBOARD = new SimpleScoreboard("§b§lGLADIATOR");

    {
        SCOREBOARD.blankLine(9);
        SCOREBOARD.setScore(8, new Score("Vitórias: 0", "wins"));
        SCOREBOARD.setScore(7, new Score("Derrotas: 0", "loses"));
        SCOREBOARD.blankLine(6);
        SCOREBOARD.setScore(5, new Score("Winstreak: 0", "winstreak"));
        SCOREBOARD.setScore(4, new Score("Elo: §a1000", "elo"));
        SCOREBOARD.blankLine(3);
        SCOREBOARD.setScore(2, new Score("Jogadores: 0", "players"));
        SCOREBOARD.blankLine(1);
        SCOREBOARD.setScore(0, new Score("§awww." + CommonConst.SITE, "site"));

        FIGHT_SCOREBOARD.blankLine(10);
        FIGHT_SCOREBOARD.setScore(9, new Score("Tempo: §a", "time"));
        FIGHT_SCOREBOARD.setScore(8, new Score("Winstreak: 0", "winstreak"));
        FIGHT_SCOREBOARD.blankLine(7);
        FIGHT_SCOREBOARD.setScore(6, new Score("§9Ninguém: §e0ms", "firstPing"));
        FIGHT_SCOREBOARD.setScore(5, new Score("§cNinguém: §e0ms", "secondPing"));
        FIGHT_SCOREBOARD.blankLine(4);
        FIGHT_SCOREBOARD.setScore(3, new Score("Modo: §aGladiator", "modo"));
        FIGHT_SCOREBOARD.setScore(2, new Score("Elo: §a1000", "elo"));
        FIGHT_SCOREBOARD.blankLine(1);
        FIGHT_SCOREBOARD.setScore(0, new Score("§e" + CommonConst.SITE, "site"));
    }

    @EventHandler
    public void onPlayerWarpJoin(PlayerScoreboardStateEvent event) {
        if (event.isScoreboardEnabled()) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    loadScoreboard(event.getPlayer());
                }
            }.runTaskLater(GameMain.getInstance(), 5L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                loadScoreboard(event.getPlayer());
            }
        }.runTaskLater(GameMain.getInstance(), 7L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                            .getMember(event.getPlayer().getUniqueId()));
        FIGHT_SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                                  .getMember(event.getPlayer().getUniqueId()));

        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
                FIGHT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7L);
    }

    @EventHandler
    public void onGladiatorFinish(GladiatorStartEvent event) {
        Player player = event.getChallenge().getPlayer();
        Player enimy = event.getChallenge().getEnimy();

        SCOREBOARD.removeViewer(
                (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

        SCOREBOARD.removeViewer(
                (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(enimy.getUniqueId()));

        FIGHT_SCOREBOARD.createScoreboard(player);
        FIGHT_SCOREBOARD.createScoreboard(enimy);

        updateScore(player, event.getChallenge());
        updateScore(enimy, event.getChallenge());
    }

    @EventHandler
    public void onGladiatorSpectator(GladiatorSpectatorEvent event) {
        if (event.getAction() == Action.JOIN) {
            SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                                .getMember(event.getPlayer().getUniqueId()));

            FIGHT_SCOREBOARD.createScoreboard(event.getPlayer());
            updateScore(event.getPlayer(), event.getChallenge());
        } else {
            loadScoreboard(event.getPlayer());
        }
    }

    @EventHandler
    public void onGladiatorPulse(GladiatorPulseEvent event) {
        updateScore(event.getChallenge().getEnimy(), event.getChallenge());
        updateScore(event.getChallenge().getPlayer(), event.getChallenge());

        event.getChallenge().getSpectatorSet().forEach(player -> updateScore(player, event.getChallenge()));
    }

    @EventHandler
    public void onGladiatorFinish(GladiatorFinishEvent event) {
        loadScoreboard(event.getChallenge().getEnimy());
        loadScoreboard(event.getChallenge().getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWarpDeath(GladiatorFinishEvent event) {
        boolean updatePlayer = true;
        boolean updateKiller = event.getWinner() != null;

        Player player = event.getLoser();
        updateState(player);

        if (updateKiller) {
            Player killer = event.getWinner();
            updateState(killer);
        }
    }

    private void updateState(Player killer) {
        CombatStatus killerStatus = CommonGeneral.getInstance().getStatusManager()
                                                 .loadStatus(killer.getUniqueId(), StatusType.GLADIATOR,
                                                             CombatStatus.class);

        SCOREBOARD.updateScore(killer, new Score("Vitórias: §7" + killerStatus.getKills(), "wins"));
        SCOREBOARD.updateScore(killer, new Score("Derrotas: §7" + killerStatus.getDeaths(), "loses"));
        SCOREBOARD.updateScore(killer, new Score("Winstreak: §a" + killerStatus.getKillstreak(), "winstreak"));

        SCOREBOARD.updateScore(killer, new Score("Elo: §a" + killerStatus.getElo(), "elo"));

        FIGHT_SCOREBOARD.updateScore(killer, new Score("Winstreak: §a" + killerStatus.getKillstreak(), "winstreak"));
        FIGHT_SCOREBOARD.updateScore(killer, new Score("Elo: §a" + killerStatus.getElo(), "elo"));
    }

    public void loadScoreboard(Player player) {
        FIGHT_SCOREBOARD.removeViewer(
                (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

        SCOREBOARD.createScoreboard(player);
        updateScore(player);
    }

    public void updateScore(Player player) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

        updateState(player);

        SCOREBOARD.updateScore(player, new Score("Money: §6" + member.getMoney(), "coins"));
        SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
    }

    private void updateScore(Player player, Challenge challenge) {
        Player enimy = challenge.getPlayer() == player ? challenge.getEnimy() : challenge.getPlayer();
        Player target = challenge.getPlayer() == player ? challenge.getPlayer() : challenge.getEnimy();

        FIGHT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
        FIGHT_SCOREBOARD.updateScore(player, new Score("§9" + target.getName() + ": §e" +
                                                       (((CraftPlayer) target).getHandle().ping >= 1000 ? "1000+" :
                                                        ((CraftPlayer) target).getHandle().ping) + "ms", "firstPing"));
        FIGHT_SCOREBOARD.updateScore(player, new Score("§c" + enimy.getName() + ": §e" +
                                                       (((CraftPlayer) enimy).getHandle().ping >= 1000 ? "1000+" :
                                                        ((CraftPlayer) enimy).getHandle().ping) + "ms", "secondPing"));
        FIGHT_SCOREBOARD.updateScore(player, new Score("Tempo: §a" + StringUtils.format(challenge.getTime()), "time"));

        updateState(player);
    }
}
