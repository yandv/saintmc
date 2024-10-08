package tk.yallandev.anticheat.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.anticheat.test.Test.CheckLevel;
import tk.yallandev.anticheat.test.TestType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;

public class ResultInventory {

    public ResultInventory(Player player, Player tester, TestType checkType, CheckLevel checkLevel) {
        MenuInventory menu = new MenuInventory("§7Check - " + tester.getName(), 5);

        ItemBuilder item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§a0% de chance");

        if (checkLevel == CheckLevel.NO_CHANCE) {
            item.glow();
        }

        menu.setItem(10, item.build());

        item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(4).name("§e40% de chance");

        if (checkLevel == CheckLevel.MAYBE) {
            item.glow();
        }

        menu.setItem(12, item.build());

        item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(1).name("§c70% de chance");

        if (checkLevel == CheckLevel.PROBABLY) {
            item.glow();
        }

        menu.setItem(14, item.build());

        item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§c100% de chance");

        if (checkLevel == CheckLevel.DEFINITELY) {
            item.glow();
        }

        menu.setItem(16, item.build());

        menu.setItem(30, new ItemBuilder().name("§aVoltar").type(Material.ARROW).build(), new MenuClickHandler() {

            @Override
            public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                new AnticheatInventory(player, tester);
                return false;
            }
        });

        menu.open(player);
    }
}
