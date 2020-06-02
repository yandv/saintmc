package tk.yallandev.saintmc.lobby.menu.collectable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.collectable.Collectables.Head;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class HeadInventory {
	
	public HeadInventory(Player player) {
		ItemBuilder b = new ItemBuilder();
		
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);

		MenuInventory inv = new MenuInventory("§7Coletáveis - Cabeças", 6);

		inv.setItem(4, b.name("§aRemover cabeça!").type(Material.SKULL_ITEM).durability(3)
				.skin(player.getName()).build(), new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						gamer.changeHead(new ItemStack(Material.AIR));
						player.sendMessage(" §a* §fSua §acabeça§f voltou ao normal!");
						player.closeInventory();
					}
				});
		
		int i = 19;
		
		for (Head head : Head.values()) {
			inv.setItem(i,
					b.name("§bCabeça de " + head.getHeadName()).type(Material.SKULL_ITEM).durability(3).skin(head.getPlayerName()).build(),
					new MenuClickHandler() {
						@Override
						public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
							gamer.changeHead(stack);
							player.sendMessage(" §a* §fA cabeça de §a" + head.getHeadName() + " §ffoi adicionado(a) a sua cabeça!");
							player.closeInventory();
						}
					});
			
			if (i % 9 == 7) {
				i += 3;
				continue;
			}
			
			i++;
		}

		inv.open(player);
	}

}
