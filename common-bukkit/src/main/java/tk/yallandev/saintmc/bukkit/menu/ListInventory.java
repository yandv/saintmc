package tk.yallandev.saintmc.bukkit.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.menu.report.ReportListInventory;

@Getter
public class ListInventory<T> {

    private MenuInventory menu;

    public ListInventory(String name, List<T> list, int size, int itemsPerPage, int page, int start, ItemHandler<T> itemHandler, String emptyName) {
        menu = new MenuInventory(name, size);

        int pageStart = 0;
        int pageEnd = itemsPerPage;

        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }

        if (pageEnd > list.size()) {
            pageEnd = list.size();
        }

        if (list.isEmpty()) {
            menu.setItem(13, new ItemBuilder().name(emptyName).type(Material.BARRIER).build());
        } else {
            for (int i = pageStart; i < pageEnd; i++) {
                menu.setItem(start, itemHandler.handleItem(list.get(i), i));

                if (start % 9 == 7) {
                    start += 3;
                    continue;
                }

                start += 1;
            }
        }

        if (page != 1) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new ReportListInventory(p, page - 1);
                                          return false;
                                      }), 45);
        }

        if (Math.ceil(list.size() / itemsPerPage) + 1 > page) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new ReportListInventory(p, page + 1);
                                          return false;
                                      }), 53);
        }
    }

    /**
     * Player will open the inventory
     *
     * @param player
     */

    public void openInventory(Player player) {
        menu.open(player);
    }

    public interface ItemHandler<T> {

        MenuItem handleItem(T t, int index);
    }
}
