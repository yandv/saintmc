package br.com.saintmc.hungergames.command;

import org.bukkit.entity.Player;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.gamer.Gamer;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.menu.kit.KitSelector;
import br.com.saintmc.hungergames.menu.kit.KitSelector.OrderType;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

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

	private void handleKit(CommandSender s, String[] args, String label, KitType kitType) {
		BukkitCommandSender sender = (BukkitCommandSender) s;
		
		if (!(sender.getSender() instanceof Player))
			return;
		
		if (args.length == 0) {
			new KitSelector((Player) sender.getSender(), 1, kitType, OrderType.MINE);
			return;
		}
		
		Kit kit = GameGeneral.getInstance().getKitController().getKit(args[0]);
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(sender.getUniqueId());
		
		if (gamer.getKit(kitType) == kit) {
			Title.send((Player) sender.getSender(), "§c" + NameUtils.formatString(kit.getName()), "§cVocê já está com esse kit!", SimpleTitle.class);
			sender.sendMessage(" §c* §fVocê já está com o kit §c" + kit.getName() + "§f!");
			return;
		}
		
		Title.send((Player) sender.getSender(), "§a" + NameUtils.formatString(kit.getName()), "§fSelecionado com sucesso!", SimpleTitle.class);
		sender.sendMessage(" §a* §fVocê selecionou o kit §a" + kit.getName() + "§f!");
		gamer.setKit(kitType, kit);
	}

}
