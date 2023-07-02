package br.com.saintmc.hungergames.menu.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.utils.ServerConfig;
import lombok.AllArgsConstructor;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@AllArgsConstructor
public class SelectorInventory {

    private static int itemsPerPage = 21;

    public SelectorInventory(Player player, int page, KitType kitType, OrderType orderType) {
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
        MenuInventory menu = new MenuInventory("§7Kit Selector", 6, true);
        List<Kit> kits = new ArrayList<>(GameGeneral.getInstance().getKitController().getAllKits());

        Comparator<Kit> comparator = orderType.getComparator(gamer, kitType);

        Collections.sort(kits, comparator);

        List<MenuItem> items = new ArrayList<>();

        for (Kit kit : kits) {
            if (ServerConfig.getInstance().isDisabled(kit, kitType)) {
                continue;
            }

            boolean hasKit = GameMain.getInstance().isDoubleKit() ? kitType == KitType.PRIMARY ? true : gamer.hasKit(kit.getName()) :
                             gamer.hasKit(kit.getName());

            if (hasKit) {
                items.add(new MenuItem(
                        new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
                                         .type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
                                         .name("§a" + NameUtils.formatString(kit.getName())).build(),
                        new OpenKitMenu(kit, kitType)));
            } else {
                ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
                                                  .name("§c" + NameUtils.formatString(kit.getName()))
                                                  .lore("\n§cVocê não possui este kit!\n§cCompre em: §e" +
                                                        CommonConst.STORE + "\n\n§7" + kit.getDescription() +
                                                        "\n\n§eClique para selecionar!").build();
                items.add(new MenuItem(item, new StoreKitMenu(kit)));
            }
        }

        int pageStart = 0;
        int pageEnd = itemsPerPage;

        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }

        if (pageEnd > items.size()) {
            pageEnd = items.size();
        }

        int w = 10;

        for (int i = pageStart; i < pageEnd; i++) {
            MenuItem item = items.get(i);
            menu.setItem(item, w);

            if (w % 9 == 7) {
                w += 3;
                continue;
            }

            w += 1;
        }

        if (page != 1) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                                      new MenuClickHandler() {

                                          @Override
                                          public boolean onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
                                              new SelectorInventory(arg0, page - 1, kitType, orderType);
                                              return false;
                                          }
                                      }), 45);
        }

        if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new SelectorInventory(p, page + 1, kitType, orderType);
                                          return false;
                                      }), 53);
        }

        if (gamer.hasKit(kitType)) {
            Kit kit = gamer.getKit(kitType);

            menu.setItem(48, new ItemBuilder().name("§a" + NameUtils.formatString(kit.getName()))
                                              .type(kit.getKitIcon().getType()).lore("\n§7" + kit.getDescription())
                                              .durability(kit.getKitIcon().getDurability()).build(),
                         new MenuClickHandler() {

                             @Override
                             public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                                 new InfoInventory(player, kit, kitType);
                                 return false;
                             }
                         });
        } else {
            menu.setItem(48, new ItemBuilder().name("§eNenhum").type(Material.ITEM_FRAME).build());
        }

        menu.setItem(50, new ItemBuilder().name("§fOrdenar por: §7" + (orderType == OrderType.MINE ? "Meus kits" :
                                                                       orderType == OrderType.ALPHABET ? "Alfabeto" :
                                                                       "Alfabeto ao contrário"))
                                          .type(Material.ITEM_FRAME).build(), new MenuClickHandler() {

            @Override
            public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                new SelectorInventory(player, page, kitType,
                                      orderType.ordinal() == OrderType.values().length - 1 ? OrderType.values()[0] :
                                      OrderType.values()[orderType.ordinal() + 1]);
                return false;
            }
        });

        menu.open(player);
    }

    @AllArgsConstructor
    public static class OpenKitMenu implements MenuClickHandler {

        private Kit kit;
        private KitType kitType;

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            if (type == ClickType.RIGHT) {
                new InfoInventory(p, kit, kitType);
                return false;
            }

            GameGeneral.getInstance().getKitController().selectKit(p, kit, kitType);
            p.closeInventory();
            return false;
        }
    }

    @AllArgsConstructor
    public static class StoreKitMenu implements MenuClickHandler {

        private Kit kit;

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            p.sendMessage(
                    "§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f em §a" + CommonConst.STORE +
                    "§f!");
            return false;
        }
    }

    public enum OrderType {

        MINE,
        ALPHABET,
        DE_ALPHABET;

        Comparator<Kit> getComparator(Gamer gamer, KitType kitType) {
            switch (this) {
            case MINE: {
                return new Comparator<Kit>() {

                    @Override
                    public int compare(Kit o1, Kit o2) {
                        boolean hasKitO1 =
                                GameMain.getInstance().isDoubleKit() ? kitType == KitType.PRIMARY ? true : gamer.hasKit(o1.getName()) :
                                gamer.hasKit(o1.getName());
                        boolean hasKitO2 =
                                GameMain.getInstance().isDoubleKit() ? kitType == KitType.PRIMARY ? true : gamer.hasKit(o2.getName()) :
                                gamer.hasKit(o2.getName());

                        int value1 = Boolean.valueOf(hasKitO2).compareTo(hasKitO1);

                        if (value1 == 0) {
                            return o1.getName().compareTo(o2.getName());
                        }

                        return value1;
                    }
                };
            }
            case DE_ALPHABET: {
                return (kit1, kit2) -> kit2.getName().compareTo(kit1.getName());
            }
            default: {
                return Comparator.comparing(kit -> kit.getName());
            }
            }
        }
    }
}
