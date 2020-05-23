package br.com.saintmc.hungergames.listener.invincibility;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.gamer.Gamer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;

public class GameListener extends br.com.saintmc.hungergames.listener.GameListener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerLoginEvent event) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer());
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(gamer.getUniqueId());
		
		if (gamer.isTimeout()) {
			if (member.hasGroupPermission(GameMain.SPECTATOR_GROUP)) {
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
	}

}
