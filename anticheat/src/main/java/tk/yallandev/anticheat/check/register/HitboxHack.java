package tk.yallandev.anticheat.check.register;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import tk.yallandev.anticheat.check.Hack;

public class HitboxHack extends Hack {

	private double HITBOX_LENGTH = 1.05;

	@EventHandler
	public void onHitPlayer(EntityDamageByEntityEvent event) {
		
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getDamager();
		Player player2 = (Player) event.getDamager();
		
		System.out.println("1");

		if (!hasInHitBox((LivingEntity) player2)) {
			alert(player);
			System.out.println("2");
		}
	}

	public boolean hasInHitBox(LivingEntity livingEntity) {
		boolean bl = false;
		Vector vector = livingEntity.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
		Vector vector2 = livingEntity.getLocation().toVector().subtract(livingEntity.getLocation().toVector());

		if (!(livingEntity.getLocation().getDirection().normalize().crossProduct(vector)
				.lengthSquared() >= HITBOX_LENGTH
				&& livingEntity.getLocation().getDirection().normalize().crossProduct(vector2)
						.lengthSquared() >= this.HITBOX_LENGTH
				|| vector.normalize().dot(livingEntity.getLocation().getDirection().normalize()) < 0.0
						&& vector2.normalize().dot(livingEntity.getLocation().getDirection().normalize()) < 0.0)) {
			bl = true;
		}

		return bl;
	}

}
