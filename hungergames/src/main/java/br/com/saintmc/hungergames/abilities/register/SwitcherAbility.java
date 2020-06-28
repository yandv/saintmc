package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class SwitcherAbility extends Ability {

	public SwitcherAbility() {
		super("Switcher",
				Arrays.asList(new ItemBuilder().type(Material.SNOW_BALL).name("§aSwitcher").amount(1).build()));
	}

	@EventHandler
	public void onProjectileLaunch(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (hasAbility(player) && isAbilityItem(player.getItemInHand())) {
				event.setCancelled(true);
				player.updateInventory();

				if (isCooldown(player))
					return;

				if (GameState.isInvincibility(GameGeneral.getInstance().getGameState())) {
					player.sendMessage("§cVocê não pode usar o seu kit durante a invencibilidade!");
					return;
				}

				Snowball ball = player.launchProjectile(Snowball.class);
//				ball.setShooter(player);
//				ball.setVelocity(player.getLocation().getDirection().multiply(1.5));

				ball.setMetadata("switch", new FixedMetadataValue(GameMain.getInstance(), player));
				addCooldown(player, 5);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
			Player player = (Player) event.getDamager().getMetadata("switch").get(0).value();

			if (player == null)
				return;

			Location loc = event.getEntity().getLocation().clone();
			event.getEntity().teleport(player.getLocation().clone());
			player.teleport(loc);
		}
	}

}
