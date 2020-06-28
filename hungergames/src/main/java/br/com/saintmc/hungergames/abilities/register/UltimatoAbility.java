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
import br.com.saintmc.hungergames.scheduler.types.GameScheduler;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class UltimatoAbility extends Ability {

	public UltimatoAbility() {
		super("Ultimato", Arrays
				.asList(new ItemBuilder().name("§cUltimato").type(Material.STAINED_GLASS_PANE).durability(14).build()));
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

			Player target = (Player) event.getRightClicked();

			if (GameState.isInvincibility(GameGeneral.getInstance().getGameState()))
				return;

			if (isCooldown(player))
				return;

			ChallengeUltimatoEvent challengeUltimatoEvent = new ChallengeUltimatoEvent(player, target);

			challengeUltimatoEvent.setCancelled(!(!UltimatoController.ULTIMATO_CONTROLLER.isInFight(player)
					&& !UltimatoController.ULTIMATO_CONTROLLER.isInFight(target)));
			Bukkit.getPluginManager().callEvent(challengeUltimatoEvent);

			if (!challengeUltimatoEvent.isCancelled())
				UltimatoController.ULTIMATO_CONTROLLER.sendUltimato(player, target);
		}

	}

	@EventHandler
	public void onChallengeUltimato(ChallengeUltimatoEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		if (GameScheduler.feastLocation != null) {
			if (player.getLocation().distance(GameScheduler.feastLocation) <= 35
					|| target.getLocation().distance(GameScheduler.feastLocation) <= 35) {
				event.setCancelled(true);
				player.sendMessage("§cVocê não pode puxar no ultimato muito próximo do feast!");
				return;
			}
		}
	}

	@EventHandler
	public void onChallengeGladiator(ChallengeGladiatorEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		if (hasAbility(target)) {
			event.setCancelled(true);
			player.sendMessage("§cVocê não pode puxar Gladiator no Ultimato!");
			return;
		}

		if (UltimatoController.ULTIMATO_CONTROLLER.isInFight(target)
				|| UltimatoController.ULTIMATO_CONTROLLER.isInFight(player)) {
			event.setCancelled(true);
			player.sendMessage("§cEsse jogador está em combate no Ultimato!");
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (hasAbility(player))
			if (isAbilityItem(e.getItem()) && e.getAction() != Action.PHYSICAL) {
				player.updateInventory();
				e.setCancelled(true);
			}

		if (e.getAction() != Action.PHYSICAL && hasAbility(player) && isAbilityItem(e.getItem())) {
			player.updateInventory();
			e.setCancelled(true);
		}
	}

}
