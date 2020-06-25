package tk.yallandev.saintmc.bukkit.command.manual;

import org.bukkit.Bukkit;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import tk.yallandev.saintmc.bukkit.event.login.PlayerRegisterEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;

public class LoginCommand implements CommandClass {

	@Command(name = "login", aliases = { "registrar" })
	public void loginCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;
		
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (player.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			player.sendMessage(" §a* §fVocê não pode §cexecutar§f esse comando!");
			return;
		}

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §a* §fUse §a/login <senha>§f para se logar!");
			return;
		}

		if (player.getLoginConfiguration().isLogged()) {
			player.sendMessage(" §a* §fVocê já está logado no servidor!");
			return;
		}

		if (!player.getLoginConfiguration().isRegistred()) {
			player.sendMessage(" §a* §fSua conta não está registrada no servidor!");
			return;
		}

		if (args[0].equals(player.getLoginConfiguration().getPassword())) {
			player.getLoginConfiguration().login(player.getLastIpAddress());
			player.sendMessage(" §a* §fSua conta foi logada com sucesso!");

			Bukkit.getPluginManager().callEvent(new PlayerChangeLoginStatusEvent(cmdArgs.getPlayer(), player, true));
		} else {
			player.sendMessage(" §a* §cSenha incorreta!");
		}
	}

	@Command(name = "register", aliases = { "registrar" })
	public void registerCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (player.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			player.sendMessage(" §a* §fVocê não pode §cexecutar§f esse comando!");
			return;
		}

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage(" §a* §fUse §a/register <senha> <repita sua senha>§f para se logar!");
			return;
		}

		if (player.getLoginConfiguration().isRegistred()) {
			player.sendMessage(" §a* §fSua conta está registrada no servidor!");
			return;
		}

		if (args[0].equals(args[1])) {
			player.sendMessage(" §a* §fSua conta foi registrada no servidor!");
			player.getLoginConfiguration().register(args[0], player.getLastIpAddress());
			
			Bukkit.getPluginManager().callEvent(new PlayerChangeLoginStatusEvent(cmdArgs.getPlayer(), player, true));
			Bukkit.getPluginManager().callEvent(new PlayerRegisterEvent(cmdArgs.getPlayer(), player));
		} else {
			sender.sendMessage(" §a* §fUse §a/register <senha> <repita sua senha>§f para se logar!");
		}
	}

}
