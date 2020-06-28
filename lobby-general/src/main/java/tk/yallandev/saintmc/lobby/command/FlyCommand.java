package tk.yallandev.saintmc.lobby.command;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;

public class FlyCommand implements CommandClass {

	@Command(name = "fly", groupToUse = Group.VIP)
	public void flyCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();

		player.setAllowFlight(!player.getAllowFlight());
		player.sendMessage(player.getAllowFlight() ? "§aVocê ativou o fly!" : "§cVocê desativou o fly!");

		if (player.getAllowFlight())
			player.setFlying(true);
	}

}
