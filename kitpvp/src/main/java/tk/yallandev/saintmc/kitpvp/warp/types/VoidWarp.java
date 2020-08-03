package tk.yallandev.saintmc.kitpvp.warp.types;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.types.VoidScoreboard;

public class VoidWarp extends Warp {

	private Map<UUID, Long> playerMap;

	public VoidWarp() {
		super("Void", BukkitMain.getInstance().getLocationFromConfig("void"), new VoidScoreboard());
		getScoreboard().setWarp(this);
		playerMap = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (inWarp(player)) {
				if (event.getCause() == DamageCause.VOID) {
					event.setCancelled(false);

					if (!playerMap.containsKey(player.getUniqueId()))
						playerMap.put(player.getUniqueId(), System.currentTimeMillis());
					
					getScoreboard().updateScore(player, playerMap.get(player.getUniqueId()));
				} else
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		if (event.getWarp() == this)
			handleInventory(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWarpJoin(PlayerWarpQuitEvent event) {
		if (event.getWarp() == this)
			playerMap.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
		if (event.getWarp() == this) {
			handleInventory(event.getPlayer());

			if (playerMap.containsKey(event.getPlayer().getUniqueId())) {
				event.getPlayer().sendMessage("§aVocê ficou " + StringUtils.formatTime(
						(int) ((System.currentTimeMillis() - playerMap.get(event.getPlayer().getUniqueId())) / 1000))
						+ " no void!");
				playerMap.remove(event.getPlayer().getUniqueId());
				getScoreboard().updateScore(event.getPlayer(), -1l);
			}
		}
	}

	private void handleInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		player.setLevel(0);
		player.setFoodLevel(20);
		player.setHealth(20D);

		for (int x = 0; x < player.getInventory().getSize(); x++)
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		player.getInventory().setItem(13, new ItemStack(Material.RED_MUSHROOM, 64));
		player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 64));
		player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));
		player.updateInventory();
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder().name("§aVoid Challenge")
				.lore("\n§7Treine seu refil e recraft complentando\nnos desafios de lava propostos.\n\n§a"
						+ GameMain.getInstance().getGamerManager().filter(gamer -> gamer.getWarp() == this).size()
						+ " jogadores")
				.type(Material.BEDROCK).build();
	}

}
