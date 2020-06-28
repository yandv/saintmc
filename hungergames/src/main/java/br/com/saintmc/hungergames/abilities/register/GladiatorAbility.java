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
import br.com.saintmc.hungergames.abilities.constructor.GladiatorController;
import br.com.saintmc.hungergames.abilities.constructor.GladiatorController.Gladiator;
import br.com.saintmc.hungergames.event.ability.ChallengeGladiatorEvent;
import br.com.saintmc.hungergames.event.ability.ChallengeUltimatoEvent;
import br.com.saintmc.hungergames.event.ability.GladiatorScapeEvent;
import br.com.saintmc.hungergames.event.ability.PlayerAjninTeleportEvent;
import br.com.saintmc.hungergames.event.ability.PlayerNinjaTeleportEvent;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class GladiatorAbility extends Ability {

	public GladiatorAbility() {
		super("Gladiator", Arrays.asList(new ItemBuilder().name("§aGladiator").type(Material.IRON_FENCE).build()));
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player))
			return;

		if (event.getPlayer().getItemInHand() == null)
			return;

		Player player = event.getPlayer();

		if (hasAbility(player) && isAbilityItem(player.getItemInHand())) {
			event.setCancelled(true);

			if (isCooldown(player))
				return;

			Player target = (Player) event.getRightClicked();
			
			if (isCooldown(target)) {
				return;
			}

			if (GameState.isInvincibility(GameGeneral.getInstance().getGameState()))
				return;

			ChallengeGladiatorEvent challengeGladiatorEvent = new ChallengeGladiatorEvent(player, target);

			challengeGladiatorEvent.setCancelled(!(!GladiatorController.GLADIATOR_CONTROLLER.isInFight(player)
					&& !GladiatorController.GLADIATOR_CONTROLLER.isInFight(target)));
			Bukkit.getPluginManager().callEvent(challengeGladiatorEvent);

			if (!challengeGladiatorEvent.isCancelled())
				GladiatorController.GLADIATOR_CONTROLLER.sendGladiator(player, target);
		}
	}

	@EventHandler
	public void onGladiatorScape(GladiatorScapeEvent event) {
		addCooldown(event.getGladiator(), 7);
	}

	@EventHandler
	public void onChallengeUltimato(ChallengeUltimatoEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		if (hasAbility(target)) {
			event.setCancelled(true);
			player.sendMessage("§cVocê não pode puxar Gladiator no Ultimato!");
			return;
		}

		if (GladiatorController.GLADIATOR_CONTROLLER.isInFight(target)
				|| GladiatorController.GLADIATOR_CONTROLLER.isInFight(player)) {
			event.setCancelled(true);
			player.sendMessage("§cEsse jogador está em combate no Gladiator!");
		}
	}

	@EventHandler
	public void onPlayerNinjaTeleport(PlayerNinjaTeleportEvent event) {
		if (hasAbility(event.getTarget()))
			if (GladiatorController.GLADIATOR_CONTROLLER.isInFight(event.getTarget())) {
				Gladiator gladiator = GladiatorController.GLADIATOR_CONTROLLER.getGladiator(event.getTarget());

				if (!gladiator.isInGladiator(event.getPlayer())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage("§cO jogador que você hitou foi para um Gladiator!");
				}
			}
	}

	@EventHandler
	public void onPlayerAjninTeleport(PlayerAjninTeleportEvent event) {
		if (hasAbility(event.getTarget()))
			if (GladiatorController.GLADIATOR_CONTROLLER.isInFight(event.getTarget())) {
				Gladiator gladiator = GladiatorController.GLADIATOR_CONTROLLER.getGladiator(event.getTarget());

				if (!gladiator.isInGladiator(event.getPlayer())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage("§cO jogador que você hitou foi para um Gladiator!");
				}
			}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (e.getAction() != Action.PHYSICAL && hasAbility(player) && isAbilityItem(e.getItem())) {
			player.updateInventory();
			e.setCancelled(true);
		}
	}

}
