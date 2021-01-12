package br.com.saintmc.hungergames.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.kit.PlayerSelectKitEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class KitListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		if (event.getKit() == null)
			return;

		Player player = event.getPlayer();

		if (ServerConfig.getInstance().isDisabled(event.getKit(), event.getKitType())) {
			player.sendMessage("§cO Kit " + NameUtils.formatString(event.getKit().getName()) + " está desativado!");
			event.setCancelled(true);
			return;
		}

		Kit kit = event.getKit();

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (!GameGeneral.getInstance().getGameState().isPregame()) {
			if (GameGeneral.getInstance().getGameState() == GameState.GAMETIME) {
				if (GameGeneral.getInstance().getTime() > 300) {
					player.sendMessage("§cVocê não pode mais selecionar kit!");
					event.setCancelled(true);
					return;
				}
			}

			if (Member.hasGroupPermission(player.getUniqueId(), Group.PRO)) {
				if (!gamer.isNoKit(event.getKitType())) {
					player.sendMessage("§cVocê não pode mais selecionar kit!");
					event.setCancelled(true);
					return;
				}
			} else {
				player.sendMessage("§cVocê não pode mais selecionar kit!");
				event.setCancelled(true);
				return;
			}
		}

		KitType verifyKit = (event.getKitType() == KitType.PRIMARY) ? KitType.SECONDARY : KitType.PRIMARY;

		if (gamer.getKit(event.getKitType()) == kit || gamer.getKit(verifyKit) == kit) {
			Title.send(player, " ", "§cVocê já está com esse kit!", SimpleTitle.class);
			player.sendMessage("§cVocê já está com o kit §c" + NameUtils.formatString(kit.getName()) + "§f!");
			event.setCancelled(true);
			return;
		}

		if (gamer.hasKit(verifyKit)) {
			if (kit.isNotCompatible(gamer.getKit(verifyKit).getClass())) {
				player.sendMessage("§cO Kit " + NameUtils.formatString(kit.getName()) + " não é compatível com o "
						+ NameUtils.formatString(gamer.getKitName(verifyKit)) + "!");
				event.setCancelled(true);
				return;
			}
		}

		if (GameMain.DOUBLEKIT) {
			if (event.getKitType() == KitType.SECONDARY) {
				if (!gamer.hasKit(kit.getName().toLowerCase())) {
					event.setCancelled(true);
					player.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f em §a"
							+ CommonConst.STORE + "§f!");
					return;
				}
			}
		} else {
			if (event.getKitType() == KitType.SECONDARY) {
				event.setCancelled(true);
				return;
			}

			if (!gamer.hasKit(kit.getName().toLowerCase())) {
				event.setCancelled(true);
				player.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f em §a"
						+ CommonConst.STORE + "§f!");
				return;
			}
		}

		Title.send(player, "§a" + NameUtils.formatString(kit.getName()), "§fSelecionado com sucesso!",
				SimpleTitle.class);
		player.sendMessage("§aVocê selecionou o kit " + NameUtils.formatString(kit.getName()) + "!");
		gamer.removeNoKit(event.getKitType());
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		if (GameGeneral.getInstance().getGameState() != GameState.GAMETIME)
			return;

		Player entity = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(entity);

		ActionBarAPI.send(event.getDamager(),
				"§7" + entity.getName() + " §8- §a"
						+ (GameMain.DOUBLEKIT
								? gamer.hasKit(KitType.SECONDARY)
										? NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)) + "/"
												+ NameUtils.formatString(gamer.getKitName(KitType.SECONDARY))
										: NameUtils.formatString(gamer.getKitName(KitType.PRIMARY))
								: NameUtils.formatString(gamer.getKitName(KitType.PRIMARY))));
	}

	public interface Verify {

		boolean verify(Kit kit);

	}

}
