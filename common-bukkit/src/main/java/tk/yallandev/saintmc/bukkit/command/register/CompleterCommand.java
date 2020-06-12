package tk.yallandev.saintmc.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.worldedit.arena.ArenaType;
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

	@Command(name = "send", aliases = { "groupset", "setargrupo" }, groupToUse = Group.ADMIN)
	public void adminCommand(CommandArgs cmdArgs) {

	}

	@Command(name = "groupset", aliases = { "removevip", "tempgroup", "givevip", "removervip", "unban", "unmute",
			"glist", "broadcast" }, groupToUse = Group.GERENTE)
	public void managerCommand(CommandArgs cmdArgs) {

	}

	@Command(name = "screenshare", aliases = { "ss", "fakelist", "find" }, groupToUse = Group.MODGC)
	public void modgcCommand(CommandArgs cmdArgs) {

	}

	@Command(name = "ban", aliases = { "mute", "banir", "unban", "desbanir", "tempban", "tempmute", "send", "staffchat",
			"sc" }, groupToUse = Group.TRIAL)
	public void trialCommand(CommandArgs cmdArgs) {

	}

	@Command(name = "lobby", aliases = { "server", "connect", "ir", "go", "discord", "hub", "ping" })
	public void memberCommand(CommandArgs cmdArgs) {

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

		return getPlayerList(cmdArgs.getArgs());
	}

	@Completer(name = "groupset", aliases = { "setargrupo" })
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

		return getPlayerList(cmdArgs.getArgs());
	}

	@Completer(name = "tag")
	public List<String> tagCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> tagList = new ArrayList<>();
			BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
					.getMember(cmdArgs.getSender().getUniqueId());

			if (cmdArgs.getArgs()[0].isEmpty()) {
				for (Tag tag : Tag.values())
					if (member.getTags().contains(tag))
						tagList.add(tag.getName());
			} else {
				for (Tag tag : Tag.values())
					if (member.getTags().contains(tag))
						if (tag.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
							tagList.add(tag.getName());
			}

			return tagList;
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
					if (enchantment.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						enchantmentList.add(enchantment.getName());
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
					if (effect.getName().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase()))
						effectList.add(effect.getName());
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
					if (enchantment.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						enchantmentList.add(enchantment.name());
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
					if (arenaType.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						typeList.add(arenaType.name());
			}
			
			return typeList;
		} else if (cmdArgs.getArgs().length == 2) {
			List<String> materialList = new ArrayList<>();

			if (cmdArgs.getArgs()[1].isEmpty()) {
				for (Material enchantment : Material.values())
					materialList.add(enchantment.name());
			} else {
				for (Material enchantment : Material.values())
					if (enchantment.name().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase()))
						materialList.add(enchantment.name());
			}

			return materialList;
		}

		return new ArrayList<>();
	}

	public List<String> getPlayerList(String[] args) {
		List<String> playerList = new ArrayList<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (args[args.length - 1].isEmpty()) {
				if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					playerList.add(player.getName());
			} else {
				if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					playerList.add(player.getName());
			}
		}

		return playerList;
	}

}
