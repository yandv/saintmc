package tk.yallandev.saintmc.lobby.menu.profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.menu.account.PreferencesInventory;
import tk.yallandev.saintmc.bukkit.menu.account.SkinInventory;
import tk.yallandev.saintmc.bukkit.menu.account.SkinInventory.MenuType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

public class ProfileInventory {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public ProfileInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		MenuInventory inv = new MenuInventory("§7§nMeu perfil", 5);

		inv.setItem(13,
				new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(player.getName())
						.name(member.getGroup() == Group.MEMBRO ? "§7" + player.getName()
								: Tag.getByName(member.getGroup().toString()).getPrefix() + " " + player.getName())
						.lore("", "§7Primeiro login: §f" + DATE_FORMAT.format(new Date(member.getFirstLogin())),
								"§7Ultimo login: §f" + DATE_FORMAT.format(new Date(member.getFirstLogin())))
						.build());

		inv.setItem(29, new ItemBuilder().type(Material.PAPER).name("§aVer estatísticas")
				.lore("\n§7Veja suas estatísticas de todos os minigames do servidor\n§aClique para visualizar").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new StatusInventory(player);
					}
				});

		inv.setItem(30, new ItemBuilder().type(Material.ITEM_FRAME).name("§aSua skin")
				.lore("\n§7Altere sua skin atual\n§aClique para visualizar").build(), new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new SkinInventory(player, member, MenuType.GENERAL);
					}
				});

		inv.setItem(31,
				new ItemBuilder().type(Material.REDSTONE_COMPARATOR).name("§aPreferências")
						.lore("\n§7Altere suas prefêrencias.\n§aClique para visualizar").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new PreferencesInventory(player, ProfileInventory.this.getClass());
					}
				});

		inv.open(player);
	}

}
