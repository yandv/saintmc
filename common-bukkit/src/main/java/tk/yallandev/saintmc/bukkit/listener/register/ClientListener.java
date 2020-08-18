package tk.yallandev.saintmc.bukkit.listener.register;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.client.lunar.LunarClient;
import tk.yallandev.saintmc.bukkit.api.client.lunar.NotificationLevel;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.client.ClientType;
import tk.yallandev.saintmc.common.utils.BufferUtils;

public class ClientListener implements Listener {

	public ClientListener() {
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(BukkitMain.getInstance(), "Lunar-Client",
				(channel, player, bytes) -> {
					if (bytes[0] == 26) {
						final UUID uuid = BufferUtils.getUUIDFromBytes(Arrays.copyOfRange(bytes, 1, 30));

						if (!uuid.equals(player.getUniqueId())) {
							return;
						}

						BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
								.getMember(uuid);

						if (member.getCustomClient() == null) {
							member.setCustomClient(new LunarClient((BukkitMember) member));
						}

						if (member.getClientType() != ClientType.LUNAR) {
							member.sendMessage("§aYou have been authenticated with Lunar Client!");
							member.setClientType(ClientType.LUNAR);

							try {
								member.getCustomClient().sendNotification("You have been Lunar Client authenticated.",
										5, NotificationLevel.NEUTRAL);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	@EventHandler
	public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
		if (event.getChannel().equals("Lunar-Client")) {
			Player player = event.getPlayer();
			BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

			if (member != null)
				if (member.getClientType() == ClientType.LUNAR) {
					if (member.getCustomClient() == null)
						member.setCustomClient(new LunarClient((BukkitMember) member));
				} else {
					try {
						ByteArrayOutputStream os = new ByteArrayOutputStream();

						os.write(26);
						os.write(BufferUtils.getBytesFromUUID(player.getUniqueId()));
						os.write(BufferUtils.writeInt(5));

						os.close();

						player.sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", os.toByteArray());
					} catch (Exception ex) {

					}

					try {
						ByteArrayOutputStream os = new ByteArrayOutputStream();

						os.write(26);
						os.write(BufferUtils.getBytesFromUUID(player.getUniqueId()));
						os.write(BufferUtils.writeInt(-1));

						os.close();

						player.sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", os.toByteArray());
					} catch (Exception ex) {

					}
				}
		}
	}

}
