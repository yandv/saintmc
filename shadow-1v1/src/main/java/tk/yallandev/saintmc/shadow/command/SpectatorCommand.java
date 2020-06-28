package tk.yallandev.saintmc.shadow.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.shadow.GameGeneral;
import tk.yallandev.saintmc.shadow.challenge.Challenge;

public class SpectatorCommand implements CommandClass {

	@Command(name = "spectator", aliases = { "espectar", "spec" }, groupToUse = Group.VIP)
	public void spectatorCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(
					" §e* Use §a/" + cmdArgs.getLabel() + " <playerName>§f para espectar o combate de alguém.");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			return;
		}

		if (GameGeneral.getInstance().getChallengeController().containsKey(target)) {
			Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(target);

			challenge.spectate(player);
		} else {
			player.sendMessage("§cO jogador " + target.getName() + " não está em combate!");
		}
	}

}
