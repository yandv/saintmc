package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MonkAbility extends Ability {

	public MonkAbility() {
		super("Monk", Arrays.asList(new ItemBuilder().type(Material.BLAZE_ROD).name("§aMonk Rod").build()));
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return;
		
		Player p = e.getPlayer();
		
		if (!hasAbility(p))
			return;
		
		if (!isAbilityItem(p.getItemInHand()))
			return;

		Player clicked = (Player) e.getRightClicked();

		if (GameState.isInvincibility(GameGeneral.getInstance().getGameState())) {
			p.sendMessage("§cVocê não pode usar isto agora!");
			return;
		}

		if (isCooldown(p)) {
			return;
		}

		addCooldown(p.getUniqueId(), 8l);

		int randomN = new Random().nextInt(36);

		ItemStack atual = (clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null);
		ItemStack random = (clicked.getInventory().getItem(randomN) != null ? clicked.getInventory().getItem(randomN).clone() : null);

		if (random == null) {
			clicked.getInventory().setItem(randomN, atual);
			clicked.setItemInHand(null);
		} else {
			clicked.getInventory().setItem(randomN, atual);
			clicked.getInventory().setItemInHand(random);
		}
	}

}
