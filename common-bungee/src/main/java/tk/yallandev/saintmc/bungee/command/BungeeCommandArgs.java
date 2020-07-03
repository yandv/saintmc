package tk.yallandev.saintmc.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;

public class BungeeCommandArgs extends CommandArgs {

	protected BungeeCommandArgs(CommandSender sender, String label, String[] args, int subCommand) {
		super(sender instanceof ProxiedPlayer
				? CommonGeneral.getInstance().getMemberManager().getMember(((ProxiedPlayer) sender).getUniqueId())
				: new BungeeCommandSender(sender), label, args, subCommand);
	}

	@Override
	public boolean isPlayer() {
		return getSender() instanceof Member;
	}

	public ProxiedPlayer getPlayer() {
		if (!isPlayer())
			return null;
		return (ProxiedPlayer) ((BungeeMember) getSender()).getProxiedPlayer();
	}

}
