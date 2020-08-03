package tk.yallandev.saintmc.bukkit.api.character;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import tk.yallandev.saintmc.bukkit.BukkitMain;

public class CharacterListener implements Listener {

	public CharacterListener() {
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(BukkitMain.getInstance(), PacketType.Play.Client.USE_ENTITY) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (event.isCancelled())
							return;

						Player player = event.getPlayer();

						if (event.getPacket().getEntityUseActions().read(0) == EntityUseAction.INTERACT) {
							int entityId = event.getPacket().getIntegers().read(0);

							Character character = Character.getCharacter(entityId);

							if (character != null)
								character.getInteractHandler().onInteract(player);
						}

					}

				});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Character.getCharacters().forEach(character -> character.show(event.getPlayer()));
	}

}
