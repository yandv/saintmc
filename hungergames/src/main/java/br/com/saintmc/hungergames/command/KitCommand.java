package br.com.saintmc.hungergames.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.menu.kit.KitSelector;
import br.com.saintmc.hungergames.menu.kit.KitSelector.OrderType;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;

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
	
	@Completer(name = "kit", aliases = { "kit2" })
	public List<String> tagCompleter(CommandArgs cmdArgs) {
		if (cmdArgs.getArgs().length == 1) {
			List<String> list = new ArrayList<>();

			for (Kit kit : GameGeneral.getInstance().getKitController().getAllKits())
				if (kit.toString().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
					list.add(kit.toString());

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
