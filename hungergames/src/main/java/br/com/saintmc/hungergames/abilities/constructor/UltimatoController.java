package br.com.saintmc.hungergames.abilities.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.ability.PlayerStompedEvent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class UltimatoController {

	public static final UltimatoController ULTIMATO_CONTROLLER = new UltimatoController();

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

			for (double t = 0; t < 25; t += 1) {
				float x = (float) (RADIUS * Math.sin(t));
				float z = (float) (RADIUS * Math.cos(t));

				borderLocation.add(ultimatoLocation.clone().add(x, 1.5, z));
				borderLocation.add(ultimatoLocation.clone().add(x, 2, z));
				borderLocation.add(ultimatoLocation.clone().add(x, 2.5, z));
			}

			this.ultimatoLocation.getWorld().playSound(ultimatoLocation, Sound.PORTAL_TRAVEL, 1.0f, 1.0f);
		}

		public void handleFinish(Player death) {
			if (death == null) {
				removeUltimato(this);
				return;
			}

			removeUltimato(this);
		}

		public void checkMovement() {
			double squaredRadius = RADIUS * RADIUS;

			for (Player player : Bukkit.getOnlinePlayers()) {
				Location to = player.getLocation();
				double distX = to.getX() - ultimatoLocation.getX();
				double distZ = to.getZ() - ultimatoLocation.getZ();

				double distance = (distX * distX) + (distZ * distZ);

				if (isInUltimato(player)) {
					if (distance > squaredRadius) {
						if (time <= 15) {
							player.setVelocity(ultimatoLocation.toVector().subtract(player.getLocation().toVector())
									.normalize().multiply(1.2));

							if (player == ultimato)
								player.sendMessage("§cVocê precisa esperar mais " + StringUtils.format(15 - time)
										+ " para sair do ultimato!");
							else
								player.sendMessage("§cVocê precisa esperar mais " + StringUtils.format(60 - time)
										+ " para sair do ultimato!");
						} else {
							if (player == ultimato) {
								if (distance > (RADIUS + 3) * (RADIUS + 3)) {
									handleFinish(null);

									CooldownController.getInstance().addCooldown(ultimato.getUniqueId(), "Kit Ultimato",
											10);

									ultimato.sendMessage("§cO ultimato acabou porque você saiu da arena!");
									this.player.sendMessage(
											"§cO ultimato acabou porque o " + ultimato.getName() + " saiu da arena!");
								}
							} else {
								player.setVelocity(ultimatoLocation.toVector().subtract(player.getLocation().toVector())
										.normalize().multiply(1.2));

								player.sendMessage("§cVocê precisa esperar mais " + StringUtils.format(60 - time)
										+ " para sair do ultimato!");
							}
						}
					}
				} else {
					if (GameGeneral.getInstance().getGamerController().getGamer(player).isPlaying())
						if (distance < squaredRadius) {
							player.setVelocity(ultimatoLocation.toVector().add(player.getLocation().toVector())
									.normalize().multiply(1.8).setY(0));
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

			if (time == 15) {
				ultimato.sendMessage("§aAgora você pode sair da arena!");
			}

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
			if (event.getType() == UpdateType.SECOND)
				ultimatoList.iterator().forEachRemaining(Ultimato::pulse);

			if (event.getCurrentTick() % 3 == 0) {
				ultimatoList.iterator().forEachRemaining(ultimato -> {
					ultimato.handleParticles();
					ultimato.checkMovement();
				});
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerStomped(PlayerStompedEvent event) {
			Player stomper = event.getStomper();
			Player stomped = event.getPlayer();

			if (isInFight(stomped)) {
				Ultimato gladiator = getUltimato(stomped);

				if (gladiator.isInUltimato(stomper))
					event.setCancelled(false);
				else
					event.setCancelled(true);
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerInteract(PlayerInteractEvent event) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
				if (event.getClickedBlock().getType() == Material.CHEST)
					if (isInFight(event.getPlayer()))
						event.setCancelled(true);
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
