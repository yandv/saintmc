package tk.yallandev.saintmc.lobby.menu.profile;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.Tag;

public class ProfileInventory {

	public ProfileInventory(Player player) {

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		ItemBuilder builder = new ItemBuilder();

		MenuInventory inv = new MenuInventory("§7Seu Perfil", 5);

		inv.setItem(13, builder.type(Material.SKULL_ITEM).durability(3).skin(player.getName())
				.name((member.getGroup() == Group.MEMBRO ? "§7" + player.getName()
						: Tag.getByName(member.getGroup().toString()).getPrefix() + " " + player.getName()) + " "
						+ "§7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)")
				.build());

		inv.setItem(30, builder.type(Material.PAPER).name("§eEstatísticas §7(Clique Aqui)").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new StatusInventory(player);
					}
				});

		inv.setItem(32, builder.type(Material.REDSTONE_COMPARATOR).name("§ePreferências §7(Clique Aqui)").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new PreferencesInventory(player);
					}
				});

		inv.open(player);
	}

}
