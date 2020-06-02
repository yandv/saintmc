package br.com.saintmc.hungergames.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.constructor.SimpleKit;
import br.com.saintmc.hungergames.event.scoreboard.ScoreboardTitleChangeEvent;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;

public class ModeratorCommand implements CommandClass {

	@Command(name = "simplekit", aliases = { "skit" }, groupToUse = Group.MODPLUS)
	public void simplekitCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = (Player) ((BukkitCommandSender) cmdArgs.getSender()).getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			handleHelp(sender, cmdArgs.getLabel());
			return;
		}

		if (args[0].equalsIgnoreCase("criar")) {
			if (args.length == 1) {
				handleHelp(sender, cmdArgs.getLabel());
			} else {
				String kitName = args[1];
				
				if (GameGeneral.getInstance().getSimplekitController().containsKey(kitName)) {
					sender.sendMessage(" §c* §fO kit §c\"" + kitName + "\"§f já existe!");
					return;
				}
				
				sender.sendMessage(" §c* §fVocê criou o kit §a\"" + kitName + "\"§f!");
				GameGeneral.getInstance().getSimplekitController().load(kitName, new SimpleKit(kitName, sender));
				CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
			}
		} else if (args[0].equalsIgnoreCase("editar")) {
			if (args.length == 1) {
				handleHelp(sender, cmdArgs.getLabel());
			} else {
				SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

				if (simpleKit == null) {
					sender.sendMessage(" §c* §fO kit §c\"" + args[1] + "\"§f não existe!");
					return;
				}

				sender.sendMessage(" §c* §fVocê editou o kit §a\"" + simpleKit.getKitName() + "\"§f!");
				simpleKit.updateKit(sender);
				CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
			}
		} else if (args[0].equalsIgnoreCase("default")) {
			if (args.length == 1) {
				handleHelp(sender, cmdArgs.getLabel());
			} else {

				if (args[1].equalsIgnoreCase("remove")) {
					sender.sendMessage(" §c* §fO kit default foi removido!");
					ServerConfig.getInstance().setDefaultSimpleKit(null);
					CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
					return;
				}

				SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

				if (simpleKit == null) {
					sender.sendMessage(" §c* §fO kit §c\"" + args[1] + "\"§f não existe!");
					return;
				}

				sender.sendMessage(" §c* §fVocê alterou o kit default para §a\"" + simpleKit.getKitName() + "\"§f!");
				ServerConfig.getInstance().setDefaultSimpleKit(simpleKit);
				CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
			}
		} else if (args[0].equalsIgnoreCase("aplicar")) {
			if (args.length <= 2) {
				handleHelp(sender, cmdArgs.getLabel());
			} else {
				SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

				if (simpleKit == null) {
					sender.sendMessage(" §c* §fO kit §c\"" + args[1] + "\"§f não existe!");
					return;
				}

				if (args[2].equalsIgnoreCase("all")) {
					Bukkit.getOnlinePlayers().forEach(player -> simpleKit.apply(player));
					sender.sendMessage("§aKit " + simpleKit.getKitName() + " aplicado para todos os jogadores!");
					CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
				} else {
					Player player = Bukkit.getPlayer(args[2]);

					if (player == null) {

						Integer v = null;

						try {
							v = Integer.valueOf(args[2]);
						} catch (NumberFormatException ex) {
							handleHelp(sender, cmdArgs.getLabel());
							return;
						}

						if (v >= 1000) {
							v = 1000;
						}

						final int value = v;

						Bukkit.getOnlinePlayers().stream()
								.filter(target -> target.getLocation().distance(sender.getLocation()) <= value)
								.forEach(target -> simpleKit.apply(target));

						sender.sendMessage("§aKit " + simpleKit.getKitName()
								+ " aplicado para o todos os jogadores em um raio de  " + v + "!");
						CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);

						return;
					}

					simpleKit.apply(player);
					sender.sendMessage(
							"§aKit " + simpleKit.getKitName() + " aplicado para o jogador " + player.getName() + "!");
					CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
				}
			}
		} else {
			handleHelp(sender, cmdArgs.getLabel());
		}
	}

	@Command(name = "cleardrop", aliases = { "cleardrops" }, groupToUse = Group.MODPLUS)
	public void cleardropCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = (Player) ((BukkitCommandSender) cmdArgs.getSender()).getSender();
		
		for (Item entity : sender.getWorld().getEntitiesByClass(Item.class)) {
			entity.remove();
		}
		
		Bukkit.broadcastMessage("§aO chão foi limpo!");
	}
	
	@Command(name = "settitle", groupToUse = Group.MODPLUS)
	public void settitleCommand(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();
		
		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <title> para mudar o titulo da scoreboard!");
			return;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int x = 0; x < args.length; x++) {
			stringBuilder.append(args[x]);
			stringBuilder.append(" ");
		}
		
		String title = stringBuilder.toString().trim().replace("&", "§");
		
		Bukkit.getPluginManager().callEvent(new ScoreboardTitleChangeEvent(title));
		sender.sendMessage(" §a* §fVocê alterou o titulo da scoreboard!");
	}

	public void handleHelp(Player sender, String label) {
		sender.sendMessage(" §e* §fUse §a/" + label + " criar <nome>§f para criar um skit");
		sender.sendMessage(" §e* §fUse §a/" + label + " editar <nome>§f para editar um skit");
		sender.sendMessage(" §e* §fUse §a/" + label + " default <nome:remove>§f para setar o kit default");
		sender.sendMessage(" §e* §fUse §a/" + label + " aplicar <nome> <all:distancia:player>§f para aplicar um skit");
	}

	@Completer(name = "simplekit", aliases = { "skit" })
	public List<String> simplekitCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> effectList = new ArrayList<>();
			String[] args = { "aplicar", "default", "criar", "editar" };

			if (cmdArgs.getArgs()[0].isEmpty()) {
				for (String arg : args)
					effectList.add(arg);
			} else {
				for (String arg : args)
					if (arg.startsWith(cmdArgs.getArgs()[0].toLowerCase()))
						effectList.add(arg);
			}

			return effectList;
		} else if (cmdArgs.getArgs().length == 2) {
			List<String> effectList = new ArrayList<>();
			Collection<SimpleKit> simpleKits = GameGeneral.getInstance().getSimplekitController().getStoreMap()
					.values();

			if (cmdArgs.getArgs()[1].isEmpty()) {
				for (SimpleKit arg : simpleKits)
					effectList.add(arg.getKitName());
			} else {
				for (SimpleKit arg : simpleKits)
					if (arg.getKitName().startsWith(cmdArgs.getArgs()[1].toLowerCase()))
						effectList.add(arg.getKitName());
			}

			return effectList;
		}

		return getPlayerList(cmdArgs.getArgs());
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
