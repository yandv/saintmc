package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class SoupListener implements Listener {

	@EventHandler
	public void onHanging(HangingBreakEvent event) {
		if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("sopa")) {
			e.setLine(0, "§6");
			e.setLine(1, "§6§lSOPA");
			e.setLine(2, "§6");
			e.setLine(3, "§6");
		} else if (e.getLine(0).equalsIgnoreCase("recraft")) {
			e.setLine(0, "§6");
			e.setLine(1, "§b§lRECRAFT");
			e.setLine(2, "§6");
			e.setLine(3, "§6");
		}

		if (e.getLine(0).contains("&")) {
			e.setLine(0, e.getLine(0).replace("&", "§"));
		}
		if (e.getLine(1).contains("&")) {
			e.setLine(1, e.getLine(1).replace("&", "§"));
		}
		if (e.getLine(2).contains("&")) {
			e.setLine(2, e.getLine(2).replace("&", "§"));
		}
		if (e.getLine(3).contains("&")) {
			e.setLine(3, e.getLine(3).replace("&", "§"));
		}
	}

	@EventHandler
	public void onPlayerInteractSoup(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if (e.getClickedBlock() == null)
			return;

		if (e.getClickedBlock().getType() != Material.WALL_SIGN && e.getClickedBlock().getType() != Material.SIGN_POST)
			return;

		Sign s = (Sign) e.getClickedBlock().getState();
		String[] lines = s.getLines();
		
		if (lines.length < 4)
			return;

		if (lines[1].toLowerCase().contains("sopa"))
			openInventory(p, true);
		else if (lines[1].toLowerCase().contains("recraft"))
			openInventory(p, false);
	}
	
	public void openInventory(Player player, boolean soup) {
		
		Inventory inv = Bukkit.getServer().createInventory(player, soup ? 54 : 9, soup ? "§8Sopas" : "§8Recraft");
		
		if (soup) {
			ItemStack sopas = new ItemStack(Material.MUSHROOM_SOUP);

			for (int i = 0; i < 54; i++)
				inv.setItem(i, sopas);
		} else {
			ItemStack red = new ItemBuilder().amount(64).type(Material.RED_MUSHROOM).build();
			ItemStack brown = new ItemBuilder().amount(64).type(Material.BROWN_MUSHROOM).build();
			ItemStack bowl = new ItemBuilder().amount(64).type(Material.BOWL).build();

			inv.setItem(0, bowl);
			inv.setItem(1, red);
			inv.setItem(2, brown);
			inv.setItem(3, bowl);
			inv.setItem(4, red);
			inv.setItem(5, brown);
			inv.setItem(6, bowl);
			inv.setItem(7, red);
			inv.setItem(8, brown);
		}
		
		player.openInventory(inv);
	}
	
}
