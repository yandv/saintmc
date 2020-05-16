package tk.yallandev.saintmc.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.permission.Tag;

public class CompleterCommand implements CommandClass {

	@Command(name = "ban", aliases = { "mute", "tempgroup", "givevip", "removervip", "banir", "unban", "desbanir",
			"tempban", "tempmute", "ping", "screenshare", "discord", "", "ss", "server", "connect", "find", "go", "ir",
			"lobby", "send" })
	public void completerCommand(CommandArgs cmdArgs) {

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

		return getPlayerList(cmdArgs.getArgs());
	}

	@Completer(name = "removevip", aliases = { "removervip" })
	public List<String> removervipCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 2) {
			ArrayList<String> rankList = new ArrayList<>();

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

		return getPlayerList(cmdArgs.getArgs());
	}

	@Completer(name = "groupset", aliases = { "setargrupo" })
	public List<String> groupsetCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 2) {
			ArrayList<String> groupList = new ArrayList<>();

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

		return getPlayerList(cmdArgs.getArgs());
	}

	@Completer(name = "tag")
	public List<String> tagCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			ArrayList<String> tagList = new ArrayList<>();

			if (cmdArgs.getArgs()[0].isEmpty()) {
				for (Tag tag : Tag.values())
					tagList.add(tag.toString());
			} else {
				for (Tag tag : Tag.values())
					if (tag.toString().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						tagList.add(tag.toString());
			}

			return tagList;
		}

		return new ArrayList<>();
	}

	@Completer(name = "enchant")
	public List<String> enchantCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			ArrayList<String> enchantmentList = new ArrayList<>();

			if (cmdArgs.getArgs()[0].isEmpty()) {
				for (Enchantment enchantment : Enchantment.values())
					enchantmentList.add(enchantment.toString());
			} else {
				for (Enchantment enchantment : Enchantment.values())
					if (enchantment.toString().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						enchantmentList.add(enchantment.toString());
			}

			return enchantmentList;
		}

		return new ArrayList<>();
	}

	@Completer(name = "effect")
	public List<String> effectCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 2) {
			ArrayList<String> enchantmentList = new ArrayList<>();

			if (cmdArgs.getArgs()[1].isEmpty()) {
				for (Enchantment enchantment : Enchantment.values())
					enchantmentList.add(enchantment.toString());
			} else {
				for (Enchantment enchantment : Enchantment.values())
					if (enchantment.toString().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase()))
						enchantmentList.add(enchantment.toString());
			}

			return enchantmentList;
		}

		return getPlayerList(cmdArgs.getArgs());
	}

	public List<String> getPlayerList(String[] args) {
		List<String> playerList = new ArrayList<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (args[args.length - 1].isEmpty()) {
				if (playerList.toString().toLowerCase().startsWith(player.getName().toLowerCase()))
					playerList.add(player.getName());
			} else {
				if (playerList.toString().toLowerCase().startsWith(player.getName().toLowerCase()))
					playerList.add(player.getName());
			}
		}

		return playerList;
	}

}
