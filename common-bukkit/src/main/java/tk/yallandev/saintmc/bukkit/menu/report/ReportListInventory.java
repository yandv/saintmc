package tk.yallandev.saintmc.bukkit.menu.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.ClickEvent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class ReportListInventory {

    private static int itemsPerPage = 21;

    public ReportListInventory(Player player, int page) {
        List<Report> reports = new ArrayList<>(CommonGeneral.getInstance().getReportManager().getReports());
        Iterator<Report> iterator = reports.iterator();

        while (iterator.hasNext()) {
            Report report = iterator.next();

            if (report.isExpired()) {
                report.expire();
                iterator.remove();
                continue;
            }
        }

        reports.sort((o1, o2) -> {
            int integer = Boolean.valueOf(o1.isOnline()).compareTo(o2.isOnline());

            if (integer != 0) {
                return integer;
            }

            integer = Integer.valueOf(o1.getReportLevel()).compareTo(o2.getReportLevel());

            if (integer != 0) {
                return integer;
            }

            integer = Integer.valueOf(o1.getPlayersReason().size()).compareTo(o2.getPlayersReason().size());

            if (integer != 0) {
                return integer;
            }

            if (o1.getLastReportTime() > o2.getLastReportTime()) {
                return 1;
            } else if (o1.getLastReportTime() == o2.getLastReportTime()) {
                return 0;
            }
            return -1;
        });

        MenuInventory menu = new MenuInventory("§7Lista de reports", 6);

        int pageStart = 0;
        int pageEnd = itemsPerPage;

        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }

        if (pageEnd > reports.size()) {
            pageEnd = reports.size();
        }

        int w = 10;

        if (reports.isEmpty()) {
            menu.setItem(13, new ItemBuilder().name("§cNenhum report").type(Material.BARRIER)
                                              .lore("§4A lista de report está vazia").build());
        } else {
            for (int i = pageStart; i < pageEnd; i++) {
                Report report = reports.get(i);

                if (report.isExpired()) {
                    report.expire();
                } else {
                    menu.setItem(w, new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
                                                     .name((report.isOnline() ? "§a" : "§c") + report.getPlayerName())
                                                     .lore(report.isOnline() ?
                                                           "§aO jogador está online no momento!\n§aClique para teletransportar" :
                                                           "§cO jogador está offline no momento.")
                                                     .skin(report.getPlayerName()).build(),
                                 new ReportClickHandler(report, menu));
                }

                if (w % 9 == 7) {
                    w += 3;
                    continue;
                }

                w += 1;
            }
        }

        if (page != 1) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new ReportListInventory(p, page - 1);
                                          return false;
                                      }), 45);
        }

        if (Math.ceil(reports.size() / itemsPerPage) + 1 > page) {
            menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
                                      (p, inventory, clickType, item, slot) -> {
                                          new ReportListInventory(p, page + 1);
                                          return false;
                                      }), 53);
        }

        menu.open(player);
    }

    private static class ReportClickHandler implements MenuClickHandler {

        private Report report;

        public ReportClickHandler(Report report, MenuInventory topInventory) {
            this.report = report;
        }

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            if (type == ClickType.RIGHT) {
                if (!report.isOnline()) {
                    p.sendMessage(" §cO jogador §c" + report.getPlayerName() + "§c não está online.");
                    return false;
                }

                p.spigot().sendMessage(new MessageBuilder("§aClique aqui para teletransportar!").setClickEvent(
                        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp " + report.getPlayerName())).create());
                return false;
            }

            new ReportInventory(p, report);
            return false;
        }
    }
}
