package tk.yallandev.saintmc.lobby.menu.profile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class PreferencesInventory {

	public PreferencesInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);

		ItemBuilder builder = new ItemBuilder();

		MenuInventory inv = new MenuInventory("§7Suas Preferências", 5);

		inv.setItem(13, builder.type(Material.SKULL_ITEM).durability(3).skin(player.getName())
				.name((member.getGroup() == Group.MEMBRO ? "§7" + player.getName()
						: Tag.getByName(member.getGroup().toString()).getPrefix() + " " + player.getName()) + " "
						+ "§7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)")
				.build());

		inv.setItem(30,
				(gamer.isSeeing()
						? builder.type(Material.INK_SACK).durability(8).name("§eEsconder jogadores")
								.lore("\n§7Clique para §aativar§7 os jogadores").build()
						: builder.type(Material.INK_SACK).durability(10).name("§eEsconder jogadores")
								.lore("\n§7Clique para §cdesativar§7 os jogadores").build()),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						gamer.setSeeing(!gamer.isSeeing());

						if (gamer.isSeeing()) {
							VanishAPI.getInstance().updateVanishToPlayer(player);
						} else {
							for (Player players : Bukkit.getOnlinePlayers()) {
								if (Member.hasGroupPermission(players.getUniqueId(), Group.LIGHT))
									continue;

								player.hidePlayer(players);
							}
						}

						player.sendMessage(" §a* §fAgora você §a" + (gamer.isSeeing() == true ? "está" : "não está")
								+ " §fvendo todos!");
						new PreferencesInventory(player);
					}
				});

		inv.setItem(31,
				(member.getAccountConfiguration().isTellEnabled()
						? builder.type(Material.INK_SACK).durability(10).name("§eMensagens privadas")
								.lore("\n§7Clique para §cdesativar§f o tell").build()
						: builder.type(Material.INK_SACK).durability(8).name("§eMensagens privadas")
								.lore("\n§7Clique para §aativar§f o tell").build()),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						member.getAccountConfiguration()
								.setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
						new PreferencesInventory(player);
					}
				});

		inv.setItem(32,
				(player.getAllowFlight()
						? builder.type(Material.INK_SACK).durability(10).name("§eFly")
								.lore("\n§7Clique para §cdesativar§7 o fly\n\n§7Você precisa ser "
										+ Tag.LIGHT.getPrefix() + "§7 ou superior para usar o fly!")
								.build()
						: builder.type(Material.INK_SACK).durability(8).name("§eFly")
								.lore("\n§7Clique para §aativar§7 o fly\n\n§7Você precisa ser " + Tag.LIGHT.getPrefix()
										+ "§7 ou superior para usar o fly!")
								.build()),
				new MenuClickHandler() {

					@Override
					public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (!member.hasGroupPermission(Group.DONATOR)) {
							player.sendMessage(" §c* §fVocê precisa ser " + Tag.DONATOR.getPrefix() + "§f, "
									+ Tag.LIGHT.getPrefix() + "§f ou superior para usar o fly!");
							return;
						}

						player.setAllowFlight(!player.getAllowFlight());

						new PreferencesInventory(player);
					}
				});

		inv.open(player);
	}
}
