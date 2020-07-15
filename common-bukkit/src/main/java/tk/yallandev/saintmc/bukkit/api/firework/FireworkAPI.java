package tk.yallandev.saintmc.bukkit.api.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkAPI {

	public static org.bukkit.entity.Firework spawn(Location location, FireworkMeta fireworkMeta) {
		org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location,
				EntityType.FIREWORK);

		firework.setFireworkMeta(fireworkMeta);
		firework.detonate();

		return firework;
	}

	public static org.bukkit.entity.Firework spawn(Location location, Color color, boolean flicker) {
		org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location,
				EntityType.FIREWORK);

		FireworkMeta fireworkMeta = firework.getFireworkMeta();

		fireworkMeta.setPower(2);
		fireworkMeta.addEffect(FireworkEffect.builder().withColor(color).flicker(flicker).build());

		firework.setFireworkMeta(fireworkMeta);
		firework.detonate();

		return firework;
	}

	public static org.bukkit.entity.Firework spawn(Location location, Color color, Color fade, Type type,
			boolean flicker) {
		org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location,
				EntityType.FIREWORK);

		FireworkMeta fireworkMeta = firework.getFireworkMeta();

		fireworkMeta.setPower(2);
		fireworkMeta.addEffect(
				FireworkEffect.builder().withColor(color).flicker(flicker).withFade(fade).with(type).build());

		firework.setFireworkMeta(fireworkMeta);
		firework.detonate();

		return firework;
	}

}
