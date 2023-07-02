package br.com.saintmc.hungergames;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import br.com.saintmc.hungergames.controller.TeamManager;
import br.com.saintmc.hungergames.controller.VarManager;
import br.com.saintmc.hungergames.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.Game;
import br.com.saintmc.hungergames.listener.register.BorderListener;
import br.com.saintmc.hungergames.listener.register.GamerListener;
import br.com.saintmc.hungergames.listener.register.KitListener;
import br.com.saintmc.hungergames.listener.register.RestoreListener;
import br.com.saintmc.hungergames.listener.register.ScoreboardListener;
import br.com.saintmc.hungergames.listener.register.UpdateListener;
import br.com.saintmc.hungergames.scheduler.SchedulerListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.tag.TagWrapper;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

@Getter
public class GameMain extends JavaPlugin {

    public static final Game GAME = new Game(0, 90);

    public static final Map<Group, List<String>> KITROTATE;

    public static final Tag WINNER = TagWrapper.create("WINNER", "§2§lWINNER§2", null, 24).setCustom(true);
    public static final Tag CHAMPION = TagWrapper.create("CHAMPION", "§6§lCHAMPION§6", null, 24).setCustom(true);

    static {
        KITROTATE = new HashMap<>();
        KITROTATE.put(Group.MEMBRO,
                      Arrays.asList("surprise", "lumberjack", "miner", "lumberjack", "reaper", "magma", "kaya",
                                    "endermage", "worm"));
        KITROTATE.put(Group.VIP,
                      Arrays.asList("snail", "thor", "anchor", "ninja", "stomper", "grappler", "kangaroo", "boxer",
                                    "ironman", "gladiator", "endermage", "ultimato"));
        KITROTATE.put(Group.VIP, Arrays.asList("turtle", "viper", "viking", "tank", "specialist", "poseidon"));
    }

    @Getter
    private static GameMain instance;

    private GameGeneral general;
    private String roomId;

    private VarManager varManager;
    private TeamManager teamManager;

    private YamlConfiguration config;


    @Override
    public void onLoad() {
        instance = this;

        File conf = new File(getDataFolder() + File.separator + "config.yml");

        if (!conf.exists()) {
            saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(conf);


        general = new GameGeneral();
        general.onLoad();

        super.onLoad();
    }

    @Override
    public void onEnable() {
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
            public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
                if (event.getLoginResult() != Result.ALLOWED) {
                    return;
                }

                if (!ServerConfig.getInstance().isJoinEnabled()) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                   GameGeneral.getInstance().getGameState().isPregame() ?
                                   "§cO servidor está carregando!" :
                                   "§cO servidor não está permitindo que jogadores entre no momento!");
                }
            }
        };

        varManager = new VarManager();
        teamManager = new TeamManager();

        Bukkit.getPluginManager().registerEvents(listener, getInstance());
        BukkitCommandFramework.INSTANCE.loadCommands(this.getClass(), "br.com.saintmc.hungergames.command");
        BukkitMain.getInstance().setRemovePlayerDat(false);

        if (CommonGeneral.getInstance().getServerType() == ServerType.EVENTO) {
            TagWrapper.registerTag(CHAMPION);
            BukkitMain.getInstance().getServerConfig().setWhitelist(true);
        } else {
            TagWrapper.registerTag(WINNER);
        }

        if (roomId == null) {
            String[] split = CommonGeneral.getInstance().getServerId().split("\\.");

            if (split.length > 1) {
                roomId = split[0].toUpperCase();
            } else {
                roomId = CommonGeneral.getInstance().getServerId();
            }
        }

        GameMain.getInstance().getVarManager().setVar("doublekit", roomId.startsWith("A"));

        loadListener();
        saveResource("cake.png", true);
        general.onEnable();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {

            public void run() {
                CommonGeneral.getInstance().debug("[World] Initializing the world configuration!");
                World world = getServer().getWorld("world");
                world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0) + 5, 0);

                world.setAutoSave(false);
                ((CraftWorld) world).getHandle().savingDisabled = true;

                CommonGeneral.getInstance().debug("[World] Loading the chunks!");

                long pid = getPID();
                long time = System.currentTimeMillis();

