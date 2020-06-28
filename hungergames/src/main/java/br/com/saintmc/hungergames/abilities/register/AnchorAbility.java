package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class AnchorAbility extends Ability {

	public AnchorAbility() {
		super("Anchor", new ArrayList<>());
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		if (GameGeneral.getInstance().getGameState() != GameState.GAMETIME)
			return;

		Player player = event.getPlayer();
		Player damager = event.getDamager();

		if (hasAbility(player) || hasAbility(damager)) {
			if (!GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying()
					&& !GameGeneral.getInstance().getGamerController().getGamer(damager).isNotPlaying()) {
				player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);

				velocityPlayer(player);
				velocityPlayer(damager);
			}
		}
	}

	private void velocityPlayer(Player player) {
		player.setVelocity(new Vector(0, 0, 0));

		new BukkitRunnable() {
			public void run() {
				player.setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskLater(GameMain.getPlugin(), 1L);
	}
}
