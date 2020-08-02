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
import tk.yallandev.saintmc.kitpvp.GameMain;

public class SoupListener implements Listener {

	@EventHandler
	public void onHanging(HangingBreakEvent event) {
		if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("sopa")) {
			event.setLine(0, "§6");
			event.setLine(1, "§6§lSOPA");
			event.setLine(2, "§6");
			event.setLine(3, "§6");
		} else if (event.getLine(0).equalsIgnoreCase("recraft")) {
			event.setLine(0, "§6");
			event.setLine(1, "§b§lRECRAFT");
			event.setLine(2, "§6");
			event.setLine(3, "§6");
		} else if (event.getLine(0).equalsIgnoreCase("spawn")) {
			event.setLine(0, "§6");
			event.setLine(1, "§5§lSPAWN");
			event.setLine(2, "§6");
			event.setLine(3, "§6");
		}

		if (event.getLine(0).contains("&")) {
			event.setLine(0, event.getLine(0).replace("&", "§"));
		}
		if (event.getLine(1).contains("&")) {
			event.setLine(1, event.getLine(1).replace("&", "§"));
		}
		if (event.getLine(2).contains("&")) {
			event.setLine(2, event.getLine(2).replace("&", "§"));
		}
		if (event.getLine(3).contains("&")) {
			event.setLine(3, event.getLine(3).replace("&", "§"));
		}
	}

	@EventHandler
	public void onPlayerInteractSoup(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.getClickedBlock() == null)
			return;

		if (event.getClickedBlock().getType() != Material.WALL_SIGN && event.getClickedBlock().getType() != Material.SIGN_POST)
			return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		String[] lines = sign.getLines();

		if (lines.length < 4)
			return;

		if (lines[1].toLowerCase().contains("sopa"))
			openInventory(player, true);
		else if (lines[1].toLowerCase().contains("recraft"))
			openInventory(player, false);
		if (lines[1].toLowerCase().contains("spawn")) {
			player.teleport(GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp()
					.getSpawnLocation());
			player.sendMessage("§aVocê foi teletransportado até o spawn!");
		}
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
