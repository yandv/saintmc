package tk.yallandev.saintmc.kitpvp.command;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KitCommand implements CommandClass {
	
	@Command(name = "kit")
	public void kit(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;
		
		Player p = cmdArgs.getPlayer();
		String[] a = cmdArgs.getArgs();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(p.getUniqueId());
		
		if (a.length == 0) {
			p.sendMessage(" §e* §fUse §a/kit <kitName>§f para selecionar um kit.");
			return;
		}
		
		if (!gamer.getWarp().getWarpSettings().isKitEnabled()) {
			p.sendMessage(" §c* §fEssa warp não permite kit!");
			return;
		}
		
		Kit kit = GameMain.getInstance().getKitManager().getKit(a[0]);
		
		if (kit == null) {
			p.sendMessage(" §c* §fO kit §a" + a[0] + "§f não existe!");
			return;
		}
		
		if (gamer.hasKit()) {
			p.sendMessage(" §c* §fVocê já está §ausando§f um kit!");
			return;
		}
		
		gamer.setKit(kit);
		GameMain.getInstance().getKitManager().selectKit(p, kit);
		p.sendMessage(" §a* §fVocê selecionou o kit §a" + kit.getKitName() + "§f!");
	}

}
