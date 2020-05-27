package br.com.saintmc.hungergames.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.kit.PlayerSelectKitEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.kit.KitType;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class KitListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		if (event.getKit() == null)
			return;

		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (!GameGeneral.getInstance().getGameState().isPregame()) {
			if (GameGeneral.getInstance().getGameState() == GameState.GAMETIME) {
				if (GameGeneral.getInstance().getTime() > 300) {
					player.sendMessage("§c§l> §fVocê não pode mais selecionar kit!");
					event.setCancelled(true);
					return;
				}
			}

			if (Member.hasGroupPermission(player.getUniqueId(), Group.LIGHT)) {
				if (!gamer.isNoKit(event.getKitType())) {
					player.sendMessage("§c§l> §fVocê não pode mais selecionar kit!");
					event.setCancelled(true);
					return;
				}
			}
		}

		if (gamer.getKit(event.getKitType()) == event.getKit() || event.getKitType() == KitType.PRIMARY
				? gamer.getKit(KitType.SECONDARY) == event.getKit()
				: gamer.getKit(KitType.PRIMARY) == event.getKit()) {
			Title.send(player, " ", "§cVocê já está com esse kit!", SimpleTitle.class);
			player.sendMessage(
					" §c* §fVocê já está com o kit §c" + NameUtils.formatString(event.getKit().getName()) + "§f!");
			event.setCancelled(true);
			return;
		}

		if (GameMain.DOUBLEKIT) {
			if (event.getKitType() == KitType.SECONDARY) {
				if (!gamer.hasKit(event.getKit().getName().toLowerCase())) {
					event.setCancelled(true);
					player.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(event.getKit().getName())
							+ "§f em §a" + CommonConst.STORE + "§f!");
					return;
				}
			}
		} else {
			if (event.getKitType() == KitType.SECONDARY) {
				event.setCancelled(true);
				player.sendMessage("§c§l> §fO kit secundário está desativado!");
				return;
			}

			if (!gamer.hasKit(event.getKit().getName().toLowerCase())) {
				event.setCancelled(true);
				player.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(event.getKit().getName())
						+ "§f em §a" + CommonConst.STORE + "§f!");
				return;
			}
		}

		Title.send(player, "§a" + NameUtils.formatString(event.getKit().getName()), "§fSelecionado com sucesso!",
				SimpleTitle.class);
		player.sendMessage(
				" §a* §fVocê selecionou o kit §a" + NameUtils.formatString(event.getKit().getName()) + "§f!");

	}

}
