package tk.yallandev.saintmc.bukkit.command.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.worldedit.arena.ArenaType;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.clan.ClanInfo;
import tk.yallandev.saintmc.common.clan.enums.ClanHierarchy;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;

public class CompleterCommand implements CommandClass {

    /**
     * Different because permission check
     *
     * @since 1.2
     */

    @Command(name = "giftcode", aliases = {"resgatar", "codigo"}, groupToUse = Group.ADMIN)
    public void principalCommand(CommandArgs cmdArgs) {

    }

    @Command(name = "send", aliases = {"groupset", "setargrupo"}, groupToUse = Group.ADMIN)
    public void adminCommand(CommandArgs cmdArgs) {

    }

    @Command(name = "groupset", aliases = {"removevip", "tempgroup", "givevip", "removervip", "unban", "unmute",
                                           "glist", "broadcast", "setargroup"}, groupToUse = Group.ADMIN)
    public void managerCommand(CommandArgs cmdArgs) {

    }

    @Command(name = "fakelist", aliases = {"find"}, groupToUse = Group.MODPLUS)
    public void modgcCommand(CommandArgs cmdArgs) {

    }

    @Command(name = "ban", aliases = {"mute", "warn", "banir", "unban", "desbanir", "tempban", "tempmute", "send",
                                      "staffchat", "sc"}, groupToUse = Group.TRIAL)
    public void trialCommand(CommandArgs cmdArgs) {

    }

    @Command(name = "lobby", aliases = {"server", "connect", "ir", "go", "hub", "ping", "play", "clan"})
    public void memberCommand(CommandArgs cmdArgs) {

    }

