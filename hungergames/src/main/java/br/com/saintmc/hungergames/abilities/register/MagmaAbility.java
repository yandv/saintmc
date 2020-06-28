package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import net.minecraft.server.v1_8_R3.DamageSource;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;

public class MagmaAbility extends Ability {

	public MagmaAbility() {
		super("Magma", new ArrayList<>());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!hasAbility(player))
			return;

		if (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damager = event.getDamager();

		if (!hasAbility(damager))
			return;

		Random r = new Random();
		Player damaged = event.getPlayer();

		if (r.nextInt(3) == 0) {
			damaged.setFireTicks(80);
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (!GameGeneral.getInstance().getGameState().isInvenciblity())
			if (event.getCurrentTick() % 10 == 0)
				for (UUID uniqueId : getMyPlayers()) {
					Player player = Bukkit.getPlayer(uniqueId);

					if (player == null)
						continue;

					if (GameGeneral.getInstance().getGamerController().getGamer(player).isNotPlaying()
							|| AdminMode.getInstance().isAdmin(player))
						continue;

					if (hasAbility(player)) {
						if (!player.getLocation().getBlock().getType().name().contains("WATER"))
							continue;

						((CraftPlayer) player).getHandle().damageEntity(DamageSource.DROWN, 1.0F);
					}
				}
	}

}
