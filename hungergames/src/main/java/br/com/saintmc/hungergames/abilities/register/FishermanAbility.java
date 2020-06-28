package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class FishermanAbility extends Ability {

	public FishermanAbility() {
		super("Fisherman", Arrays.asList(new ItemBuilder().type(Material.FISHING_ROD).name("§aFisherman").build()));
	}

	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		if (!(event.getCaught() instanceof LivingEntity))
			return;

		Player player = event.getPlayer();

		if (hasAbility(player)) {
			if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY)
				event.getCaught().teleport(player.getLocation());

			player.getItemInHand().setDurability((short) 0);
		}
	}

}
