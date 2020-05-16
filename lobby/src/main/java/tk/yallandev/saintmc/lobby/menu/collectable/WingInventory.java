package tk.yallandev.saintmc.lobby.menu.collectable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.collectable.Collectables.CollectableType;
import tk.yallandev.saintmc.lobby.collectable.Collectables.Wing;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class WingInventory {

	public WingInventory(Player player) {

		ItemBuilder b = new ItemBuilder();
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);
		MenuInventory inv = new MenuInventory("§7Coletáveis - Capa", 5);

		inv.setItem(4, b.type(Material.ANVIL).name("§aRemover capa!").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						gamer.setUsingWing(false);
						player.sendMessage(" §a* §fVocê removeu todas as suas §aCapa§f ativas!");
						player.closeInventory();
						
					}
				});
		
		int i = 19;
		
		for (Wing wing : Wing.values()) {
			inv.setItem(i,
					b.type(wing.getMaterial()).name("§bCapa de " + wing.getWingName()).build(),
					new MenuClickHandler() {

						@Override
						public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
							player.closeInventory();
							
							if (gamer.isUsingWing()) {
								player.sendMessage(" §c* §fVocê já está com alguma §acapa §fativada!");
								return;
							}
							
							player.sendMessage(" §a* §fCapa §a" + wing.getWingName() + " §fadicionada a você!");
							gamer.display(CollectableType.WING, wing.getParticleType());
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
