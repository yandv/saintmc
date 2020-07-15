package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.server.EnumParticle;
import net.minecraft.server.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class StomperKit extends Kit {

	public StomperKit() {
		super("Stomper", "Pise em cima de seus inimigos", Material.IRON_BOOTS, new ArrayList<>());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		
		Player stomper = (Player) event.getEntity();

		if (!hasAbility(stomper))
			return;

		DamageCause cause = event.getCause();

		if (cause != DamageCause.FALL)
			return;

		double dmg = event.getDamage();

		for (Player stompado : Bukkit.getOnlinePlayers()) {
			if (stompado.getUniqueId() == stomper.getUniqueId())
				continue;

			if (stompado.isDead())
				continue;

			if (GameMain.getInstance().getGamerManager().getGamer(stompado.getUniqueId()).isSpawnProtection())
				continue;

			if (stompado.getLocation().distance(stomper.getLocation()) > 3)
				continue;

			double dmg2 = dmg;

			if (stompado.isSneaking() && dmg2 > 8)
				dmg2 = 8;

			if (stompado.getHealth() - dmg2 <= 0)
				stompado.damage(Integer.MAX_VALUE, stomper);
			else {
				stompado.damage(dmg2, stomper);
				stompado.setHealth(stompado.getHealth() - dmg2);
			}
		}

		for (int x = (int) -3; x <= 3; x++) {
			for (int z = (int) -3; z <= 3; z++) {
				Location effect = stomper.getLocation().clone().add(x, 0, z);

				if (effect.distance(stomper.getLocation()) > 3)
					continue;

				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true,
						(float) effect.getX(), (float) effect.getY(), (float) effect.getZ(), 0.1F, 0.1F, 0.1F, 1, 30);

				Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer.canSee(stomper))
						.forEach(viewer -> ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet));
			}
		}

		stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1, 1);

		if (event.getDamage() < 4.0D)
			return;
		
		if (hasAbility(stomper)) {
			event.setCancelled(true);
			stomper.damage(4.0D);
		}
	}

}
