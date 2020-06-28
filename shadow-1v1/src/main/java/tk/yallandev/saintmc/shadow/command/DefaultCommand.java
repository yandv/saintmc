package tk.yallandev.saintmc.shadow.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.shadow.GameGeneral;

public class DefaultCommand implements CommandClass {

	@Command(name = "spawn")
	public void spawnCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();

		if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
			player.sendMessage("§cVocê está em um 1v1!");
			return;
		}

		Bukkit.getPluginManager().callEvent(
				new PlayerRespawnEvent(player, BukkitMain.getInstance().getLocationFromConfig("spawn"), false));
	}

}
