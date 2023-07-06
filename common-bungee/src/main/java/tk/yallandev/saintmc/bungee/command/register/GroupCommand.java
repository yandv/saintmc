package tk.yallandev.saintmc.bungee.command.register;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class GroupCommand implements CommandClass {

    @Command(name = "groupset", usage = "/<command> <player> <group>", groupToUse = Group.ADMIN,
             aliases = {"setargrupo"}, runAsync = true)
    public void groupsetCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length != 2) {
            sender.sendMessage("§cUso /groupset <player> <group> para setar um grupo.");
            return;
        }

        Group grupo = null;

        try {
            grupo = Group.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            sender.sendMessage("§cO grupo " + args[1].toUpperCase() + " não existe.");
            return;
        }

        Group group = grupo;
        Group playerGroup = Group.MEMBRO;

        if (cmdArgs.isPlayer()) {
            Member battleSender = CommonGeneral.getInstance().getMemberManager()
                                               .getMember(cmdArgs.getPlayer().getUniqueId());
            playerGroup = battleSender.getServerGroup();
        } else {
            playerGroup = Group.ADMIN;
        }

        if (group.ordinal() < Group.YOUTUBER.ordinal() && group.ordinal() >= Group.VIP.ordinal()) {
            sender.sendMessage("§cO grupo " + group.name() + " pode ser setado somente temporariamente.");
            return;
        }

        if (cmdArgs.isPlayer()) {
            switch (playerGroup) {
            case ADMIN: {
                if (group.ordinal() > Group.ADMIN.ordinal()) {
                    sender.sendMessage("§cVocê só pode manejar o grupo " + Tag.ADMIN.getPrefix() + "§c ou inferior.");
                    return;
                }
                break;
            }
            default:
                break;
            }
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage("§cO jogador " + args[0] + " não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + ".");
                return;
            }
        }

        if (cmdArgs.isPlayer()) {
            if (playerGroup.ordinal() < player.getGroup().ordinal()) {
                sender.sendMessage("§cVocê não pode majenar o grupo desse jogador.");
                return;
            }
        }

        Group actualGroup = player.getGroup();

        if (actualGroup == group) {
            sender.sendMessage("§cO jogador " + player.getPlayerName() + " já está nesse grupo.");
            return;
        }

        player.setGroup(group);
        sender.sendMessage("§aVocê alterou o grupo do " + player.getPlayerName() + "(" +
                           player.getUniqueId().toString().replace("-", "") + ")" + " para " + group.name() + ".");
    }

    @Command(name = "permission", usage = "/<command> <player> <group>", groupToUse = Group.ADMIN, runAsync = true)
    public void addpermissionCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length <= 2) {
            sender.sendMessage(" §cUse §c/" + cmdArgs.getLabel() +
                               " <playerName> <add/remove> <permission>§c para adicionar ou remover um grupo.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador §c" + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador §c" + args[0] + "§c nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§c.");
                return;
            }
        }

        if (args[1].equalsIgnoreCase("add")) {
            player.addPermission(args[2]);
            sender.sendMessage("§aVocê adicionou a permissão " + args[2] + " de " + player.getName());
        } else if (args[1].equalsIgnoreCase("remove")) {
            player.removePermission(args[2]);
            sender.sendMessage("§aVocê removeu a permissão " + args[2] + " de " + player.getName());
        }
    }

    @Command(name = "addmedal", groupToUse = Group.ADMIN, runAsync = true)
    public void addmedalCommand(CommandArgs cmdArgs) {
        String[] args = cmdArgs.getArgs();
        CommandSender sender = cmdArgs.getSender();

        if (args.length == 0) {
            sender.sendMessage(
                    " §cUse §c/" + cmdArgs.getLabel() + " <player> <medalha>§c para dar medalha para alguém.");

            TextComponent textComponent = new MessageBuilder(" §aMedalhas disponíveis: ").create();

            for (TextComponent txt : Arrays.asList(Medal.values()).stream().filter(medal -> medal != Medal.NONE)
                                           .map(medal -> new MessageBuilder(
                                                   "" + medal.getChatColor() + medal.getMedalIcon() + ", ").setHoverEvent(
                                                                                                              new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                                                                                      "" + medal.getChatColor() + medal.getMedalName()).create()))
                                                                                                      .setClickEvent(
                                                                                                              new ClickEvent(
                                                                                                                      ClickEvent.Action.RUN_COMMAND,
                                                                                                                      "/medal " +
                                                                                                                      medal.name()))
                                                                                                      .create())
                                           .collect(Collectors.toList())) {
                textComponent.addExtra(txt);
            }

            sender.sendMessage(textComponent);
            return;
        }

        Medal medal = Medal.getMedalByName(args[1]);

        if (medal == null) {
            sender.sendMessage("§cEssa medalha não existe.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador " + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador §c" + args[0] + "§c nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador " + args[0] + "§c.");
                return;
            }
        }

        if (player.getMedalList().contains(medal)) {
            sender.sendMessage("§cO jogador " + player.getPlayerName() + " já tem esta medalha!");
        } else {
            player.addMedal(medal);
            player.sendMessage("§aVocê ganhou a medalha " + medal.getMedalName() + "!");
            sender.sendMessage("§aVocê deu a medalha " + medal.getMedalName() + " para o " + player.getName() + ".");
        }
    }

    @Command(name = "givevip", usage = "/<command> <player> <tempo> <group>", groupToUse = Group.ADMIN,
             aliases = {"tempgroup"}, runAsync = true)
    public void givevip(BungeeCommandArgs cmdArgs) {
        final CommandSender sender = cmdArgs.getSender();
        final String[] args = cmdArgs.getArgs();

        if (args.length != 3) {
            sender.sendMessage(
                    " §aUse /tempgroup <player> <tempo> <group> para setar um grupo temporariamente.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador §c" + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador §c" + args[0] + "§c nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§c.");
                return;
            }
        }

        long expiresCheck;

        try {
            expiresCheck = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e1) {
            sender.sendMessage(" §cFormato de tempo invalido.");
            return;
        }

        expiresCheck = expiresCheck - System.currentTimeMillis();
        RankType rank;

        try {
            rank = RankType.valueOf(args[2].toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(" §cO grupo §c" + args[2].toUpperCase() +
                               "§c não existe ou não pode ser setado como temporario.");
            return;
        }

        long newAdd = System.currentTimeMillis();

        if (player.getRanks().containsKey(rank)) {
            newAdd = player.getRanks().get(rank);
        }

        newAdd = newAdd + expiresCheck;
        player.getRanks().put(rank, newAdd);
        player.setTag(Tag.valueOf(rank.toString()));
        player.saveRanks();

        player.sendMessage("§aVocê recebeu o rank " + Tag.valueOf(rank.name()).getPrefix() + "§a por " +
                           DateUtils.formatDifference(expiresCheck / 1000) + "!");
        sender.sendMessage("§aVocê deu o vip" + rank.name() + " para o " + player.getPlayerName() + "(" +
                           player.getUniqueId().toString().replace("-", "") + ")" + " com a duração de " +
                           DateUtils.formatDifference(expiresCheck / 1000) + "!");
    }

    @Command(name = "removevip", usage = "/<command> <player> <group>", groupToUse = Group.ADMIN,
             aliases = {"removervip"})
    public void removevip(BungeeCommandArgs cmdArgs) {
        final CommandSender sender = cmdArgs.getSender();
        final String[] args = cmdArgs.getArgs();

        if (args.length != 2) {
            sender.sendMessage(" §cUse /removevip <player> <group>.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador §c" + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador §c" + args[0] + "§c nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§c.");
                return;
            }
        }

        RankType rank;

        try {
            rank = RankType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(" §cO grupo §c" + args[1].toUpperCase() +
                               "§c não existe ou não pode ser setado como temporario.");
            return;
        }

        player.getRanks().remove(rank);
        player.saveRanks();

        sender.sendMessage(
                " §aVocê removeu o grupo §a§l" + rank.name() + "§a de §a" + player.getPlayerName() + "(" +
                player.getUniqueId().toString().replace("-", "") + ")§a.");
//		BungeeMain.getPlugin().getDiscordManager().sendMessage(
//				new EmbedBuilder()
//						.setTitle("LonneMC - " + (cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE"))
//						.appendDescription("\nO jogador " + player.getName() + " teve seu cargo atualizado ("
//								+ rank.name() + " foi removido")
//						.setColor(Color.YELLOW).build(),
//				BungeeMain.getPlugin().getDiscordManager().getTextChannel("logs", true));
    }
}
