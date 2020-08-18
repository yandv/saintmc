package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class HulkKit extends Kit {

	public HulkKit() {
		super("Hulk", "Pegue seus inimigos em suas costas e lançe-os para longe", Material.SADDLE, new ArrayList<>());
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player))
			if (event.getRightClicked() instanceof Player) {
				Player clicked = (Player) event.getRightClicked();

				if (!GameMain.getInstance().getGamerManager().getGamer(clicked.getUniqueId()).isSpawnProtection())
					if (!player.isInsideVehicle() && !clicked.isInsideVehicle()
							&& player.getItemInHand().getType() == Material.AIR) {

						if (isCooldown(player))
							return;

						addCooldown(player, 12l);
						player.setPassenger((Entity) clicked);
					}
			}
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player player = event.getPlayer();
		Player hulk = event.getDamager();

		if (hulk.getPassenger() != null && hulk.getPassenger() == player && hasAbility(hulk)
				&& hulk.getPassenger() == player) {
			event.setCancelled(true);
			player.setSneaking(true);

			Vector v = hulk.getEyeLocation().getDirection().multiply(1.6F);
			v.setY(0.6D);
			player.setVelocity(v);

			new BukkitRunnable() {

				@Override
				public void run() {
					player.setSneaking(false);
				}
			}.runTaskLater(GameMain.getInstance(), 10l);
		}
	}

}