//                try {
//                    for (int x = 0; x <= 28; x++) {
//                        for (int z = 0; z <= 28; z++) {
//                            world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
//                            world.getSpawnLocation().clone().add(x * -16, 0, z * -16).getChunk().load();
//                            world.getSpawnLocation().clone().add(x * 16, 0, z * -16).getChunk().load();
//                            world.getSpawnLocation().clone().add(x * -16, 0, z * 16).getChunk().load();
//                        }
//
//                        if (x % 2 == 0) {
//                            CommonGeneral.getInstance().debug("[World] " + StringUtils.formatTime(
//                                    (int) ((System.currentTimeMillis() - time) / 1000)) + " have passed! PID: " + pid +
//                                                              " - used mem: " + ((Runtime.getRuntime().totalMemory() -
//                                                                                  Runtime.getRuntime().freeMemory()) /
//                                                                                 2L / 1048576L));
//                        }
//                    }
//                } catch (OutOfMemoryError ex) {
//
//                }

                CommonGeneral.getInstance().debug("[World] All chunks has been loaded!");

                world.setDifficulty(Difficulty.NORMAL);

                if (world.hasStorm()) {
                    world.setStorm(false);
                }

                world.setTime(0L);
                world.setWeatherDuration(999999999);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("announceAdvancements", "false");
                org.bukkit.WorldBorder border = world.getWorldBorder();
                border.setCenter(0, 0);
                border.setSize(800);

                CommonGeneral.getInstance().debug("[World] World has been loaded!");

                for (Entity e : world.getEntities())
                    e.remove();

                CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING, 300);
                ServerConfig.getInstance().setJoinEnabled(true);
                HandlerList.unregisterAll(listener);

                next();
            }
        });

        super.onEnable();
    }

    public static int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayFromString(String string) {
        switch (string.toLowerCase()) {
        case "sunday":
            return 1;
        case "monday":
            return 2;
        case "tuesday":
            return 3;
        case "wednesday":
            return 4;
        case "thursday":
            return 5;
        case "friday":
            return 6;
        case "saturday":
            return 7;
        default:
            return 0;
        }
    }

    public static String getDay(int day) {
        switch (day) {
        case 1:
            return "sunday";
        case 2:
            return "monday";
        case 3:
            return "tuesday";
        case 4:
            return "wednesday";
        case 5:
            return "thursday";
        case 6:
            return "friday";
        case 7:
            return "saturday";
        default:
            return "unknown";
        }
    }

    public void next() {
        Map<Integer, List<String>> map = new HashMap<>();

        for (String calendar : getConfig().getConfigurationSection("calendar").getKeys(false)) {
            if (getDayFromString(calendar) == 0) continue;

            for (String key : getConfig().getConfigurationSection("calendar." + calendar).getKeys(false)) {
                map.computeIfAbsent(getDayFromString(calendar), k -> new ArrayList<>()).add(key);
            }
        }

        Map.Entry<Long, String> entry = getTime(map, getDayNumberOld(new Date()), false);

        long time = entry.getKey();

        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                       getConfig().getString("calendar." + entry.getValue()));
            }
        }.runTaskLater(this, (time - System.currentTimeMillis()) / 50L);

        Bukkit.broadcastMessage(DateUtils.formatDifference((time - System.currentTimeMillis()) / 1000L));
    }

    private Map.Entry<Long, String> getTime(Map<Integer, List<String>> map, int day, boolean nextDay) {
        Calendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(System.currentTimeMillis() + (nextDay ? 86400000L : 0L));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long next = 0L;
        String total = "";
        long currentTime = System.currentTimeMillis();

        for (String time : map.get(day)) {
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            long current = calendar.getTimeInMillis() + (1000L * 60L * 60L * hour) + (1000L * 60L * minute);

            if (current - currentTime < 0) {
                continue;
            }

            if (next == 0L) {
                next = current;
                total = getDay(day).toUpperCase() + "." + time;
                continue;
            }

            if (currentTime - current > currentTime - next) {
                next = current;
                total = getDay(day).toUpperCase() + "." + time;
            }
        }

        if (next < System.currentTimeMillis()) {
            return getTime(map, day + 1, true);
        }

        return new AbstractMap.SimpleEntry<>(next, total);
    }


    @Override
    public void onDisable() {
        general.onDisable();
        super.onDisable();
    }

    /**
     * Get all alive players in the game
     *
     * @return
     */

    public List<Gamer> getAlivePlayers() {
        return getGeneral().getGamerController().getGamers().stream().filter(Gamer::isPlaying)
                           .collect(Collectors.toList());
    }

    /**
     * Get all alive teams in the game
     *
     * @return
     */

    public List<Team> getAliveTeams() {
        return getTeamManager().getTeams().stream().filter(Team::isAlive).collect(Collectors.toList());
    }

    public boolean isTeamEnabled() {
        return getVarManager().getVar(GameConst.TEAM_HG_VAR, false);
    }

    public int getMaxPlayersPerTeam() {
        return getVarManager().getVar(GameConst.MAX_PLAYERS_PER_TEAM_VAR, 2);
    }

    public void loadListener() {
        Bukkit.getPluginManager().registerEvents(new UpdateListener(), this);
        Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
        Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getPluginManager().registerEvents(new KitListener(), this);
        Bukkit.getPluginManager().registerEvents(new RestoreListener(), this);
        Bukkit.getPluginManager().registerEvents(new SchedulerListener(getGeneral()), this);
    }

    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getInstance());
    }

    public void sendPlayerToHungerGames(Player p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Hungergames");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }

    public static GameMain getPlugin() {
        return instance;
    }

    /**
     * Check if the player has won the last game and apply the tag
     *
     * @param gamer
     */

    public void checkWinner(Gamer gamer) {
        BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                          .getMember(gamer.getUniqueId());

        Tag verifyTag = CommonGeneral.getInstance().getServerType() == ServerType.EVENTO ? CHAMPION : WINNER;

        if (member.getTags().contains(verifyTag)) {
            gamer.setWinner(true);
            member.removePermission("tag." + verifyTag.getName().toLowerCase());

            member.sendMessage("§aVocê ganhou a tag " + verifyTag.getPrefix() + "§a " +
                               (verifyTag == CHAMPION ? "por ter ganhado o ultimo evento que participou" :
                                "por ter ganhado a ultima partida") + "!");
            member.sendMessage("§aVocê terá todos os kits nessa partida!");
            member.sendMessage("§cCaso você saia da partida, você perderá suas vantagens!");

            new BukkitRunnable() {

                @Override
                public void run() {
                    member.setTag(verifyTag);
                }
            }.runTaskLater(GameMain.getInstance(), 20l);
        }
    }

    public boolean isDoubleKit() {
        return getVarManager().getVar(GameConst.DOUBLEKIT, false);
    }
}
