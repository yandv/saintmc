package tk.yallandev.saintmc.bukkit.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.permission.Group;

public class BukkitCommandArgs extends CommandArgs {

	protected BukkitCommandArgs(CommandSender sender, String label, String[] args, int subCommand) {
		super(new BukkitCommandSender(sender), label, args, subCommand);
	}

	@Override
	public boolean isPlayer() {
		return ((BukkitCommandSender) getSender()).getSender() instanceof Player;
	}

	public Player getPlayer() {
		if (!isPlayer())
			return null;
		return (Player) ((BukkitCommandSender) getSender()).getSender();
	}

	public int broadcast(String message, Group group) {
		int x = 0;
		
		for (Member battlePlayer : CommonGeneral.getInstance().getMemberManager().getMembers()) {
			Player player = Bukkit.getPlayer(battlePlayer.getUniqueId());
			
			if (player == null)
				continue;
			
			player.sendMessage(message);
			x++;
		}
		
		return x;
	}
	
}