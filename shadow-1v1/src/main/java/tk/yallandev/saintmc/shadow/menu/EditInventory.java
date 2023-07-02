package tk.yallandev.saintmc.shadow.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.account.Member;

import java.util.UUID;

public class EditInventory extends MenuInventory {

    private int currentSlot = -1;

    private final MenuClickHandler PERMUTATION = new MenuClickHandler() {

        @Override
        public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            return translateSlot(slot) != -1;
        }
    };

    public EditInventory(Player player) {
        this(player, getContents(player.getUniqueId()));
    }

    public EditInventory(Player player, ItemStack[] contents) {
        super("§7Editar seu inventário", 6);

        for (int i = 9; i < 36; i++) {
            setItem(i - 9, contents[i] == null ? new ItemStack(Material.AIR) : contents[i], PERMUTATION);
        }

        for (int i = 27; i < 36; i++) {
            setItem(i, new ItemBuilder().name("§f").lore("§7Itens da hotbar em baixo", "§7Itens do inventário a cima.")
                                        .type(Material.STAINED_GLASS_PANE).durability(7).build());
        }

        for (int i = 0; i < 9; i++) {
            setItem(36 + i, contents[i] == null ? new ItemStack(Material.AIR) : contents[i], PERMUTATION);
        }

        setItem(48, new ItemBuilder().name("§cCancelar operação.").type(Material.BARRIER)
                                     .lore("§7Clique para cancelar sem salvar as alterações feitas.").build(),
                (p, inv, type, stack, slot) -> {
                    p.closeInventory();
                    return false;
                });

        setItem(50, new ItemBuilder().name("§aSalvar").type(Material.WOOL).durability(5)
                                     .lore("§7Clique para cancelar sem salvar as alterações feitas.").build(),
                (p, inv, type, stack, slot) -> {
                    if (p.getItemOnCursor() != null && p.getItemOnCursor().getType() != Material.AIR) {
                        player.sendMessage("§cVocê não pode salvar o inventário com um item na mão.");
                        return false;
                    }

                    ItemStack[] newContents = new ItemStack[36];

                    for (int i = 0; i < 36; i++) {
                        newContents[i] = i >= 9 ? inv.getItem(i - 9) : inv.getItem(i + 36);
                    }

                    Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

                    if (member == null) {
                        p.sendMessage("§cNão foi possível salvar o inventário, tente novamente mais tarde.");
                        return false;
                    }

                    member.setGladiatorInventory(translate(newContents));
                    p.closeInventory();
                    p.sendMessage("§aO seu inventário foi salvo.");
                    return false;
                });

        setDragHandler((player1, inventory, inventorySlots, newItems, oldCursor, type) -> {
            return inventorySlots.stream().noneMatch(slot -> slot >= 45);
        });

        open(player);
    }

    public int translateSlot(int slot) {
        if (slot >= 36 && slot < 45) {
            return slot - 36;
        }

        if (slot >= 0 && slot < 27) {
            return slot + 9;
        }

        return -1;
    }

    public static ItemStack[] createContents() {
        ItemStack[] contents = new ItemStack[36];

        contents[0] = new ItemBuilder().name("§aEspada de Diamante!").type(Material.DIAMOND_SWORD)
                                       .enchantment(Enchantment.DAMAGE_ALL).build();
        contents[1] = new ItemStack(Material.COBBLE_WALL, 64);
        contents[2] = new ItemStack(Material.LAVA_BUCKET);
        contents[3] = new ItemStack(Material.WATER_BUCKET);
        contents[8] = new ItemStack(Material.WOOD, 64);

        contents[27] = new ItemStack(Material.LAVA_BUCKET);
        contents[28] = new ItemStack(Material.LAVA_BUCKET);

        contents[17] = new ItemStack(Material.STONE_AXE);
        contents[26] = new ItemStack(Material.STONE_PICKAXE);

        contents[13] = new ItemStack(Material.BOWL, 64);
        contents[14] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[15] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[16] = new ItemStack(Material.INK_SACK, 64, (short) 3);

        contents[22] = new ItemStack(Material.BOWL, 64);
        contents[23] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[24] = new ItemStack(Material.INK_SACK, 64, (short) 3);
        contents[25] = new ItemStack(Material.INK_SACK, 64, (short) 3);

        contents[9] = new ItemStack(Material.IRON_HELMET);
        contents[10] = new ItemStack(Material.IRON_CHESTPLATE);
        contents[11] = new ItemStack(Material.IRON_LEGGINGS);
        contents[12] = new ItemStack(Material.IRON_BOOTS);

        contents[18] = new ItemStack(Material.IRON_HELMET);
        contents[19] = new ItemStack(Material.IRON_CHESTPLATE);
        contents[20] = new ItemStack(Material.IRON_LEGGINGS);
        contents[21] = new ItemStack(Material.IRON_BOOTS);

        return contents;
    }

    public static String translate(ItemStack[] contents) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ItemStack itemStack : contents) {
            if (itemStack == null) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append(itemStack.getTypeId()).append(',').append(itemStack.getAmount()).append(',')
                             .append(itemStack.getDurability());
            }

            stringBuilder.append(";");
        }


        return stringBuilder.toString();
    }

    public static ItemStack[] translate(String contents) {
        String[] split = contents.split(";");

        ItemStack[] itemStacks = new ItemStack[split.length];

        for (int i = 0; i < split.length; i++) {
            String[] itemSplit = split[i].split(",");

            int id = Integer.parseInt(itemSplit[0]);
            int amount = itemSplit.length >= 2 ? Integer.parseInt(itemSplit[1]) : 1;
            short durability = itemSplit.length >= 2 ? Short.parseShort(itemSplit[2]) : 1;


            itemStacks[i] = new ItemStack(id, amount, durability);
        }

        return itemStacks;
    }

    public static ItemStack[] getContents(UUID playerId) {
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(playerId);

        if (member == null) {
            return createContents();
        }

        if (member.getGladiatorInventory() == null) {
            ItemStack[] contents = createContents();
            member.setGladiatorInventory(translate(contents));
            return contents;
        }

        return translate(member.getGladiatorInventory());
    }
}
