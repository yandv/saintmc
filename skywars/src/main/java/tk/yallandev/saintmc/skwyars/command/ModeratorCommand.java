package tk.yallandev.saintmc.skwyars.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.Interact;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.game.chest.Chest;
import tk.yallandev.saintmc.skwyars.game.chest.ChestType;

public class ModeratorCommand implements CommandClass {

	@Command(name = "setspawn", aliases = { "setlocation" }, groupToUse = Group.MODPLUS)
	public void setspawnCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			cmdArgs.getSender().sendMessage("§cUso /" + cmdArgs.getLabel() + " <warpName> para setar uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < args.length; x++) {
			stringBuilder.append(args[x]).append(" ");
		}

		String configName = args[0];
		GameMain.getInstance().registerLocationInConfig(((BukkitMember) cmdArgs.getSender()).getPlayer().getLocation(),
				configName);
		sender.sendMessage("§aVocê setou a warp " + configName + "!");
	}

	@Command(name = "tempo", groupToUse = Group.MOD)
	public void tempoCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <tempo:stop> para alterar o tempo do jogo!");
			return;
		}

		if (args[0].equalsIgnoreCase("stop")) {
			GameGeneral.getInstance().setCountTime(!GameGeneral.getInstance().isCountTime());
			sender.sendMessage(
					(GameGeneral.getInstance().isCountTime() ? "§aVocê ativou" : "§cVocê desativou") + " o timer!");
			return;
		}

		long time;

		try {
			time = DateUtils.parseDateDiff(args[0], true);
		} catch (Exception e) {
			sender.sendMessage("§cO formato de tempo não é valido.");
			return;
		}

		int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000);

		if (seconds >= 60 * 120)
			seconds = 60 * 120;

		sender.sendMessage("§aO tempo do jogo foi alterado para " + args[0] + "!");
		GameGeneral.getInstance().setTime(seconds);
	}

	@Command(name = "chest", groupToUse = Group.MOD)
	public void chestCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage("§cUso /" + cmdArgs.getLabel() + " <chestType> para adicionar baus ao jogo!");
			return;
		}

		if (args[0].equalsIgnoreCase("save")) {
			GameGeneral.getInstance().getLocationController().saveChests();
			player.sendMessage("§aA config foi salva!");
			return;
		}

		ChestType chest = null;

		try {
			chest = ChestType.valueOf(args[0].toUpperCase());
		} catch (Exception ex) {
			// mandar msg de nao existe esse tipo
			return;
		}

		final ChestType chestType = chest;

		/**
		 * foda-se
		 */

		player.getInventory().addItem(new ActionItemStack(
				new ItemBuilder().name("§a" + NameUtils.formatString(chestType.name()))
						.type(chestType == ChestType.DEFAULT ? Material.DIAMOND
								: chestType == ChestType.MINIFEAST ? Material.GOLD_INGOT : Material.IRON_INGOT)
						.build(),
				new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (block == null)
							return true;

						if (block.getType() != Material.CHEST)
							return true;

						if (action == ActionType.LEFT) {
							GameGeneral.getInstance().getLocationController()
									.registerChest(new Chest(block.getX(), block.getY(), block.getZ(), chestType));
							player.sendMessage("§aO bau foi adicionado como " + chestType.name() + "!");
						} else {
							GameGeneral.getInstance().getLocationController()
									.removeChest(new Chest(block.getX(), block.getY(), block.getZ(), chestType));
							player.sendMessage("§cO bau foi removido!");
						}

						return true;
					}
				}).getItemStack());
	}

	@Completer(name = "chest")
	public List<String> chestCompleter(CommandArgs cmdArgs) {
		List<String> list = new ArrayList<>();

		if (cmdArgs.getArgs().length == 0) {
			list.addAll(Arrays.asList(ChestType.values()).stream().map(chestType -> chestType.name())
					.collect(Collectors.toList()));
		} else {
			for (ChestType chestType : ChestType.values()) {
				if (chestType.name().toLowerCase().startsWith(cmdArgs.getArgs()[0])) {
					list.add(chestType.name());
				}
			}
		}

		return list;
	}

}
