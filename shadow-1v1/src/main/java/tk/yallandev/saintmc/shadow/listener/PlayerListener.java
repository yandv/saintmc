package tk.yallandev.saintmc.shadow.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import tk.yallandev.saintmc.shadow.GameMain;
import tk.yallandev.saintmc.shadow.gamer.Gamer;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

	public PlayerListener() {
		ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP));

		recipe.addIngredient(new MaterialData(Material.INK_SACK, (byte) 3));
		recipe.addIngredient(new MaterialData(Material.BOWL));

		Bukkit.addRecipe(recipe);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		Gamer gamer = new Gamer(player);

		GameMain.getInstance().getGamerManager().loadGamer(player.getUniqueId(), gamer);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameMain.getInstance().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player p = (Player) event.getDamager();
		ItemStack sword = p.getItemInHand();

		if (sword == null)
			return;

		if (sword.getType() == Material.DIAMOND_SWORD)
			event.setDamage(event.getDamage() + 2.5);

		if (sword.getType().name().contains("SWORD"))
			sword.setDurability((short) 0);
	}

}
