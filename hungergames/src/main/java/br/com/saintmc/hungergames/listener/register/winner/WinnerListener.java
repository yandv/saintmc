package br.com.saintmc.hungergames.listener.register.winner;

import javax.swing.ImageIcon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

public class WinnerListener implements Listener {

	private Player winner;

	private int time;

	public WinnerListener(Player winner) {
		this.winner = winner;
		
		if (this.winner == null) {
			Bukkit.broadcastMessage("§aNenhum jogador ganhou!");

			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getOnlinePlayers().forEach(player -> GameMain.getInstance().sendPlayerToLobby(player));

					if (Bukkit.getOnlinePlayers().size() == 0)
						Bukkit.shutdown();
				}
			}.runTaskTimer(GameMain.getInstance(), 0, 10);
		}

		Location cakeLocation = winner.getLocation().clone().add(0, winner.getLocation().getY() > 70 ? 10 : 40, 0);

		int r = 4;
		int rSquared = r * r;

		int cx = (int) cakeLocation.getX();
		int cz = (int) cakeLocation.getZ();
		World w = cakeLocation.getWorld();

		for (int x = cx - r; x <= cx + r; x++) {
			for (int z = cz - r; z <= cz + r; z++) {
				if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
					w.getBlockAt(x, (int) cakeLocation.getY(), z).setType(Material.GLASS);
					w.getBlockAt(x, (int) cakeLocation.getY() + 1, z).setType(Material.CAKE_BLOCK);
				}
			}
		}
		
		winner.teleport(cakeLocation.clone().add(0, 3.5, 0));
		winner.setItemInHand(new ItemBuilder().name("§aGanhou!").type(Material.EMPTY_MAP).build());
		winner.updateInventory();
		
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(winner);
		
		gamer.getStatus().addWin();
	}

	@EventHandler
	public void onMapInitialize(MapInitializeEvent event) {
		MapView map = event.getMap();
		
		map.getRenderers().forEach(renderer -> map.removeRenderer(renderer));

		map.addRenderer(new MapRenderer() {

			@Override
			public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
				mapCanvas.drawText(38, 6, MinecraftFont.Font, "Parabens,");
				mapCanvas.drawText(17, 15, MinecraftFont.Font, "voce venceu o HG");
				mapCanvas.drawImage(14, 40,
						new ImageIcon(GameMain.getInstance().getDataFolder().getPath() + "/saintmc.png").getImage());
			}

		});
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerLoginEvent event) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player.hasGroupPermission(Group.TRIAL)) {
			event.allow();
			return;
		}
		
		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO servidor está sendo finalizado!");
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		
		time++;

		if (time == 15) {
			new BukkitRunnable() {
				int x = 0;
				
				@Override
				public void run() {
					x++;
					
					if (x == 10) {
						Bukkit.shutdown();
						return;
					}
					
					Bukkit.getOnlinePlayers().forEach(player -> GameMain.getInstance().sendPlayerToLobby(player));

					if (Bukkit.getOnlinePlayers().size() == 0)
						Bukkit.shutdown();
				}
			}.runTaskTimer(GameMain.getInstance(), 0, 5);
			return;
		}
		
		Bukkit.broadcastMessage("§b" + winner.getName() + " §eganhou a partida!");
	}

}
