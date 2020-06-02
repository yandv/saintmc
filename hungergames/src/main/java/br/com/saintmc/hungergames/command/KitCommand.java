package br.com.saintmc.hungergames.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.menu.kit.KitSelector;
import br.com.saintmc.hungergames.menu.kit.KitSelector.OrderType;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;

public class KitCommand implements CommandClass {

	@Command(name = "kit")
	public void kitCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		handleKit(sender, args, cmdArgs.getLabel(), KitType.PRIMARY);
	}

	@Command(name = "kit2")
	public void kit2Command(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		handleKit(sender, args, cmdArgs.getLabel(), KitType.SECONDARY);
	}

	@Command(name = "givekit", groupToUse = Group.MODPLUS)
	public void giveitemKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <all:player>§f para dar o kit do item!");
			return;
		}

		List<Player> playerList = new ArrayList<>();

		if (args[0].equalsIgnoreCase("all")) {
			playerList = Bukkit.getOnlinePlayers().stream()
					.filter(player -> !GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying())
					.collect(Collectors.toList());
		} else {
			Player player = Bukkit.getPlayer(args[0]);

			if (player == null) {
				sender.sendMessage(" §c* §fO jogador §c\"" + args[0] + "\"§f não existe!");
				return;
			}

			playerList.add(player);
		}

		for (Player player : playerList) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			if (gamer == null)
				continue;

			for (Kit playerKit : gamer.getKitMap().values()) {
				for (Ability ability : playerKit.getAbilities()) {
					for (ItemStack item : ability.getItemList()) {
						player.getInventory().addItem(item);
					}
				}
			}
		}

		sender.sendMessage("§a" + playerList.size() + " receberam o item do kit!");
	}

	@Command(name = "forcekit", groupToUse = Group.MODPLUS)
	public void forcekitKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <kit> <all:player> <1/2>§f forca o kit do player!");
			return;
		}

		Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

		if (kit == null) {
			sender.sendMessage(" §c* §fO kit §c\"" + args[0] + "\"§f não existe!");
			return;
		}

		List<Player> playerList = new ArrayList<>();

		if (args[1].equalsIgnoreCase("all")) {
			playerList = Bukkit.getOnlinePlayers().stream()
					.filter(player -> !GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying())
					.collect(Collectors.toList());
		} else {
			Player player = Bukkit.getPlayer(args[1]);

			if (player == null) {
				sender.sendMessage(" §c* §fO jogador §c\"" + args[1] + "\"§f não existe!");
				return;
			}

			playerList.add(player);
		}

		KitType kitType = KitType.PRIMARY;

		if (args.length >= 3) {
			kitType = args[2].equalsIgnoreCase("1") ? KitType.PRIMARY : KitType.SECONDARY;
		}

		for (Player player : playerList) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			if (gamer == null)
				continue;
			
			GameGeneral.getInstance().getKitController().setKit(player, kit, kitType);
		}

		sender.sendMessage("§a" + playerList.size() + " tiveram o kit alterado para o " + kit.getName() + "!");
	}

	@Command(name = "togglekit", groupToUse = Group.MODPLUS)
	public void togglekitKit(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length < 2) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <kit> <on:off> <1/2>§f forca o kit do player!");
			return;
		}

		List<Kit> kitList = new ArrayList<>();

		if (args[0].equalsIgnoreCase("all")) {
			kitList.addAll(GameGeneral.getInstance().getKitController().getAllKits());
		} else {
			if (args[0].contains(",")) {
				for (String kitName : args[0].split(",")) {
					Kit kit = GameGeneral.getInstance().getKitController().getKit(kitName);

					if (kit == null) {
						sender.sendMessage(" §c* §fO kit §c\"" + args[0] + "\"§f não existe!");
						return;
					}
					
					kitList.add(kit);
				}
			} else {
				Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

				if (kit == null) {
					sender.sendMessage(" §c* §fO kit §c\"" + args[0] + "\"§f não existe!");
					return;
				}
				
				kitList.add(kit);
			}
		}
		
		if (!Arrays.asList("on", "off").contains(args[1].toLowerCase())) {
			sender.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <kit> <on:off> <1/2>§f forca o kit do player!");
			return;
		}

		boolean enabled = args[1].equalsIgnoreCase("on") ? true : false;

		KitType kitType = KitType.PRIMARY;

		if (args.length >= 3) {
			kitType = args[2].equalsIgnoreCase("1") ? KitType.PRIMARY : KitType.SECONDARY;
		}

		if (!args[0].equalsIgnoreCase("all")) {
			if (ServerConfig.getInstance().isDisabled(kitList.stream().findFirst().orElse(null), kitType)) {
				if (!enabled) {
					sender.sendMessage("§cO kit já está desativado!");
					return;
				}
			} else {
				if (enabled) {
					sender.sendMessage("§aO kit já está ativado!");
					return;
				}
			}
		}

		if (!enabled) {
			for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers()) {
				if (kitList.contains(gamer.getKit(kitType))) {
					GameGeneral.getInstance().getKitController().unregisterPlayer(gamer.getPlayer(), kitType);
				}
			}

			for (Kit kit : kitList) {
				ServerConfig.getInstance().disableKit(kit, kitType);
			}
		} else
			for (Kit kit : kitList) {
				ServerConfig.getInstance().enableKit(kit, kitType);
			}
		
		List<String> listName = new ArrayList<>();
		
		for (Kit kit : kitList)
			listName.add(kit.getName());
		
		sender.sendMessage(" §a* §fVocê " + (enabled ? "§aativou" : "§cdesativou") + " §f"
				+ (args[0].equalsIgnoreCase("all") ? "todos os kits" : "o(s) kit(s) §a" + Joiner.on(", ").join(listName))
				+ "§f!");
	}

	@Completer(name = "kit", aliases = { "kit2", "togglekit", "forcekit", "givekit" })
	public List<String> tagCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> list = new ArrayList<>();

			for (Kit kit : GameGeneral.getInstance().getKitController().getAllKits())
				if (kit.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
					list.add(kit.getName());

			return list;
		}

		return new ArrayList<>();
	}

	private void handleKit(CommandSender s, String[] args, String label, KitType kitType) {
		BukkitCommandSender sender = (BukkitCommandSender) s;

		if (!(sender.getSender() instanceof Player))
			return;

		if (args.length == 0) {
			new KitSelector((Player) sender.getSender(), 1, kitType, OrderType.MINE);
			return;
		}

		Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);

		if (kit == null) {
			s.sendMessage(" §c* §fO kit §c\"" + args[0] + "\"§f não existe!");
			return;
		}

		GameGeneral.getInstance().getKitController().selectKit((Player) sender.getSender(), kit, kitType);
	}

}
