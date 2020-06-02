package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.game.GameState;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;

public class TimelordAbility extends Ability {

	private static final int RADIUS = 6;
	
	private List<Location> borderList = new ArrayList<>();;

	public TimelordAbility() {
		super("Timelord",
				Arrays.asList(new ItemBuilder().name(ChatColor.GOLD + "Timelord").type(Material.WATCH).build()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!e.getAction().name().contains("RIGHT"))
			return;

		if (!hasAbility(e.getPlayer()))
			return;

		if (!GameState.isInvincibility(GameGeneral.getInstance().getGameState()))
			return;

		Player player = e.getPlayer();

		if (!isAbilityItem(player.getItemInHand()))
			return;

		if (isCooldown(player)) {
			return;
		}

		for (Player game : Bukkit.getOnlinePlayers()) {
			if (game == player)
				continue;

			if (AdminMode.getInstance().isAdmin(game))
				continue;

			double distance = player.getLocation().distance(game.getPlayer().getLocation());

			if (distance <= RADIUS) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 255), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 250), true);
			}
		}

		Location mainBlock = player.getLocation();
		List<Location> locationList = new ArrayList<>();

		for (int x = -RADIUS; x <= RADIUS; x++) {
			for (int z = -RADIUS; z <= RADIUS; z++) {
				if (x == RADIUS || z == RADIUS || x == -RADIUS || z == -RADIUS) {
					locationList.add(mainBlock.clone().add(x, 1, z));
					locationList.add(mainBlock.clone().add(x, 2, z));
					locationList.add(mainBlock.clone().add(x, 3, z));
					locationList.add(mainBlock.clone().add(x, 4, z));
				}
			}
		}
		
		borderList.addAll(locationList);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				borderList.removeAll(locationList);
			}
		}.runTaskLater(GameMain.getInstance(), 20*6);

		e.setCancelled(true);
		addCooldown(player, 25);
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getCurrentTick() % 3 != 0)
			return;
		
		Iterator<Location> entry = borderList.iterator();

		while (entry.hasNext()) {
			Location location = entry.next();
			
			location.getWorld().spigot().playEffect(location, Effect.COLOURED_DUST);
		}
	}

}
