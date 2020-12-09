package tk.yallandev.saintmc.lobby.menu.collectable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

public class CollectableInventory {

	public CollectableInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		MenuInventory inv = new MenuInventory("§7Coletáveis", 3);

		inv.setItem(11,
				new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin("MHF_Question").name("§aCabeças")
						.lore("§7Você precisa ser " + Tag.PRO.getPrefix()
								+ "§7 ou superior para usar as cabeças customizadas!\n\n§eClique para abrir!")
						.build(),
				new MenuClickHandler() {
					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (!member.hasGroupPermission(Group.PRO)) {
							player.sendMessage(
									"§cVocê precisa ser " + Tag.PRO.getPrefix() + " ou superior para usar as cabeças!");
							return;
						}
						new HeadInventory(player);
					}
				});

		inv.setItem(13,
				new ItemBuilder().type(Material.POTION).name("§aPartículas")
						.lore("§7Você precisa ser " + Tag.EXTREME.getPrefix()
								+ "§7 ou superior para usar as partículas!\n\n§eClique para abrir!")
						.build(),
				new MenuClickHandler() {
					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (!member.hasGroupPermission(Group.EXTREME)) {
							player.sendMessage("§cVocê precisa ser " + Tag.EXTREME.getPrefix()
									+ " ou superior para usar as partículas!");
							return;
						}

						new ParticleInventory(player);
					}
				});
		inv.setItem(15,
				new ItemBuilder().type(Material.EMERALD).name("§aCapas")
						.lore("§7Você precisa ser " + Tag.ULTIMATE.getPrefix()
								+ "§7 ou superior para usar as capas!\n\n§eClique para abrir!")
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (!member.hasGroupPermission(Group.ULTIMATE)) {
							player.sendMessage("§cVocê precisa ser " + Tag.ULTIMATE.getPrefix()
									+ " ou superior para usar as capas!");
							return;
						}

						new WingInventory(player);
					}
				});

		inv.open(player);
	}

}
