package tk.yallandev.anticheat.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.yallandev.anticheat.menu.AnticheatInventory;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandSender;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;

public class AnticheatCommand implements CommandClass {

	@Command(name = "anticheat", aliases = { "ac" }, groupToUse = Group.AJUDANTE)
	public void anticheatCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0 || !cmdArgs.isPlayer()) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

			member.getAccountConfiguration()
					.setAnticheatEnabled(!member.getAccountConfiguration().isAnticheatEnabled());
			member.sendMessage(member.getAccountConfiguration().isAnticheatEnabled()
					? "§aAs mensagens do anticheat foram ativadas!"
					: "§cAs mensagens do anticheat foram desativadas!");
			return;
		}

		if (args[0].equalsIgnoreCase("check")) {
			if (args.length == 1) {
				return;
			}

			Player tester = Bukkit.getPlayer(args[1]);

			if (tester == null) {
				sender.sendMessage("§cO jogador " + args[1] + " está offline!");
				return;
			}

			new AnticheatInventory((Player) ((BukkitCommandSender) sender).getSender(), tester);
		}
	}

}
