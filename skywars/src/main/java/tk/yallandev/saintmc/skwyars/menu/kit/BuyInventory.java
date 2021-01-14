package tk.yallandev.saintmc.skwyars.menu.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;
import tk.yallandev.saintmc.skwyars.menu.kit.SelectorInventory.InventoryType;
import tk.yallandev.saintmc.skwyars.menu.kit.SelectorInventory.OrderType;

public class BuyInventory {

	public BuyInventory(Player player, Kit kit, int page, OrderType orderType) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		MenuInventory menu = new MenuInventory("§7§nComprar kit " + kit.getName(), 4);

		menu.setItem(11, new ItemBuilder().type(Material.DIAMOND).lore("§7Preço em Moedas: §a" + kit.getPrice())
				.name("§aComprar usando Moedas").build(), (p, inv, type, stack, slot) -> {
					if (member.getMoney() > kit.getPrice()) {
						member.removeMoney(kit.getPrice());
						member.addPermission(
								CommonGeneral.getInstance().getServerType().name().toLowerCase().replace("_", "-")
										+ ".kit." + kit.getName().toLowerCase());
						p.sendMessage("Você comprou o kit " + kit.getName() + "!");
						p.closeInventory();
					} else {
						p.closeInventory();
						p.sendMessage("§cVocê não possui moedas para comprar esse kit!");
					}
				});

		menu.setItem(15, new ItemBuilder().type(Material.GOLD_INGOT).lore("§7Preço em Cash: §6" + kit.getCashPrice())
				.name("§6Comprar usando Cash").build(), (p, inv, type, stack, slot) -> {
					if (member.getCash() > kit.getCashPrice()) {
						member.removeCash(kit.getCashPrice());
						member.addPermission(
								CommonGeneral.getInstance().getServerType().name().toLowerCase().replace("_", "-")
										+ ".kit." + kit.getName().toLowerCase());
						p.sendMessage("Você comprou o kit " + kit.getName() + "!");
						p.closeInventory();
					} else {
						p.closeInventory();
						p.sendMessage("§cVocê não possui cash para comprar esse kit!");
					}
				});

		menu.setItem(30, new ItemBuilder().name("§aVoltar").type(Material.ARROW).build(),
				(p, inv, type, stack, slot) -> new SelectorInventory(player, page, InventoryType.STORE, orderType));

		menu.setItem(31, new ItemBuilder().type(Material.EMERALD).name("§aInformações")
				.lore("", "§7Suas moedas: §f" + member.getMoney(), "§7Seu cash: §6" + member.getCash()).build());

		menu.open(player);

	}

}
