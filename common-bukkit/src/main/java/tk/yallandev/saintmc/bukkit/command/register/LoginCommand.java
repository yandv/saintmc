package tk.yallandev.saintmc.bukkit.command.register;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;

public class LoginCommand implements CommandClass {

	@Command(name = "changepassword", aliases = { "changepw", "mudarsenha" })
	public void changepasswordCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (player.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL
				&& !player.getLoginConfiguration().isRegistred()) {
			player.sendMessage("§cVocê não pode executar esse comando!");
			return;
		}

		if (cmdArgs.getArgs().length <= 1) {
			player.sendMessage(
					" §e* §fUse §a/" + cmdArgs.getLabel() + " <sua senha> <nova senha>§f para mudar de senha!");
			return;
		}

		String password = cmdArgs.getArgs()[0];
		String newPassword = cmdArgs.getArgs()[1];

		if (player.getLoginConfiguration().getPassword().equals(password))
			if (password.equals(newPassword))
				player.sendMessage("§cSua nova senha não pode ser igual a anterior!");
			else {
				player.getLoginConfiguration().changePassword(password, newPassword);
				player.sendMessage("§aSua senha foi alterada com sucesso!");

				player.getLoginConfiguration().clearSessions();

				if (player.getLoginConfiguration().startSession(player.getLastIpAddress()))
					player.sendMessage("§aAgora você possui uma sessão ativa no servidor!");
				else {
					player.sendMessage("§cNão foi possível estabelecer uma sessão no servidor!");
					player.sendMessage("§cVocê precisa se logar novamente para ativar uma sessão!");
				}
			}
		else
			player.sendMessage("§cSua senha antiga não está correta!");
	}

}
