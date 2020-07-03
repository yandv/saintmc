package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import tk.yallandev.saintmc.bukkit.BukkitMain;

public class SoupListener implements Listener {

	private static final long DELAY = 75l;

	/**
	 * ideia de idiota ? acho q não
	 */

	public SoupListener() {
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(BukkitMain.getInstance(), PacketType.Play.Client.BLOCK_PLACE) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						handleSoup(event.getPlayer());
					}

				});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			handleSoup(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		handleSoup(event.getPlayer());
	}

	private void handleSoup(Player player) {
		if (player.hasMetadata("soup"))
			if (player.getMetadata("soup").stream().findFirst().orElse(null).asLong() > System.currentTimeMillis()) {
				return;
			}

		if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
			if (((Damageable) player).getHealth() < ((Damageable) player).getMaxHealth()
					|| player.getFoodLevel() < 20) {
				int restores = 7;

				if (((Damageable) player).getHealth() < ((Damageable) player).getMaxHealth())
					if (((Damageable) player).getHealth() + restores <= ((Damageable) player).getMaxHealth())
						player.setHealth(((Damageable) player).getHealth() + restores);
					else
						player.setHealth(((Damageable) player).getMaxHealth());

				if (player.getFoodLevel() + restores <= 20) {
					player.setFoodLevel(player.getFoodLevel() + restores);
					player.setSaturation(5f);
				} else {
					player.setFoodLevel(20);
					player.setSaturation(5f);
				}

				player.getItemInHand().setType(Material.BOWL);
				player.getItemInHand().setAmount(1);
				player.setMetadata("soup",
						new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + DELAY));
			}
		}
	}
}
