package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.levansj01.verus.api.events.ViolationEvent;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class TimelordKit extends Kit {

	private static final int RADIUS = 5;
	private Map<Player, Long> timelordList = new HashMap<>();

	public TimelordKit() {
		super("Timelord", "Pare o tempo com seu relógio", Material.WATCH, 11700,
				Arrays.asList(new ItemBuilder().name("§aTimelord").type(Material.WATCH).build()));
	}

	@EventHandler
	public void onViolantion(ViolationEvent event) {
		if (timelordList.containsKey(event.getPlayer()))
			if (event.getCheck().getDisplay().equals("Fly"))
				if (event.getCheck().getSubType().equals("B"))
					event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (hasAbility(event.getPlayer()) && isAbilityItem(event.getPlayer().getItemInHand())) {
			if (isCooldown(event.getPlayer()))
				return;

			for (Player game : Bukkit.getOnlinePlayers()) {
				if (game == event.getPlayer())
					continue;

				if (AdminMode.getInstance().isAdmin(game))
					continue;

				double distance = event.getPlayer().getLocation().distance(game.getPlayer().getLocation());

				if (distance <= RADIUS) {
					timelordList.put(game, System.currentTimeMillis() + 4000l);
				}
			}

			event.setCancelled(true);
			addCooldown(event.getPlayer(), 45);
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1.0f, 1.0f);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (timelordList.containsKey(event.getPlayer()))
			if (timelordList.get(event.getPlayer()) > System.currentTimeMillis())
				event.setCancelled(true);
			else
				timelordList.remove(event.getPlayer());
	}

}
