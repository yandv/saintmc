package tk.yallandev.saintmc.bungee.command.register;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.ban.Category;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.ban.constructor.Ban.UnbanReason;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.ban.constructor.Warn;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class BanCommand implements CommandClass {

    @Command(name = "p", groupToUse = Group.TRIAL, runAsync = true)
    public void punishCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length < 4) {
            sender.sendMessage(" §cUse /p <ban:mute> <player> <motivo> <tempo> para punir um jogador.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[1]);

        if (uuid == null) {
            sender.sendMessage("§cO jogador " + args[1] + " não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage("§cO jogador " + args[1] + " nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + ".");
                return;
            }
        }

        Group playerGroup = Group.MEMBRO;

        if (cmdArgs.isPlayer()) {
            playerGroup = Member.getGroup(cmdArgs.getSender().getUniqueId());
        } else {
            playerGroup = Group.ADMIN;
        }

        if (cmdArgs.isPlayer()) {
            if (playerGroup.ordinal() < player.getGroup().ordinal()) {
                sender.sendMessage("§cVocê não pode banir esse jogador.");
                return;
            }
        }

        Category category = null;

        try {
            category = Category.valueOf(args[2].toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(" §cCategoria invalida.");
            return;
        }

        long expiresCheck;

        try {
            expiresCheck = args[3].equalsIgnoreCase("0") ? -1L : DateUtils.parseDateDiff(args[3], true);
        } catch (Exception e1) {
            sender.sendMessage(" §cFormato de tempo invalido.");
            return;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            Ban ban = new Ban(category, player.getUniqueId(), player.getPlayerName(),
                              cmdArgs.isPlayer() ? cmdArgs.getSender().getName() : "CONSOLE", sender.getUniqueId(),
                              category.getReason(), expiresCheck);

            if (BungeeMain.getInstance().getPunishManager().ban(player, ban)) {
                sender.sendMessage(
                        " §aVocê baniu o jogador §a" + player.getPlayerName() + "§a por §a" + ban.getReason() +
                        "§a.");
            } else {
                sender.sendMessage("§cO jogador já está banido.");
            }
        } else if (args[0].equalsIgnoreCase("mute")) {
            Mute mute = new Mute(category, player.getUniqueId(),
                                 cmdArgs.isPlayer() ? cmdArgs.getSender().getName() : "CONSOLE",
                                 sender.getUniqueId(), category.getReason(), expiresCheck);

            if (BungeeMain.getInstance().getPunishManager().mute(player, mute)) {
                sender.sendMessage(
                        " §aVocê mutou o jogador §a" + player.getPlayerName() + "§a por §a" + mute.getReason() +
                        "§a.");
            } else {
                sender.sendMessage(" §cNão foi possível mutar o jogador.");
            }
        } else {
            sender.sendMessage(" §cUse /p <ban:mute:warn> <player> <tempo> <motivo> para punir um jogador.");
        }
    }

    @Command(name = "unban", aliases = {"desbanir"}, runAsync = true, groupToUse = Group.ADMIN)
    public void unbanCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /unban <player> <unbanReason> para desbanir um jogador.");
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
                    sender.sendMessage(" §cO jogador " + args[0] + "§c nunca entrou no servidor!");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§f!");
                return;
            }
        }

        UnbanReason unbanReason = UnbanReason.OTHER;

        try {
            unbanReason = UnbanReason.valueOf(args[1].toUpperCase());
        } catch (Exception ex) {
            unbanReason = UnbanReason.OTHER;
        }

        if (BungeeMain.getInstance().getPunishManager().unban(player, sender.getUniqueId(),
                                                              cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() :
                                                              "CONSOLE", unbanReason)) {
            sender.sendMessage(" §aVocê desbaniu o jogador §a" + player.getPlayerName() + "§a.");
        } else {
            sender.sendMessage(" §cNão foi possível banir o jogador.");
        }
    }

    @Command(name = "unmute", runAsync = true, groupToUse = Group.MODPLUS)
    public void unmuteCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /unmute <player> para desmutar um jogador.");
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
                    sender.sendMessage(" §cO jogador " + args[0] + " nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador " + args[0] + "§c.");
                return;
            }
        }

        if (BungeeMain.getInstance().getPunishManager().unmute(player, sender.getUniqueId(),
                                                               cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() :
                                                               "CONSOLE")) {
            sender.sendMessage(" §aVocê desmutou o jogador §a" + player.getPlayerName() + "§a.");
        } else {
            sender.sendMessage(" §cNão foi possível desmutar o jogador!");
        }
    }

    @Command(name = "warn", aliases = {"avisar"}, runAsync = true, groupToUse = Group.TRIAL)
    public void warnCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /warn <player> <motivo> para avisar um jogador.");
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

        Group playerGroup = Group.MEMBRO;

        if (cmdArgs.isPlayer()) {
            playerGroup = Member.getGroup(cmdArgs.getPlayer().getUniqueId());
        } else {
            playerGroup = Group.ADMIN;
        }

        if (cmdArgs.isPlayer()) {
            if (playerGroup.ordinal() < player.getGroup().ordinal()) {
                sender.sendMessage(" §cVocê não pode majenar o grupo desse jogador.");
                return;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++)
            sb.append(args[i]).append(" ");

        int id = CommonGeneral.getInstance().getPunishData().getTotalWarn() + 1;

        Warn warn = new Warn(uuid, id, cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE",
                             sender.getUniqueId(), sb.toString().trim(),
                             System.currentTimeMillis() + (1000 * 60 * 60 * 12));

        if (BungeeMain.getInstance().getPunishManager().warn(player, warn)) {
            sender.sendMessage(
                    " §aVocê alertou o jogador §a" + player.getPlayerName() + "§a por §a" + warn.getReason() +
                    "§a.");
        } else {
            sender.sendMessage(" §cNão foi possível alertar o jogador.");
        }
    }

    @Command(name = "dupeip", runAsync = true, groupToUse = Group.ADMIN)
    public void dupeipCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(
                    " §cUse §c/" + cmdArgs.getLabel() + " <player>cf para ver os jogadores com o mesmo ip.");
            return;
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

        Collection<MemberModel> memberCollection = CommonGeneral.getInstance().getPlayerData()
                                                                .loadMemberByIp(player.getLastIpAddress());

        sender.sendMessage("   §3§lDUPEIP");
        sender.sendMessage(" ");
        sender.sendMessage("§7Conta pesquisada: §a" + player.getName() + "");
        sender.sendMessage("§7Numeros de conta: §a" + memberCollection.size());
        sender.sendMessage("§7Conta" + (memberCollection.size() > 1 ? "" : "s") + ": §a" +
                           (memberCollection.isEmpty() ? "§cNenhuma conta encontrada" : Joiner.on(", ")
                                                                                              .join(memberCollection
                                                                                                            .stream()
                                                                                                            .map(memberModel -> memberModel.getPlayerName())
                                                                                                            .collect(
                                                                                                                    Collectors.toList()))));
    }
}
