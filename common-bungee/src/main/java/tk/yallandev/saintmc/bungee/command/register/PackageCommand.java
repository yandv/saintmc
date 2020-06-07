package tk.yallandev.saintmc.bungee.command.register;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class PackageCommand implements CommandClass {

	@Command(name = "givevip", usage = "/<command> <player> <tempo> <group>", groupToUse = Group.GERENTE, aliases = {
			"tempgroup" })
	public void givevip(BungeeCommandArgs cmdArgs) {
		final CommandSender sender = cmdArgs.getSender();
		final String[] args = cmdArgs.getArgs();

		if (args.length != 3) {
			sender.sendMessage(
					" §e* §fUse §a/tempgroup <player> <tempo> <group>§f para setar um grupo temporariamente.");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		long expiresCheck;

		try {
			expiresCheck = DateUtils.parseDateDiff(args[1], true);
		} catch (Exception e1) {
			sender.sendMessage(" §c* §fFormato de tempo invalido");
			return;
		}

		expiresCheck = expiresCheck - System.currentTimeMillis();
		RankType rank;

		try {
			rank = RankType.valueOf(args[2].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(" §c* §fO grupo §c" + args[2].toUpperCase()
					+ "§f não existe ou não pode ser setado como temporario.");
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

		sender.sendMessage(" §a* §fVocê deu o vip §a§l" + rank.name() + "§f para o " + player.getPlayerName() + "("
				+ player.getUniqueId().toString().replace("-", "") + ")" + " com a dura§§o de "
				+ DateUtils.formatDifference(expiresCheck / 1000) + "!");
//        
//        BungeeMain.getPlugin().getDiscordManager().sendMessage(new EmbedBuilder().setTitle("LonneMC - " + (cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE")).appendDescription("\nO jogador " + player.getName() + " teve seu cargo atualizado (" + rank.name() + " de " + DateUtils.formatDifference(language, expiresCheck / 1000) + ")").setColor(Color.YELLOW).build(), BungeeMain.getPlugin().getDiscordManager().getTextChannel("logs", true));
	}

	@Command(name = "removevip", usage = "/<command> <player> <group>", groupToUse = Group.DIRETOR, aliases = {
			"removervip" })
	public void removevip(BungeeCommandArgs cmdArgs) {
		final CommandSender sender = cmdArgs.getSender();
		final String[] args = cmdArgs.getArgs();

		if (args.length != 2) {
			sender.sendMessage(" §e* §fUse §a/removevip <player> <group>§f para.");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		RankType rank;

		try {
			rank = RankType.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(" §c* §fO grupo §c" + args[1].toUpperCase()
					+ "§f não existe ou não pode ser setado como temporario.");
			return;
		}

		player.getRanks().remove(rank);
		player.saveRanks();

		sender.sendMessage(" §a* §fVocê removeu o grupo §a§l" + rank.name() + "§f de §a" + player.getPlayerName() + "("
				+ player.getUniqueId().toString().replace("-", "") + ")§f!");
//        BungeeMain.getPlugin().getDiscordManager().sendMessage(new EmbedBuilder().setTitle("LonneMC - " + (cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE")).appendDescription("\nO jogador " + player.getName() + " teve seu cargo atualizado (" + rank.name() + " foi removido").setColor(Color.YELLOW).build(), BungeeMain.getPlugin().getDiscordManager().getTextChannel("logs", true));
	}

	@Completer(name = "tempgroup", aliases = { "givevip" })
	public List<String> tempgroupCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 3) {
			List<String> rankList = new ArrayList<>();

			if (cmdArgs.getArgs()[2].isEmpty()) {
				for (RankType rankType : RankType.values())
					rankList.add(rankType.toString());
			} else {
				for (RankType rankType : RankType.values())
					if (rankType.toString().toLowerCase().startsWith(cmdArgs.getArgs()[2].toLowerCase()))
						rankList.add(rankType.toString());
			}

			return rankList;
		}

		return getPlayerList(cmdArgs.getArgs(), 0);
	}

	@Completer(name = "removevip", aliases = { "removervip" })
	public List<String> removervipCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 2) {
			List<String> rankList = new ArrayList<>();

			if (cmdArgs.getArgs()[1].isEmpty()) {
				for (RankType rankType : RankType.values())
					rankList.add(rankType.toString());
			} else {
				for (RankType rankType : RankType.values())
					if (rankType.toString().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase()))
						rankList.add(rankType.toString());
			}

			return rankList;
		}

		return getPlayerList(cmdArgs.getArgs(), 0);
	}

	@Completer(name = "groupset", aliases = { "setargrupo", "setargrupo", "removevip", "removervip" })
	public List<String> groupsetCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 2) {
			List<String> groupList = new ArrayList<>();

			if (cmdArgs.getArgs()[1].isEmpty()) {
				for (Group group : Group.values())
					groupList.add(group.toString());
			} else {
				for (Group group : Group.values())
					if (group.toString().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase()))
						groupList.add(group.toString());
			}

			return groupList;
		}

		return getPlayerList(cmdArgs.getArgs(), 0);
	}
	
	public List<String> getPlayerList(String[] args, int index) {
		List<String> playerList = new ArrayList<>();

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (args[index].isEmpty()) {
				if (player.getName().toLowerCase().startsWith(args[index].toLowerCase()))
					playerList.add(player.getName());
			} else {
				if (player.getName().toLowerCase().startsWith(args[index].toLowerCase()))
					playerList.add(player.getName());
			}
		}

		return playerList;
	}

}
