package tk.yallandev.saintmc.bukkit.api.client.lunar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.client.CustomClient;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.utils.BufferUtils;

public class LunarClient extends CustomClient {

	public LunarClient(BukkitMember member) {
		super(member);
	}

	public void updateServerName(String name) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		os.write(11);
		os.write(BufferUtils.writeString(name));
		os.close();

		getMember().getPlayer().sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", os.toByteArray());
	}

	@Override
	public void sendTitle(String title, String subTitle, float size, int duration, int fadeIn, int fadeOut)
			throws IOException {

		ByteArrayOutputStream t = new ByteArrayOutputStream();

		t.write(14);

		t.write(BufferUtils.writeString("normal"));
		t.write(BufferUtils.writeString(title));
		t.write(BufferUtils.writeFloat(size));
		t.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(duration)));
		t.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(fadeIn)));
		t.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(fadeOut)));

		t.close();

		ByteArrayOutputStream sT = new ByteArrayOutputStream();

		sT.write(14);

		sT.write(BufferUtils.writeString("subtitle"));
		sT.write(BufferUtils.writeString(subTitle));
		sT.write(BufferUtils.writeFloat(size));
		sT.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(duration)));
		sT.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(fadeIn)));
		sT.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(fadeOut)));

		sT.close();

		getMember().getPlayer().sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", t.toByteArray());
		getMember().getPlayer().sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", sT.toByteArray());
	}

	@Override
	public void sendNotification(String message, int delay, NotificationLevel level) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		os.write(9);
		os.write(BufferUtils.writeString(message));
		os.write(BufferUtils.writeLong(TimeUnit.SECONDS.toMillis(delay)));
		os.write(BufferUtils.writeString(level.name()));

		os.close();

		getMember().getPlayer().sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", os.toByteArray());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendCooldown(String cooldownName, Material material, int seconds) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		os.write(3);
		os.write(BufferUtils.writeString(cooldownName));
		os.write(BufferUtils.writeLong(seconds * 1000l));
		os.write(BufferUtils.writeInt(material.getId()));

		os.close();

		getMember().getPlayer().sendPluginMessage(BukkitMain.getInstance(), "Lunar-Client", os.toByteArray());
	}

}
