package tk.yallandev.saintmc.bukkit.command.register;

import java.util.UUID;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.menu.report.ReportInventory;
import tk.yallandev.saintmc.bukkit.menu.report.ReportListInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;

public class ReportCommand implements CommandClass {

	@Command(name = "report", aliases = { "reports", "reportar" })
	public void reportCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (!member.hasGroupPermission(Group.AJUDANTE)) {
			sender.sendMessage("§cVocê não tem permissão para executar esse comando!");
			return;
		}

		if (args.length == 0) {
			new ReportListInventory(sender, 1);
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage("§cO jogador " + args[0] + " não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + "!");
				return;
			}
		}

		Report report = CommonGeneral.getInstance().getReportManager().getReport(player.getUniqueId());

		if (report == null) {
			sender.sendMessage("§cO jogador " + args[0] + " não foi reportado!");
			return;
		}

		new ReportInventory(sender, report);
	}

}