package br.com.saintmc.hungergames.menu.spectator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.kit.KitType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class SpectatorInventory {

    private static int itemsPerPage = 21;

    public SpectatorInventory(Player player, int page) {

        MenuInventory menu = new MenuInventory("§7Players", 6, true);

        List<MenuItem> items = new ArrayList<>();

        for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers().stream().filter(Gamer::isPlaying)
                                      .sorted((g1, g2) -> g1.getPlayerName().compareTo(g2.getPlayerName()))
                                      .collect(Collectors.toList())) {
            items.add(new MenuItem(new ItemBuilder().lore((GameMain.getPlugin().isDoubleKit() ? "\n§7Kit 1: §f" +
                                                                                                NameUtils.formatString(
                                                                                                        gamer.getKitName(
                                                                                                                KitType.PRIMARY)) +
                                                                                                "\n§7Kit 2: §f" +
                                                                                                NameUtils.formatString(
                                                                                                        gamer.getKitName(
                                                                                                                KitType.SECONDARY)) :
                                                           "\n§7Kit: §f" +
                                                           NameUtils.formatString(gamer.getKitName(KitType.PRIMARY))) +
                                                          "\n§7Kills: §e" + gamer.getMatchKills())
                                                    .type(Material.SKULL_ITEM).durability(3)
                                                    .skin(gamer.getPlayer().isOnline() ? gamer.getPlayer().getName() :
                                                          gamer.getPlayerName()).name("§e" +
                                                                                      (gamer.getPlayer().isOnline() ?
                                                                                       gamer.getPlayer().getName() :
                                                                                       gamer.getPlayerName())).build(),
                                   (p, inv, type, stack, slot) -> {
                                       if (gamer.getPlayer().isOnline()) {
                                           p.teleport(gamer.getPlayer());
                                       } else {
                                           p.sendMessage("§cJogador offline!");
                                       }
                                       return false;
                                   }));
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
                                              new SpectatorInventory(arg0, page - 1);
                                              return false;
                                          }
                                      }), 45);
        }

        if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new SpectatorInventory(p, page + 1);
                                          return false;
                                      }), 53);
        }

        menu.open(player);
    }
}
