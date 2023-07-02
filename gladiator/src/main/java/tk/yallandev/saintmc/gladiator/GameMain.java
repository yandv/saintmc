package tk.yallandev.saintmc.gladiator;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.character.Character;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;
import tk.yallandev.saintmc.bukkit.api.hologram.ViewHandler;
import tk.yallandev.saintmc.bukkit.api.hologram.impl.CraftHologram;
import tk.yallandev.saintmc.bukkit.api.hologram.impl.TopRanking;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.listener.register.CombatListener;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.combat.CombatStatus;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.gladiator.command.DefaultCommand;
import tk.yallandev.saintmc.gladiator.command.SpectatorCommand;
import tk.yallandev.saintmc.gladiator.controller.GamerManager;
import tk.yallandev.saintmc.gladiator.listener.ArenaListener;
import tk.yallandev.saintmc.gladiator.listener.GladiatorListener;
import tk.yallandev.saintmc.gladiator.listener.PlayerListener;
import tk.yallandev.saintmc.gladiator.listener.RankingListener;
import tk.yallandev.saintmc.gladiator.listener.ScoreboardListener;
import tk.yallandev.saintmc.gladiator.listener.SpectatorListener;
import tk.yallandev.saintmc.gladiator.listener.StatusListener;
import tk.yallandev.saintmc.gladiator.listener.WorldListener;
import tk.yallandev.saintmc.gladiator.menu.EditInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

        BukkitMain.getInstance().createCharacter("§aEditar inventário", "yandv", "inv", new Character.Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                new EditInventory(player);
                return false;
            }
        }, new CraftHologram("§a",
                             new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0, 0, 0)).addLineBelow(
                "§aEditar inventário."));

        ViewHandler viewHandler = (h, player, text) -> {
            return (CommonConst.RANDOM.nextInt(100000) + 1) + "m/s²";
        };

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINS",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-wins")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.GLADIATOR, "kills",
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

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lELO",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-elo")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.GLADIATOR, "elo",
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
                                                 (model == null ? 0 : model.getStatus().getElo()));

        new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINSTREAK",
                                           BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-winstreak")),
                         () -> {

                             List<TopRanking.RankingModel<CombatStatus>> list = new ArrayList<>();
                             Collection<CombatStatus> ranking = CommonGeneral.getInstance().getStatusData()
                                                                             .ranking(StatusType.GLADIATOR,
                                                                                      "killstreak", CombatStatus.class);

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
                                                 (model == null ? 0 : model.getStatus().getKillstreak()));

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
