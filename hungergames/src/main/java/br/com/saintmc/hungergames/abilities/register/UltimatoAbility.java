package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.abilities.constructor.UltimatoController;
import br.com.saintmc.hungergames.event.ability.ChallengeGladiatorEvent;
import br.com.saintmc.hungergames.event.ability.ChallengeUltimatoEvent;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class UltimatoAbility extends Ability {

	private UltimatoController controller;

	public UltimatoAbility() {
		super("Ultimato", Arrays
				.asList(new ItemBuilder().name("§cUltimato").type(Material.STAINED_GLASS_PANE).durability(14).build()));
		controller = new UltimatoController();
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return;

		Player player = e.getPlayer();

		if (!hasAbility(player))
			return;

		if (e.getPlayer().getItemInHand() == null)
			return;

		if (!isAbilityItem(e.getPlayer().getItemInHand()))
			return;

		Player target = (Player) e.getRightClicked();

		if (GameState.isInvincibility(GameGeneral.getInstance().getGameState()))
			return;

		e.setCancelled(true);

		ChallengeUltimatoEvent event = new ChallengeUltimatoEvent(player, target);

		event.setCancelled(!(!controller.isInFight(player) && !controller.isInFight(target)));
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled())
			controller.sendUltimato(player, target);
	}

	@EventHandler
	public void onChallengeGladiator(ChallengeGladiatorEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		if (controller.isInFight(target) || controller.isInFight(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if ((e.getAction() != Action.PHYSICAL) && (hasAbility(player)) && (e.getPlayer().getItemInHand() != null)
				&& (e.getPlayer().getItemInHand().getType() == Material.IRON_FENCE)) {
			e.getPlayer().updateInventory();
			e.setCancelled(true);
		}
	}

}
