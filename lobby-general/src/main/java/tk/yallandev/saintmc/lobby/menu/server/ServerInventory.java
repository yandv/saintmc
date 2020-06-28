package tk.yallandev.saintmc.lobby.menu.server;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuUpdateHandler;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.menu.server.skywars.SkywarsInventory;

import static tk.yallandev.saintmc.common.server.ServerType.*;

public class ServerInventory {

    public static boolean LOBBY_HG = true;

    public ServerInventory(Player player) {
        MenuInventory menuInventory = new MenuInventory("§7§nSelecionar modo", 3);

        createItens(player, menuInventory);

        menuInventory.setUpdateHandler(new MenuUpdateHandler() {

            @Override
            public void onUpdate(Player player, MenuInventory menu) {
                createItens(player, menuInventory);
            }
        });

        menuInventory.open(player);
    }

    public void createItens(Player player, MenuInventory menuInventory) {
        menuInventory.setItem(10, new ItemBuilder().name("§aCompetitivo").type(Material.MUSHROOM_SOUP).lore("§7" +
                                                                                                            getTotalNumber(
                                                                                                                    ServerType.LOBBY_HG,
                                                                                                                    EVENTO,
                                                                                                                    HUNGERGAMES) +
                                                                                                            " jogando agora.")
                                                   .build(), (p, inv, type, stack, slot) -> {
            BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_HG);
            return false;
        });

        menuInventory.setItem(11, new ItemBuilder().name("§aGladiator").type(Material.IRON_FENCE)
                                                   .lore("§7" + getTotalNumber(GLADIATOR) + " jogando agora.").build(),
                              (p, inv, type, stack, slot) -> {
                                  if (type == ClickType.LEFT) {
                                      new GladiatorInventory(p);
                                      return false;
                                  }

                                  BukkitMain.getInstance().sendServer(player, ServerType.GLADIATOR);
                                  return false;
                              });

        menuInventory.setItem(12, new ItemBuilder().name("§a1v1").type(Material.BLAZE_ROD)
                                                   .lore("§7" + getTotalNumber(ONEXONE) + " jogando agora.").build(),
                              (p, inv, type, stack, slot) -> {
                                  if (type == ClickType.LEFT) {
                                      new GladiatorInventory(p);
                                      return false;
                                  }

                                  BukkitMain.getInstance().sendServer(player, ServerType.ONEXONE);
                                  return false;
        });
    }

    public int getTotalNumber(ServerType... serverTypes) {
        return BukkitMain.getInstance().getServerManager().getTotalNumber(serverTypes);
    }

    @RequiredArgsConstructor
    public static class SendClick implements MenuClickHandler {

        private final String serverId;

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            BukkitMain.getInstance().sendPlayer(p, serverId);
            return false;
        }
    }
}
