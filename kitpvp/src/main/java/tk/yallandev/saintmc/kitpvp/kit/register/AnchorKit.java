package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class AnchorKit extends Kit {

	public AnchorKit() {
		super("Anchor", "Se prenda ao chão e não saia dele", Material.ANVIL, 9000, new ArrayList<>());
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player player = event.getPlayer();
		Player damager = event.getDamager();

		if (hasAbility(player) || hasAbility(damager)) {
			player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);

			velocityPlayer(player);
			velocityPlayer(damager);
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
