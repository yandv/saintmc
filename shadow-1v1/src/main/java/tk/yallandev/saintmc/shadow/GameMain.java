package tk.yallandev.saintmc.shadow;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.hologram.ViewHandler;
import tk.yallandev.saintmc.bukkit.api.hologram.impl.CraftHologram;
import tk.yallandev.saintmc.bukkit.api.hologram.impl.TopRanking;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.listener.register.CombatListener;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.combat.CombatStatus;
import tk.yallandev.saintmc.shadow.command.DefaultCommand;
import tk.yallandev.saintmc.shadow.command.SpectatorCommand;
import tk.yallandev.saintmc.shadow.controller.GamerManager;
import tk.yallandev.saintmc.shadow.listener.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Testando nesse plugin um modelo aberto Onde cada classe tem sua função bem
 * definida e as outras não podem interferir
 *
 * @author Allan
 */

@Getter
public class GameMain extends JavaPlugin {

    @Getter
    private static GameMain instance;

    private GameGeneral gameGeneral;

    private GamerManager gamerManager;

    @Override
    public void onLoad() {
        instance = this;
        gameGeneral = new GameGeneral();

        gameGeneral.onLoad();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        loadListener();
        gameGeneral.onEnable();

        gamerManager = new GamerManager();

        BukkitCommandFramework.INSTANCE.registerCommands(new DefaultCommand());
        BukkitCommandFramework.INSTANCE.registerCommands(new SpectatorCommand());

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINS",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-wins")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.SHADOW, "kills",
                                                                                      CombatStatus.class);

                             for (CombatStatus wins : ranking) {
                                 Member member = CommonGeneral.getInstance().getPlayerData()
                                                              .loadMember(wins.getUniqueId(), MemberVoid.class);

                                 list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(),
                                                                        member.getServerGroup()));
                             }

                             return list;
                         }, (model, position) -> "§e" + position + ". " + (model == null ? "§7Ninguém" :
                                                                           model.getGroup().getColor() +
                                                                           model.getPlayerName()) + " §7- §e" +
                                                 (model == null ? 0 : model.getStatus().getKills()));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lRANKING", BukkitMain.getInstance().getLocationFromConfig(
                "topranking-hologram-ranking")), () -> {
            return new ArrayList<>(CommonGeneral.getInstance().getPlayerData().ranking("elo"));
        }, (model, position) -> "§e" + position + ". " +
                                (model == null ? "§7Ninguém" : model.getGroup().getColor() + model.getPlayerName()) +
                                " §7- §e" + (model == null ? 0 :
                                             model.getLeague().getColor() + model.getLeague().getSymbol() + " §e" +
                                             model.getXp() + " XP"));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINSTREAK", BukkitMain.getInstance().getLocationFromConfig(
                "topranking-hologram-winstreak")), () -> {

            List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
            Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                            .ranking(StatusType.SHADOW, "killstreak",
                                                                     CombatStatus.class);

            for (CombatStatus wins : ranking) {
                Member member = CommonGeneral.getInstance().getPlayerData()
                                             .loadMember(wins.getUniqueId(), MemberVoid.class);

                list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(), member.getServerGroup()));
            }

            return list;
        }, (model, position) -> "§e" + position + ". " +
                                (model == null ? "§7Ninguém" : model.getGroup().getColor() + model.getPlayerName()) +
                                " §7- §e" + (model == null ? 0 : model.getStatus().getKillstreak()));

        super.onEnable();
    }

    @Override
    public void onDisable() {

        gameGeneral.onDisable();

        super.onDisable();
    }

    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new ArenaListener(), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new StatusListener(), this);
        Bukkit.getPluginManager().registerEvents(new RankingListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
    }
}
