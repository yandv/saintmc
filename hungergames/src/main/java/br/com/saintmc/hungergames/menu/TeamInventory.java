package br.com.saintmc.hungergames.menu;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.common.permission.Group;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yandv on 19/03/2023.
 * <p>
 * This inventory is used to select teams.
 *
 * @author yandv
 * @version 1.0
 * @since 1.0
 */

public class TeamInventory extends MenuInventory {

    private static final int ITEMS_PER_PAGE = 21;


    public TeamInventory(Player player) {
        this(player, 1, GameMain.getInstance().getTeamManager().getTeams().stream().map(TeamInventory::createItem)
                                .collect(Collectors.toList()));
    }

    public TeamInventory(Player player, int page, List<MenuItem> itemList) {
        super("§7Selecione seu time", 5, InventoryType.CHEST, true);

        if (itemList.isEmpty()) {
            setItem(22, new ItemBuilder().type(Material.BARRIER).name("§cNada disponível")
                                         .lore("", "§7Nenhum time está disponível no momento.",
                                               "§7Aguarde alguns instantes, estamos trabalhando...").build());
        } else {
            int pageStart = 0;
            int pageEnd = ITEMS_PER_PAGE;

            if (page > 1) {
                pageStart = ((page - 1) * ITEMS_PER_PAGE);
                pageEnd = (page * ITEMS_PER_PAGE);
            }

            if (pageEnd > itemList.size()) pageEnd = itemList.size();

            int w = 10;

            for (int i = pageStart; i < pageEnd; i++) {
                MenuItem item = itemList.get(i);
                setItem(item, w);

                if (w % 9 == 7) {
                    w += 3;
                    continue;
                }

                w += 1;
            }
        }

        if (page > 1) {
            setItem(39, new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).lore("").build(),
                    (p, inventory, clickType, itemStack, i) -> {new TeamInventory(player, page - 1, itemList); return false;});
        }

        if (Math.ceil(itemList.size() / ITEMS_PER_PAGE) + 1 > page) {
            setItem(41, new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).lore("").build(),
                    (p, inventory, clickType, itemStack, i) -> {new TeamInventory(player, page + 1, itemList); return false;});
        }

        open(player);
    }

    private static MenuItem createItem(Team team) {
        return new MenuItem(
                new ItemBuilder().type(Material.LEATHER_CHESTPLATE)
                                 .name(team.getColor().getChatColor() + team.getColor().getName())
                                 .color(team.getColor().toBukkitColor())
                                 .lore("", "§fTime: " + team.getColor().getChatColor() + team.getColor().getName(), "§fParticipantes:")
                                 .lore("§7" + (team.getPlayerList().isEmpty() ? "Nenhum participante." :
                                               team.getParticipantsAsGamer().stream().map(Gamer::getPlayerName)
                                                   .collect(Collectors.joining("\n")))).lore("", team.isFull() ?
                                                                                                 "§cO time está cheio." :
                                                                                                 "§aClique para entrar nesse time.")
                                 .build(), (player, inventory, clickType, itemStack, i) -> {
            if (team.getPlayerList().contains(player.getUniqueId())) {
                player.sendMessage("§cVocê já está nesse time.");
                updateItem(team, itemStack);
                return false;
            }

            if (team.isFull()) {
                updateItem(team, itemStack);
                if (CommonGeneral.getInstance().getMemberManager().hasServerGroup(player.getUniqueId(), Group.ADMIN)) {
                    player.sendMessage(
                            "§cO time está cheio, caso queira entrar use /team join " + team.getId() + " force");
                    return false;
                }

                player.sendMessage("§cO time está cheio.");
                return false;
            }

            Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());

            if (gamer.getTeam() != null)
                gamer.getTeam().removePlayer(gamer);

            player.closeInventory();
            team.addPlayer(gamer);
            return false;
        });
    }

    private static void updateItem(Team team, ItemStack itemStack) {
        itemStack.setItemMeta(createItem(team).getStack().getItemMeta());
    }

}
