package tk.yallandev.saintmc.kitpvp.kit.register;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class StomperKit extends Kit {

	public StomperKit() {
		super("Stomper", "Pise em cima de seus inimigos", Material.IRON_BOOTS);
	}

	@EventHandler
	public void onStomper(EntityDamageEvent event) {
		Entity entityStomper = event.getEntity();

		if (!(entityStomper instanceof Player))
			return;

		Player stomper = (Player) entityStomper;

		if (!hasAbility(stomper))
			return;

		DamageCause cause = event.getCause();

		if (cause != DamageCause.FALL)
			return;

		double dmg = event.getDamage();

		for (Player stompado : Bukkit.getOnlinePlayers()) {
			if (stompado.getUniqueId() == stomper.getUniqueId())
				continue;

			if (GameMain.getInstance().getGamerManager().getGamer(stompado.getUniqueId()).isSpawnProtection())
				continue;

			if (stompado.getLocation().distance(stomper.getLocation()) > 4)
				continue;

			double dmg2 = dmg;

			if (stompado.isSneaking() && dmg2 > 8)
				dmg2 = 8;

			stompado.damage(dmg2, stomper);
		}

		for (int x = (int) -4; x <= 4; x++) {
			for (int z = (int) -4; z <= 4; z++) {
				Location effect = stomper.getLocation().clone().add(x, 0, z);

				if (effect.distance(stomper.getLocation()) > 4)
					continue;

				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SMOKE_NORMAL, true,
						(float) effect.getX(), (float) effect.getY(), (float) effect.getZ(), 0.1F, 0.1F, 0.1F, 1, 30);

				Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer.canSee(stomper))
						.forEach(viewer -> ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet));
			}
		}

		stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1, 1);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		Player p = (Player) event.getEntity();

		if (event.getDamage() < 4.0D)
			return;

		if (hasAbility(p)) {
			event.setCancelled(true);
			p.damage(4.0D);
		}
	}

	@Override
	public void applyKit(Player player) {

	}

}
