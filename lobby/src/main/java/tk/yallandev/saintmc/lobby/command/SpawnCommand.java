package tk.yallandev.saintmc.lobby.command;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;

public class SpawnCommand implements CommandClass {

	@Command(name = "spawn")
	public void spawnCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();

		if (LobbyMain.getInstance().getPlayerManager().isCombat(player))
			LobbyMain.getInstance().getPlayerManager().getGamer(player).setCombat(false);

		cmdArgs.getPlayer().teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
		PlayerListener.getPlayerListener().addItem(player,
				CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));
	}

}
