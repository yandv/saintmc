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

	public CollectableInventory(Player player, Page page) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		MenuInventory inv = new MenuInventory("§7Coletáveis", 3);

		switch (page) {
		case FIRST: {
			inv.setItem(11,
					new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin("MHF_Question")
							.name("§eCabeças §7(Clique aqui)").lore("\n§7Você precisa ser " + Tag.LIGHT.getPrefix() + "§7 ou superior para usar as partículas!").build(),
					new MenuClickHandler() {
						@Override
						public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
							if (!member.hasGroupPermission(Group.LIGHT)) {
								player.sendMessage(" §c* §fVocê precisa ser " + Tag.LIGHT.getPrefix() + "§f ou superior para usar as cabeças!");
								return;
							}
							new HeadInventory(player);
						}
					});

			inv.setItem(12, new ItemBuilder().type(Material.POTION).name("§ePartículas §7(Clique aqui)")
					.lore("\n§7Você precisa ser " + Tag.BLIZZARD.getPrefix() + "§7 ou superior para usar as partículas!").build(), new MenuClickHandler() {
						@Override
						public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
							if (!member.hasGroupPermission(Group.BLIZZARD)) {
								player.sendMessage(" §c* §fVocê precisa ser " + Tag.BLIZZARD.getPrefix() + "§f ou superior para usar as partículas!");
								return;
							}
							
							new ParticleInventory(player);
						}
					});
			inv.setItem(14, new ItemBuilder().type(Material.EMERALD).name("§eCapas §7(Clique aqui)")
					.lore("\n§7Você precisa ser " + Tag.SAINT.getPrefix() + "§7 ou superior para usar as capas!").build(), new MenuClickHandler() {

						@Override
						public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
							if (!member.hasGroupPermission(Group.SAINT)) {
								player.sendMessage(" §c* §fVocê precisa ser " + Tag.SAINT.getPrefix() + "§f ou superior para usar as capas!");
								return;
							}
							
							new WingInventory(player);
						}
					});
			
			inv.setItem(15, new ItemBuilder().type(Material.JUKEBOX).name("§eKit de Música §7(Clique aqui)")
					.lore("\n§7Os kits musicais ainda estão em §3desenvolvimento§7!").build(), new MenuClickHandler() {

						@Override
						public void onClick(Player p, Inventory inventory, ClickType type, ItemStack stack, int slot) {
							
						}
					});

			inv.setItem(26, new ItemBuilder().type(Material.ARROW).name("§aPágina posterior").build(),
					new MenuClickHandler() {

						@Override
						public void onClick(Player p, Inventory inventory, ClickType type, ItemStack stack, int slot) {
							new CollectableInventory(p, Page.SECOND);
						}
					});
			break;
		}
		case SECOND: {
			inv.setItem(18, new ItemBuilder().type(Material.ARROW).name("§aPágina anterior").build(),
					new MenuClickHandler() {

						@Override
						public void onClick(Player p, Inventory inventory, ClickType type, ItemStack stack, int slot) {
							new CollectableInventory(p, Page.FIRST);
						}

					});
			break;
		}
		}

		inv.open(player);
	}

	public enum Page {

		FIRST, SECOND;

	}
}
