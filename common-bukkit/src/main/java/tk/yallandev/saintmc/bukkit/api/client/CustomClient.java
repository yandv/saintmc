package tk.yallandev.saintmc.bukkit.api.client;

import java.io.IOException;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.client.lunar.NotificationLevel;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;

@AllArgsConstructor
@Getter
public abstract class CustomClient {

	private BukkitMember member;

	public abstract void sendTitle(String title, String subTitle, float size, int duration, int fadeIn, int fadeOut)
			throws IOException;

	public abstract void sendNotification(String message, int delay, NotificationLevel level) throws IOException;

	public abstract void sendCooldown(String cooldownName, Material material, int seconds) throws IOException;

}
