package br.com.saintmc.hungergames.listener.register;

import br.com.saintmc.hungergames.event.VarChangeEvent;
import br.com.saintmc.hungergames.event.team.TeamPlayerJoinEvent;
import br.com.saintmc.hungergames.event.team.TeamPlayerLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.game.GameTimeEvent;
import br.com.saintmc.hungergames.event.kit.PlayerSelectedKitEvent;
import br.com.saintmc.hungergames.event.player.PlayerTimeoutEvent;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class ScoreboardListener extends GameListener {

    private static final Scoreboard SCOREBOARD;

    static {
        SCOREBOARD = new SimpleScoreboard(
                GameMain.getInstance().getVarManager().getVar("scoreboard-name", "§b§lCOMPETITIVO"));

        createScore();
    }

    @EventHandler
    public void onPlayerScoreboardState(PlayerScoreboardStateEvent event) {
        if (event.isScoreboardEnabled()) {
            Player player = event.getPlayer();

            if (player != null) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            return;
                        }

                        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
                        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                        SCOREBOARD.updateScore(player, new Score("Jogadores: §7" + (isPregame() ?
                                                                                    getGameGeneral().getPlayersInGame() +
                                                                                    "/" + Bukkit.getMaxPlayers() :
                                                                                    getGameGeneral().getPlayersInGame()),
                                                                 "players"));
                        SCOREBOARD.updateScore(player, new Score(
                                "Ranking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)",
                                "ranking"));
                        SCOREBOARD.updateScore(player, new Score("Kills: §e" + gamer.getMatchKills(), "kills"));
                        if (GameMain.getInstance().getVarManager().getVar("doublekit", false)) {
                            SCOREBOARD.updateScore(player, new Score(
                                    "Kit 1: §e" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
                        } else {
                            SCOREBOARD.updateScore(player, new Score(
                                    "Kit: §e" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
                        }
                        SCOREBOARD.updateScore(player, new Score(
                                "Kit 2: §e" + NameUtils.formatString(gamer.getKitName(KitType.SECONDARY)), "kit2"));
                    }
                }.runTaskLater(GameMain.getInstance(), 7l);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createScoreboard(player);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

                SCOREBOARD.updateScore(player, new Score(
                        "Ranking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)",
                        "ranking"));
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));

                Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

                if (GameMain.getInstance().getVarManager().getVar("doublekit", false)) {
                    SCOREBOARD.updateScore(player, new Score(
                            "Kit 1: §e" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
                } else {
                    SCOREBOARD.updateScore(player, new Score(
                            "Kit: §e" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
                }

                SCOREBOARD.updateScore(player, new Score(
                        "Kit 2: §e" + NameUtils.formatString(gamer.getKitName(KitType.SECONDARY)), "kit2"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();

        new BukkitRunnable() {

            @Override
            public void run() {
                if (player != null) {
                    Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
                    SCOREBOARD.updateScore(player, new Score("Kills: §e" + gamer.getMatchKills(), "kills"));
                }

                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler
    public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
        if (event.getKit() == null) {
            return;
        }

        Player player = event.getPlayer();

        if (GameMain.getInstance().getVarManager().getVar("doublekit", false)) {
            if (event.getKitType() == KitType.PRIMARY) {
                SCOREBOARD.updateScore(player, new Score("Kit 1: §e" + NameUtils.formatString(event.getKit().getName()),
                                                         "kit1"));
            } else {
                SCOREBOARD.updateScore(player, new Score("Kit 2: §e" + NameUtils.formatString(event.getKit().getName()),
                                                         "kit2"));
            }
        } else {
            if (event.getKitType() == KitType.PRIMARY) {
                SCOREBOARD.updateScore(player,
                                       new Score("Kit: §e" + NameUtils.formatString(event.getKit().getName()), "kit1"));
            } else {
                SCOREBOARD.updateScore(player, new Score("Kit 2: §e" + NameUtils.formatString(event.getKit().getName()),
                                                         "kit2"));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdminMode(PlayerAdminModeEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler
    public void onTeam(TeamPlayerJoinEvent event) {
        if (GameMain.getInstance().getMaxPlayersPerTeam() == 2) {
            event.getTeam().getParticipantsAsPlayer().forEach(player -> {
                Player team = event.getTeam().getParticipantsAsPlayer().stream().filter(p -> !p.equals(player))
                                   .findFirst().orElse(null);

                if (team == null) {
                    SCOREBOARD.updateScore(player, new Score("§aNinguém", "team"));
                } else {
                    SCOREBOARD.updateScore(player, new Score("§a" + team.getName(), "team"));
                }
            });
        }
    }

    @EventHandler
    public void onTeam(TeamPlayerLeaveEvent event) {
        if (GameMain.getInstance().getMaxPlayersPerTeam() == 2) {
            event.getTeam().getParticipantsAsPlayer().forEach(player -> {
                Player team = event.getTeam().getParticipantsAsPlayer().stream().filter(p -> !p.equals(player))
                                   .findFirst().orElse(null);

                if (team == null) {
                    SCOREBOARD.updateScore(player, new Score("§aNinguém", "team"));
                } else {
                    SCOREBOARD.updateScore(player, new Score("§a" + team.getName(), "team"));
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTimeout(PlayerTimeoutEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                SCOREBOARD.updateScore(new Score("Jogadores: §7" + (isPregame() ?
                                                                    getGameGeneral().getPlayersInGame() + "/" +
                                                                    Bukkit.getMaxPlayers() :
                                                                    getGameGeneral().getPlayersInGame()), "players"));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);
    }

    @EventHandler
    public void onGameStage(GameTimeEvent event) {
        String str = "Iniciando em: §7";

        switch (getGameGeneral().getGameState()) {
        case WAITING: {
            if (ServerConfig.getInstance().isTimeInWaiting()) {
                str = "Aguardando: §7";
            }
            break;
        }
        case WINNING:
        case GAMETIME:
            str = "Tempo: §7";
            break;
        case INVINCIBILITY:
            str = "Invencivel por: §7";
            break;
        default:
            break;
        }

        SCOREBOARD.updateScore(new Score(str + StringUtils.format(getGameGeneral().getTime()), "time"));
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        SCOREBOARD.clear();

        createScore();

        for (Player player : Bukkit.getOnlinePlayers()) {
            SCOREBOARD.createScoreboard(player);

            Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

            if (GameMain.getInstance().isDoubleKit()) {
                SCOREBOARD.updateScore(player, new Score(
                        "Kit 1: §e" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
            } else {
                SCOREBOARD.updateScore(player,
                                       new Score("Kit: §e" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)),
                                                 "kit1"));
            }
            SCOREBOARD.updateScore(player,
                                   new Score("Kit 2: §e" + NameUtils.formatString(gamer.getKitName(KitType.SECONDARY)),
                                             "kit2"));
        }

        SCOREBOARD.updateScore(new Score("Jogadores: §7" + getGameGeneral().getPlayersInGame(), "players"));
    }

    @EventHandler
    public void onScoreboardTitleChange(VarChangeEvent event) {
        if (event.getVarName().equals("scoreboard-name")) {
            SCOREBOARD.setDisplayName(event.getNewValue());
        }
    }

    public void createScoreboard(Player player) {
        SCOREBOARD.createScoreboard(player);
    }

    private static void createScore() {
        SCOREBOARD.clear();

        if (GameGeneral.getInstance().getGameState().isPregame()) {
            if (GameMain.getInstance().isDoubleKit()) {
                SCOREBOARD.blankLine(10);
                SCOREBOARD.setScore(9, new Score("Iniciando em: §75:00", "time"));
                SCOREBOARD.setScore(8, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(7);
                SCOREBOARD.setScore(6, new Score("Kit 1: §eNenhum", "kit1"));
                SCOREBOARD.setScore(5, new Score("Kit 2: §eNenhum", "kit2"));
                SCOREBOARD.blankLine(4);
                SCOREBOARD.setScore(3, new Score("Ranking: §7(§f-§7)", "ranking"));
                SCOREBOARD.blankLine(2);
                SCOREBOARD.setScore(1, new Score("§e" + CommonConst.SITE, "site"));
            } else {
                SCOREBOARD.blankLine(9);
                SCOREBOARD.setScore(8, new Score("Iniciando em: §75:00", "time"));
                SCOREBOARD.setScore(7, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(6);
                SCOREBOARD.setScore(5, new Score("Kit: §eNenhum", "kit1"));
                SCOREBOARD.blankLine(4);
                SCOREBOARD.setScore(3, new Score("Ranking: §7(§f-§7)", "ranking"));
                SCOREBOARD.blankLine(2);
            }
        } else {
            if (GameMain.getInstance().isDoubleKit()) {
                SCOREBOARD.blankLine(9);
                SCOREBOARD.setScore(8, new Score("Invencivel por: §7", "time"));
                SCOREBOARD.setScore(7, new Score("Jogadores: §70/80", "players"));
                SCOREBOARD.blankLine(6);
                SCOREBOARD.setScore(5, new Score("Kit 1: §eNenhum", "kit1"));
                SCOREBOARD.setScore(4, new Score("Kit 2: §eNenhum", "kit2"));
                SCOREBOARD.setScore(3, new Score("Kills: §e0", "kills"));
                SCOREBOARD.blankLine(2);
            } else {
                SCOREBOARD.blankLine(8);
                SCOREBOARD.setScore(7, new Score("Invencivel por: §7", "time"));
                SCOREBOARD.setScore(6, new Score("Jogadores: §780", "players"));
                SCOREBOARD.blankLine(5);
                SCOREBOARD.setScore(4, new Score("Kit: §eNenhum", "kit1"));
                SCOREBOARD.setScore(3, new Score("Kills: §e0", "kills"));
                SCOREBOARD.blankLine(2);
            }
        }

        SCOREBOARD.setScore(1, new Score("§awww." + CommonConst.SITE, "site"));
    }
}