    @Completer(name = "giftcode", aliases = {"resgatar", "codigo"})
    public List<String> giftcodeCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            List<String> argList = new ArrayList<>();

            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (String arg : Arrays.asList("criar"))
                    argList.add(arg);
            } else {
                for (String arg : Arrays.asList("criar"))
                    if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                        argList.add(arg);
                    }
            }

            return argList;
        } else if (cmdArgs.getArgs().length == 2) {
            List<String> argList = new ArrayList<>();

            if (cmdArgs.getArgs()[0].equalsIgnoreCase("criar")) {
                if (cmdArgs.getArgs()[1].isEmpty()) {
                    for (String arg : Arrays.asList("rank", "kit"))
                        argList.add(arg);
                } else {
                    for (String arg : Arrays.asList("rank", "kit"))
                        if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                            argList.add(arg);
                        }
                }
            } else {
                if (cmdArgs.getArgs()[1].isEmpty()) {
                    for (String arg : Arrays.asList("deletar"))
                        argList.add(arg);
                } else {
                    for (String arg : Arrays.asList("deletar"))
                        if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                            argList.add(arg);
                        }
                }
            }

            return argList;
        } else if (cmdArgs.getArgs().length == 3) {
            List<String> argList = new ArrayList<>();

            if (cmdArgs.getArgs()[1].equalsIgnoreCase("rank")) {
                if (cmdArgs.getArgs()[2].isEmpty()) {
                    for (RankType rankType : RankType.values())
                        argList.add(rankType.toString());
                } else {
                    for (RankType rankType : RankType.values())
                        if (rankType.toString().toLowerCase().startsWith(cmdArgs.getArgs()[2].toLowerCase())) {
                            argList.add(rankType.toString());
                        }
                }
            } else if (cmdArgs.getArgs()[1].equalsIgnoreCase("kit")) {
                if (cmdArgs.getArgs()[2].isEmpty()) {
                    for (String arg : Arrays.asList("kangaroo", "grappler"))
                        argList.add(arg);
                } else {
                    for (String arg : Arrays.asList("kangaroo", "grappler"))
                        if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[2].toLowerCase())) {
                            argList.add(arg);
                        }
                }
            }

            return argList;
        }

        return new ArrayList<>();
    }

    @Completer(name = "clan")
    public List<String> clanCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getSender() instanceof Member) {
            Member member = (Member) cmdArgs.getSender();

            if (cmdArgs.getArgs().length == 1) {
                List<String> argList = new ArrayList<>();
                List<String> avaiableArgs = Arrays.asList("criar", "apagar", "leave", "top", "kick", "setgroup",
                                                          "membros", "info", "join", "deny", "chat");

                if (cmdArgs.getArgs()[0].isEmpty()) {
                    for (String arg : avaiableArgs)
                        argList.add(arg);
                } else {
                    for (String arg : avaiableArgs)
                        if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                            argList.add(arg);
                        }
                }

                return argList;
            } else if (cmdArgs.getArgs().length == 2) {
                List<String> argList = new ArrayList<>();
                if (member.hasClan()) {
                    if (cmdArgs.getArgs()[0].equalsIgnoreCase("kick")) {
                        if (cmdArgs.getArgs()[1].isEmpty()) {
                            for (String arg : member.getClan().getMemberMap().values().stream()
                                                    .map(ClanInfo::getPlayerName).collect(Collectors.toList()))
                                argList.add(arg);
                        } else {
                            for (String arg : member.getClan().getMemberMap().values().stream()
                                                    .map(ClanInfo::getPlayerName).collect(Collectors.toList()))
                                if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                                    argList.add(arg);
                                }
                        }
                    } else if (cmdArgs.getArgs()[0].equalsIgnoreCase("invite")) {
                        if (cmdArgs.getArgs()[1].isEmpty()) {
                            for (String arg : getPlayerList(cmdArgs.getArgs()).stream()
                                                                              .filter(player -> !member.getClan()
                                                                                                       .isMember(
                                                                                                               player))
                                                                              .collect(Collectors.toList()))
                                argList.add(arg);
                        } else {
                            for (String arg : getPlayerList(cmdArgs.getArgs()).stream()
                                                                              .filter(player -> !member.getClan()
                                                                                                       .isMember(
                                                                                                               player))
                                                                              .collect(Collectors.toList()))
                                if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                                    argList.add(arg);
                                }
                        }
                    } else if (cmdArgs.getArgs()[0].equalsIgnoreCase("setgroup")) {
                        if (cmdArgs.getArgs()[1].isEmpty()) {
                            for (String arg : member.getClan().getMemberMap().values().stream()
                                                    .map(ClanInfo::getPlayerName).collect(Collectors.toList()))
                                argList.add(arg);
                        } else {
                            for (String arg : member.getClan().getMemberMap().values().stream()
                                                    .map(ClanInfo::getPlayerName).collect(Collectors.toList()))
                                if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                                    argList.add(arg);
                                }
                        }
                    }
                }

                return argList;
            } else if (cmdArgs.getArgs().length == 3) {
                List<String> argList = new ArrayList<>();

                if (cmdArgs.getArgs()[0].equalsIgnoreCase("setgroup")) {
                    if (cmdArgs.getArgs()[2].isEmpty()) {
                        for (ClanHierarchy clanHierarchy : ClanHierarchy.values())
                            argList.add(clanHierarchy.toString());
                    } else {
                        for (ClanHierarchy clanHierarchy : ClanHierarchy.values())
                            if (clanHierarchy.toString().toLowerCase().startsWith(cmdArgs.getArgs()[2].toLowerCase())) {
                                argList.add(clanHierarchy.toString());
                            }
                    }
                }

                return argList;
            }
        }

        return new ArrayList<>();
    }

    @Completer(name = "send")
    public List<String> sendCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            List<String> argList = new ArrayList<>();
            List<String> avaiableArg = Arrays.asList("current", "all");

            for (Player player : Bukkit.getOnlinePlayers())
                avaiableArg.add(player.getName());

            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (String arg : avaiableArg)
                    argList.add(arg);
            } else {
                for (String arg : avaiableArg)
                    if (arg.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                        argList.add(arg);
                    }
            }

            return argList;
        }

        return new ArrayList<>();
    }

    @Completer(name = "tempgroup", aliases = {"givevip"})
    public List<String> tempgroupCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 3) {
            List<String> rankList = new ArrayList<>();

            if (cmdArgs.getArgs()[2].isEmpty()) {
                for (RankType rankType : RankType.values())
                    rankList.add(rankType.toString());
            } else {
                for (RankType rankType : RankType.values())
                    if (rankType.toString().toLowerCase().startsWith(cmdArgs.getArgs()[2].toLowerCase())) {
                        rankList.add(rankType.toString());
                    }
            }

            return rankList;
        }

        return getPlayerList(cmdArgs.getArgs());
    }

    @Completer(name = "removevip", aliases = {"removervip"})
    public List<String> removervipCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 2) {
            List<String> rankList = new ArrayList<>();

            if (cmdArgs.getArgs()[1].isEmpty()) {
                for (RankType rankType : RankType.values())
                    rankList.add(rankType.toString());
            } else {
                for (RankType rankType : RankType.values())
                    if (rankType.toString().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                        rankList.add(rankType.toString());
                    }
            }

            return rankList;
        }

        return getPlayerList(cmdArgs.getArgs());
    }

    @Completer(name = "groupset", aliases = {"setargrupo"})
    public List<String> groupsetCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 2) {
            List<String> groupList = new ArrayList<>();

            if (cmdArgs.getArgs()[1].isEmpty()) {
                for (Group group : Group.values())
                    groupList.add(group.toString());
            } else {
                for (Group group : Group.values())
                    if (group.toString().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                        groupList.add(group.toString());
                    }
            }

            return groupList;
        }

        return getPlayerList(cmdArgs.getArgs());
    }

    @Completer(name = "tag")
    public List<String> tagCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            if (cmdArgs.getArgs().length == 1) {
                List<String> tagList = new ArrayList<>();
                BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                                  .getMember(cmdArgs.getSender().getUniqueId());

                if (cmdArgs.getArgs()[0].isEmpty()) {
                    for (Tag tag : Tag.values())
                        if (member.getTags().contains(tag)) {
                            tagList.add(tag.getName());
                        }
                } else {
                    for (Tag tag : Tag.values())
                        if (member.getTags().contains(tag)) {
                            if (tag.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                                tagList.add(tag.getName());
                            }
                        }
                }

                return tagList;
            }
        }

        return new ArrayList<>();
    }

    @Completer(name = "medal")
    public List<String> medalCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            if (cmdArgs.getArgs().length == 1) {
                List<String> medalList = new ArrayList<>();
                BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                                  .getMember(cmdArgs.getSender().getUniqueId());

                if (cmdArgs.getArgs()[0].isEmpty()) {
                    for (Medal medal : member.getMedalList())
                        if (medal != null && medal != Medal.NONE) {
                            medalList.add(medal.name());
                        }
                } else {
                    for (Medal medal : member.getMedalList())
                        if (medal != null && medal != Medal.NONE) {
                            if (medal.name().startsWith(cmdArgs.getArgs()[0].toUpperCase())) {
                                medalList.add(medal.name());
                            }
                        }
                }

                return medalList;
            }
        }

        return new ArrayList<>();
    }

    @Completer(name = "addmedal")
    public List<String> addmedalCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 2) {
            List<String> medalList = new ArrayList<>();

            if (cmdArgs.getArgs()[1].isEmpty()) {
                for (Medal medal : Medal.values())
                    medalList.add(medal.name());
            } else {
                for (Medal medal : Medal.values())
                    if (medal.name().startsWith(cmdArgs.getArgs()[1].toUpperCase())) {
                        medalList.add(medal.name());
                    }
            }

            return medalList;
        }

        return new ArrayList<>();
    }

    @Completer(name = "enchant")
    public List<String> enchantCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            List<String> enchantmentList = new ArrayList<>();

            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (Enchantment enchantment : Enchantment.values())
                    enchantmentList.add(enchantment.getName());
            } else {
                for (Enchantment enchantment : Enchantment.values())
                    if (enchantment.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                        enchantmentList.add(enchantment.getName());
                    }
            }

            return enchantmentList;
        }

        return new ArrayList<>();
    }

    @Completer(name = "effect")
    public List<String> effectCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 2) {
            List<String> effectList = new ArrayList<>();

            if (cmdArgs.getArgs()[1].isEmpty()) {
                for (PotionEffectType effect : PotionEffectType.values())
                    effectList.add(effect.getName());
            } else {
                for (PotionEffectType effect : PotionEffectType.values())
                    if (effect.getName().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                        effectList.add(effect.getName());
                    }
            }

            return effectList;
        }

        return getPlayerList(cmdArgs.getArgs());
    }

    @Completer(name = "set")
    public List<String> setCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            List<String> enchantmentList = new ArrayList<>();

            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (Material enchantment : Material.values())
                    enchantmentList.add(enchantment.name());
            } else {
                for (Material enchantment : Material.values())
                    if (enchantment.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                        enchantmentList.add(enchantment.name());
                    }
            }

            return enchantmentList;
        }

        return new ArrayList<>();
    }

    @Completer(name = "createarena")
    public List<String> createarenaCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            List<String> typeList = new ArrayList<>();

            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (ArenaType arenaType : ArenaType.values())
                    typeList.add(arenaType.name());
            } else {
                for (ArenaType arenaType : ArenaType.values())
                    if (arenaType.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                        typeList.add(arenaType.name());
                    }
            }

            return typeList;
        } else if (cmdArgs.getArgs().length == 2) {
            List<String> materialList = new ArrayList<>();

            if (cmdArgs.getArgs()[1].isEmpty()) {
                for (Material enchantment : Material.values())
                    materialList.add(enchantment.name());
            } else {
                for (Material enchantment : Material.values())
                    if (enchantment.name().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                        materialList.add(enchantment.name());
                    }
            }

            return materialList;
        }

        return new ArrayList<>();
    }

    public List<String> getPlayerList(String[] args) {
        List<String> playerList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (args[args.length - 1].isEmpty()) {
                if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    playerList.add(player.getName());
                }
            } else {
                if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    playerList.add(player.getName());
                }
            }
        }

        return playerList;
    }
}
