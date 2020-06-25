package tk.yallandev.saintmc.bukkit.command.register;

import java.util.UUID;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.menu.report.ReportInventory;
import tk.yallandev.saintmc.bukkit.menu.report.ReportListInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;

public class ReportCommand implements CommandClass {

	@Command(name = "report", aliases = { "reports", "reportar" })
	public void reportComman(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (!member.hasGroupPermission(Group.HELPER))
			return;

		if (args.length == 0) {
			new ReportListInventory(cmdArgs.getPlayer(), 1);
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}

		Report report = CommonGeneral.getInstance().getReportManager().getReport(player.getUniqueId());

		if (report == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não foi reportado!");
			return;
		}

		new ReportInventory(sender, report);
	}

}