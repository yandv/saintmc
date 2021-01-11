package tk.yallandev.saintmc.bukkit.command.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.saintmc.anticheat.controller.MemberController;
import tk.yallandev.saintmc.CommonGeneral;
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
		}
	}

	@Command(name = "autoban", groupToUse = Group.AJUDANTE)
	public void autobanCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§eO /" + cmdArgs.getLabel() + " <player> para cancelar o banimento do jogador!");
			return;
		}

		Player player = Bukkit.getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage("§cO jogador está offline!");
			return;
		}

		MemberController.INSTANCE.unload(player);
		sender.sendMessage("§aO banimento automatico do jogador " + player.getName() + " foi removido!");
	}

}
