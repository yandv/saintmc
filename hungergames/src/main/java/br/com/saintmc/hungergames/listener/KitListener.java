package br.com.saintmc.hungergames.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.kit.PlayerSelectKitEvent;
import br.com.saintmc.hungergames.gamer.Gamer;
import br.com.saintmc.hungergames.kit.KitType;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class KitListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		
		if (event.getKit() == null)
			return;

		if (GameMain.DOUBLEKIT) {
			if (event.getKitType() == KitType.SECONDARY) {
				
				Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer());
				
				if (!gamer.hasKit(event.getKit().getName().toLowerCase())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(event.getKit().getName()) + "§f em §a"
							+ CommonConst.STORE + "§f!");
					return;
				}
			}
		} else {
			
			if (event.getKitType() == KitType.SECONDARY) {
				event.setCancelled(true);
				event.getPlayer().sendMessage("§c§l> §fO kit secundário está desativado!");
				return;
			}
			
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer());
			
			if (!gamer.hasKit(event.getKit().getName().toLowerCase())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(event.getKit().getName()) + "§f em §a"
						+ CommonConst.STORE + "§f!");
				return;
			}
			
		}

	}

}
