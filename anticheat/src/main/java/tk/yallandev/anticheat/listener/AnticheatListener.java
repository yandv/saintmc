package tk.yallandev.anticheat.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;

import tk.yallandev.anticheat.AnticheatController;
import tk.yallandev.anticheat.stats.PlayerStats;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class AnticheatListener implements Listener {

	public AnticheatListener() {

		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(AnticheatController.getInstance().getPlugin(), PacketType.Play.Client.USE_ENTITY) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (event.getPlayer() == null) {
							event.setCancelled(true);
							return;
						}

						PacketContainer packetContainer = event.getPacket();
						Player player = event.getPlayer();

						if (player == null) {
							return;
						}

						EnumWrappers.EntityUseAction action = packetContainer.getEntityUseActions().read(0);

						int n = (Integer) packetContainer.getIntegers().read(0);

						Entity entity = null;

						for (Entity entity2 : player.getWorld().getEntities())
							if (entity2.getEntityId() == n)
								entity = entity2;
						
						PlayerStats playerStats = AnticheatController.getInstance().getPlayerController()
								.getPlayerStats(player.getUniqueId());
						
						playerStats.handleHit(false);
					}

				});

	}

	@EventHandler
	public void onPlayerPacket(PlayerAnimationEvent event) {
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING)
			return;

		Player player = event.getPlayer();

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			return;
		}

		PlayerStats playerStats = AnticheatController.getInstance().getPlayerController()
				.getPlayerStats(player.getUniqueId());
		playerStats.handleClick();
	}

	@EventHandler
	public void onUpdate(UpdateEvent updateEvent) {
		if (updateEvent.getType() == UpdateType.SECOND) {
			for (PlayerStats playerStats : AnticheatController.getInstance().getPlayerController().getStoreMap().values()) {
				
			}
		}
	}

}
