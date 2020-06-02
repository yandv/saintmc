package br.com.saintmc.hungergames.abilities.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.GameMain;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class UltimatoController {

	private static final int RADIUS = 10;

	private Map<Player, Ultimato> ultimatoMap;
	private List<Ultimato> ultimatoList;

	private UltimatoListener listener = new UltimatoListener();

	public UltimatoController() {
		ultimatoMap = new HashMap<>();
		ultimatoList = new ArrayList<>();
	}

	public void sendUltimato(Player player, Player target) {
		Ultimato ultimato = new Ultimato(player, target);

		ultimatoMap.put(player, ultimato);
		ultimatoMap.put(target, ultimato);
		ultimatoList.add(ultimato);

		listener.register();
	}

	public void removeUltimato(Ultimato ultimato) {
		ultimatoMap.remove(ultimato.ultimato);
		ultimatoMap.remove(ultimato.player);
		ultimatoList.remove(ultimato);

		if (ultimatoList.isEmpty())
			listener.unregister();
	}

	public boolean isInFight(Player player) {
		return ultimatoMap.containsKey(player);
	}

	public Ultimato getUltimato(Player player) {
		return ultimatoMap.get(player);
	}

	public class Ultimato {

		private Player ultimato;
		private Player player;

		private Location ultimatoLocation;
		private List<Location> borderLocation;

		private int time;

		public Ultimato(Player ultimato, Player player) {
			this.ultimato = ultimato;
			this.player = player;

			this.ultimatoLocation = ultimato.getLocation();
			this.borderLocation = new ArrayList<>();

			for (double t = 0; t < 50; t += 0.5) {
				float x = (float) (RADIUS * Math.sin(t));
				float z = (float) (RADIUS * Math.cos(t));

				borderLocation.add(ultimatoLocation.clone().add(x, 1.5, z));
				borderLocation.add(ultimatoLocation.clone().add(x, 2, z));
				borderLocation.add(ultimatoLocation.clone().add(x, 2.5, z));
			}
		}

		public void handleFinish(Player death) {
			if (death == null) {
				removeUltimato(null);
				return;
			}

			removeUltimato(this);
		}

		public void handleMove() {
			double squaredRadius = RADIUS * RADIUS;

			for (Player player : Bukkit.getOnlinePlayers()) {
				Location to = player.getLocation();
				double distX = to.getX() - ultimatoLocation.getX();
				double distZ = to.getZ() - ultimatoLocation.getZ();

				double distance = (distX * distX) + (distZ * distZ);

				if (isInFight(player)) {
					if (distance > squaredRadius) {
						player.setVelocity(ultimatoLocation.toVector().subtract(player.getLocation().toVector())
								.normalize().multiply(1.2));

						player.sendMessage("§cVocê precisa esperar mais " + StringUtils.format(60 - time)
								+ " para sair do ultimato!");
					}
				} else {
					if (distance < squaredRadius) {
						player.setVelocity(ultimatoLocation.toVector().add(player.getLocation().toVector()).normalize()
								.multiply(1.8).setY(0));
					}
				}
			}
		}

		public void handleParticles() {
			for (Location particleLocation : borderLocation) {
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true,
						(float) particleLocation.getX(), (float) particleLocation.getY(),
						(float) particleLocation.getZ(), 0, 0, 0, 0, 1);

				for (Player player : Bukkit.getOnlinePlayers()) {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				}
			}
		}

		public void pulse() {
			time++;

			if (time == 60) {
				handleFinish(null);
			}
		}

		public boolean isInUltimato(Player player) {
			return player == this.player || player == ultimato;
		}
	}

	public class UltimatoListener implements Listener {

		private boolean registered;

		@EventHandler
		public void onUpdate(UpdateEvent event) {
			if (event.getType() == UpdateType.SECOND) {
				ultimatoMap.values().forEach(Ultimato::pulse);
			} else {
				if (event.getCurrentTick() % 3 != 0)
					return;

				for (Ultimato ultimato : ultimatoList) {
					ultimato.handleParticles();
					ultimato.handleMove();

					if (event.getType() == UpdateType.SECOND)
						ultimato.pulse();
				}
			}
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onPlayerDamage(PlayerDamagePlayerEvent event) {
			Player damager = event.getDamager();
			Player player = event.getPlayer();

			if (isInFight(damager)) {
				if (isInFight(player)) {
					Ultimato gladiator = getUltimato(damager);

					if (gladiator.isInUltimato(player)) {
						event.setCancelled(false);
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}

		@EventHandler
		public void onPlayerDeath(PlayerDeathEvent event) {
			Player player = event.getEntity();

			if (isInFight(player))
				getUltimato(player).handleFinish(player);
		}

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player))
				getUltimato(player).handleFinish(player);
		}

		public void register() {
			if (!registered) {
				Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
				registered = true;
			}
		}

		public void unregister() {
			if (registered) {
				HandlerList.unregisterAll(this);
				registered = false;
			}
		}

	}

}
