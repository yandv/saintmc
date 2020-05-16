package tk.yallandev.saintmc.game.games.hungergames.command;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.constructor.Gamer;

public class AdminCommand implements CommandClass {

	@Command(name = "spec", aliases = { "spectator" }, groupToUse = Group.YOUTUBERPLUS)
	public void spectatorCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;
		
		Player p = args.getPlayer();
		Gamer gamer = Gamer.getGamer(p.getUniqueId());
		String[] a = args.getArgs();
		
		if (a.length != 1) {
			p.sendMessage("§%spectator-command-prefix%§ §%spectator-command-usage%§");
			return;
		}
		
		if (a[0].equalsIgnoreCase("on")) {
			if (gamer.isSpectatorsEnabled()) {
				p.sendMessage("§%spectator-command-prefix%§ §%spectator-command-already-enable%§");
				return;
			}
			
			p.sendMessage("§%spectator-command-prefix%§ §%spectator-command-enabled%§");
			gamer.setSpectatorsEnabled(true);
			VanishAPI.getInstance().updateVanishToPlayer(p);
		} else if (a[0].equalsIgnoreCase("off")) {
			if (gamer.isSpectatorsEnabled() == false) {
				p.sendMessage("§%spectator-command-prefix%§ §%spectator-command-already-disable%§");
				return;
			}
			
			p.sendMessage("§%spectator-command-prefix%§ §%spectator-command-disabled%§");
			gamer.setSpectatorsEnabled(false);
			VanishAPI.getInstance().updateVanishToPlayer(p);
		} else {
			p.sendMessage("§%spectator-command-prefix%§ §%spectator-command-usage%§");
		}
		
	}

}
