package tk.yallandev.saintmc.gladiator.menu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.json.JsonBuilder;

import java.util.HashMap;
import java.util.Map;
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
                    if (p.getItemOnCursor() != null && p.getItemOnCursor().getType() != Material.AIR) {
                        player.setItemOnCursor(null);
                    }

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

    public static JsonObject translate(ItemStack[] contents) {
        JsonObject jsonObject = new JsonObject();

        for (int i = 0; i < contents.length; i++) {
            jsonObject.add(Integer.toString(i), toJson(contents[i]));
        }


        return jsonObject;
    }

    public static JsonObject toJson(ItemStack itemStack) {
        if (itemStack == null) {
            return new JsonBuilder().addProperty("type", "AIR").addProperty("amount", 1)
                    .addProperty("durability", 0)
                    .add("enchantments", new JsonObject())
                    .build();
        }

        JsonObject enchantments = new JsonObject();

        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchantments.addProperty(Integer.toString(entry.getKey().getId()), entry.getValue());
        }

        return new JsonBuilder()
                .addProperty("type", itemStack.getType().name())
                .addProperty("amount", itemStack.getAmount())
                .addProperty("durability", itemStack.getDurability())
                .add("enchantments", enchantments)
                .build();
    }

    public static ItemStack fromJson(JsonObject jsonObject) {
        Material material = jsonObject.has("type") ? Material.valueOf(jsonObject.get("type").getAsString()) : Material.AIR;
        int amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsInt() : 0;
        short durability = jsonObject.has("durability") ? jsonObject.get("durability").getAsShort() : 0;
        JsonObject enchantments = jsonObject.getAsJsonObject("enchantments");
        Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : enchantments.entrySet()) {
            enchantmentMap.put(Enchantment.getById(Integer.parseInt(entry.getKey())), entry.getValue().getAsInt());
        }
        

        return new ItemBuilder().type(material).amount(amount).durability(durability).enchantment(enchantmentMap).build();
    }

    public static ItemStack[] translate(JsonObject jsonObject) {
        ItemStack[] itemStacks = new ItemStack[jsonObject.size()];

        for (int i = 0; i < jsonObject.size(); i++) {
            itemStacks[i] = fromJson(jsonObject.getAsJsonObject(Integer.toString(i)));
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
