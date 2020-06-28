package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class PyroAbility extends Ability {

	public PyroAbility() {
		super("Pyro", Arrays.asList(new ItemBuilder().name("§aPyro").type(Material.FLINT_AND_STEEL).build(),
				new ItemBuilder().name("§aPyro").type(Material.FIREBALL).amount(3).build()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!hasAbility(player))
			return;

		if (!isAbilityItem(player.getItemInHand()) && player.getItemInHand().getType() != Material.FIREBALL)
			return;

		event.setCancelled(true);

		if (event.getAction() != Action.RIGHT_CLICK_AIR)
			return;

		if (isCooldown(player))
			return;

		ItemStack item = player.getItemInHand();

		item.setAmount(item.getAmount() - 1);

		if (item.getAmount() == 0)
			player.setItemInHand(new ItemStack(Material.AIR));

		Fireball ball = event.getPlayer().launchProjectile(Fireball.class);
		ball.setIsIncendiary(false);
		ball.setYield(2.0F);
		addCooldown(player, 15);
	}

}
