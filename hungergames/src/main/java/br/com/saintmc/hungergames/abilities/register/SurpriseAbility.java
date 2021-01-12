package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.player.PlayerItemReceiveEvent;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class SurpriseAbility extends Ability {

	private Set<UUID> surpriseList = new HashSet<>();

	public SurpriseAbility() {
		super("Surprise", new ArrayList<>());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerItemReceive(PlayerItemReceiveEvent event) {
		Player player = event.getPlayer();

		if (!getMyPlayers().contains(player.getUniqueId()))
			return;

		if (surpriseList.contains(player.getUniqueId()))
			return;

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
		KitType kitType = gamer.getKit(KitType.PRIMARY).getAbilities().contains(this) ? KitType.PRIMARY
				: KitType.SECONDARY;

		List<Kit> list = new ArrayList<>(GameGeneral.getInstance().getKitController().getAllKits());

		if (ServerConfig.getInstance().isSurpriseDisable()) {
			if (GameMain.DOUBLEKIT)
				list.removeIf(kit -> gamer.hasKit(kitType == KitType.PRIMARY ? KitType.SECONDARY : KitType.PRIMARY)
						&& gamer.getKit(kitType == KitType.PRIMARY ? KitType.SECONDARY : KitType.PRIMARY)
								.isNotCompatible(kit.getClass()));

			list.removeIf(kit -> ServerConfig.getInstance().isDisabled(kit, kitType)
					|| kit.getName().equalsIgnoreCase("surprise"));

			if (list.isEmpty()) {
				player.sendMessage("§cTodos os kits estão desativados!");
				return;
			}
		}

		surpriseList.add(player.getUniqueId());
		Kit kit = list.get(CommonConst.RANDOM.nextInt(list.size()));
		GameGeneral.getInstance().getKitController().setKit(player, kit, kitType);
		kit.registerAbilities(player);
		player.sendMessage("§aO surprise escolheu " + NameUtils.formatString(kit.getName()));
	}
}
