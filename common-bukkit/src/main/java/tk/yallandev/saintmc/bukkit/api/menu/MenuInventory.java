package tk.yallandev.saintmc.bukkit.api.menu;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.event.player.PlayerOpenInventoryEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Getter
public class MenuInventory {

    private int rows;
    private InventoryType inventoryType = InventoryType.CHEST;

    private String title;
    private Inventory inventory;
    private final boolean onePerPlayer;

    @Setter
    private boolean reopenInventory = false;

    private final Map<Integer, MenuItem> slotItem;

    @Setter
    private MenuUpdateHandler updateHandler;

    @Setter
    private DragHandler dragHandler;

    public MenuInventory(String title, int rows) {
        this(title, rows, InventoryType.CHEST, false);
    }

    public MenuInventory(String title, int rows, boolean onePerPlayer) {
        this(title, rows, InventoryType.CHEST, onePerPlayer);
    }

    public MenuInventory(String title, InventoryType inventoryType) {
        this(title, 3, inventoryType, false);
    }

    public MenuInventory(String title, InventoryType inventoryType, boolean onePerPlayer) {
        this(title, 3, inventoryType, onePerPlayer);
    }

    public MenuInventory(String title, int rows, InventoryType inventoryType, boolean onePerPlayer) {
        this.rows = rows;
        this.inventoryType = inventoryType;
        this.slotItem = new HashMap<>();
        this.title = title;
        this.onePerPlayer = onePerPlayer;

        if (!this.onePerPlayer) {
            this.inventory = inventoryType == InventoryType.CHEST ?
                             Bukkit.createInventory(new MenuHolder(this), rows * 9, title) :
                             Bukkit.createInventory(new MenuHolder(this), inventoryType, title);
        }
    }

    public void addItem(MenuItem item) {
        setItem(firstEmpty(), item);
    }

    public void addItem(ItemStack item) {
        setItem(firstEmpty(), item);
    }

    public void addItem(ItemStack item, MenuClickHandler handler) {
        setItem(firstEmpty(), item, handler);
    }

    public void setItem(ItemStack item, int slot) {
        setItem(slot, new MenuItem(item));
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, new MenuItem(item));
    }

    public void setItem(Player viewer, int slot, ItemStack item) {
        setItem(viewer, slot, new MenuItem(item));
    }

    public void setItem(Player viewer, int slot, ItemStack item, MenuClickHandler handler) {
        setItem(viewer, slot, new MenuItem(item, handler));
    }


    public void setItem(Player viewer, int slot, MenuItem item) {
        if (onePerPlayer) {
            Inventory topInventory = viewer.getOpenInventory().getTopInventory();

            if (topInventory.getType() == inventoryType && topInventory.getSize() == rows * 9) {
                topInventory.setItem(slot, item.getStack());
            }
        }

        setItem(slot, item);
    }

    public void removeItem(Player viewer, int slot) {
        if (onePerPlayer) {
            Inventory topInventory = viewer.getOpenInventory().getTopInventory();

            if (topInventory.getType() == inventoryType && topInventory.getSize() == rows * 9 &&
                topInventory.getTitle().equals(title)) {
                topInventory.setItem(slot, new ItemStack(Material.AIR));
            }
        }

        removeItem(slot);
    }

    public void setItem(int slot, ItemStack item, MenuClickHandler handler) {
        setItem(slot, new MenuItem(item, handler));
    }

    public void setItem(MenuItem item, int slot) {
        setItem(slot, item);
    }

    public void removeItem(int slot) {
        this.slotItem.remove(slot);

        if (!onePerPlayer) {
            inventory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    public void setItem(int slot, MenuItem item) {
        this.slotItem.put(slot, item);

        if (!onePerPlayer) {
            inventory.setItem(slot, item.getStack());
        }
    }

    public void setRows(int rows) {
        this.rows = rows;

        if (onePerPlayer) {
            List<Entry<Integer, MenuItem>> copyOf = ImmutableList.copyOf(slotItem.entrySet());

            this.inventory = Bukkit.createInventory(new MenuHolder(this), rows * 9, "");
            slotItem.clear();
            for (Entry<Integer, MenuItem> item : copyOf)
                setItem(item.getKey(), item.getValue());
        }
    }

    public int firstEmpty() {
        if (!onePerPlayer) {
            return inventory.firstEmpty();
        } else {
            for (int i = 0; i < rows * 9; i++) {
                if (!slotItem.containsKey(i)) {
                    return i;
                }
            }
            return -1;
        }
    }

    public boolean hasItem(int slot) {
        return this.slotItem.containsKey(slot);
    }

    public MenuItem getItem(int slot) {
        return this.slotItem.get(slot);
    }

    public void clear() {
        slotItem.clear();
        if (!onePerPlayer) {
            inventory.clear();
        }
    }

    public void open(Player p) {
        if (onePerPlayer) {
            if (p.getOpenInventory() == null//
                || p.getOpenInventory().getTopInventory().getType() != inventoryType//
                || p.getOpenInventory().getTopInventory().getSize() != rows * 9 ||
                p.getOpenInventory().getTopInventory().getHolder() == null//
                || !(p.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder)//
                || !(((MenuHolder) p.getOpenInventory().getTopInventory().getHolder()).isOnePerPlayer())) {
                createAndOpenInventory(p);
            } else {
                updateTitle(p);
                Inventory topInventory = p.getOpenInventory().getTopInventory();

                for (int i = 0; i < rows * 9; i++) {
                    if (slotItem.containsKey(i)) {
                        topInventory.setItem(i, slotItem.get(i).getStack());
                    } else {
                        topInventory.setItem(i, null);
                    }
                }

                ((MenuHolder) topInventory.getHolder()).setMenu(this);
                p.updateInventory();
            }
        } else {
            p.openInventory(inventory);
        }

        Bukkit.getPluginManager().callEvent(
                new PlayerOpenInventoryEvent(p, onePerPlayer ? p.getOpenInventory().getTopInventory() : inventory));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void updateTitle(Player player) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);

            Method getHandle = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            Field activeContainerField = entityPlayer.getClass().getField("activeContainer");
            Object activeContainer = activeContainerField.get(entityPlayer);
            Field windowIdField = activeContainer.getClass().getField("windowId");
            int id = windowIdField.getInt(activeContainer);

            packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));
            packet.getStrings().write(0, "minecraft:" + inventoryType.name().toLowerCase());
            packet.getIntegers().write(0, id);
            packet.getIntegers().write(1, rows * 9);

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAndOpenInventory(Player player) {
        Inventory playerInventory = inventoryType == InventoryType.CHEST ?
                                    Bukkit.createInventory(new MenuHolder(this), rows * 9, this.title) :
                                    Bukkit.createInventory(new MenuHolder(this), inventoryType, this.title);

        for (int i = 0; i < playerInventory.getContents().length; i++) {
            if (slotItem.containsKey(i)) {
                playerInventory.setItem(i, slotItem.get(i).getStack());
            } else {
                playerInventory.setItem(i, new ItemStack(Material.AIR));
            }
        }

        player.openInventory(playerInventory);
    }


    public void destroy(Player p) {
        if (p.getOpenInventory().getTopInventory().getHolder() != null &&
            p.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) {
            ((MenuHolder) p.getOpenInventory().getTopInventory().getHolder()).destroy();
        }
    }

    public void close(Player p) {
        p.closeInventory();
    }

    public boolean onDrag(Player player, Set<Integer> inventorySlots, Map<Integer, ItemStack> newItems, ItemStack oldCursor, ItemStack oldCursor1, DragType type) {
        if (dragHandler != null) {
            return dragHandler.onDrag(player, this, inventorySlots, newItems, oldCursor, type);
        }

        return false;
    }

    public interface DragHandler {

        boolean onDrag(Player player, MenuInventory inventory, Set<Integer> inventorySlots, Map<Integer, ItemStack> newItems, ItemStack oldCursor, DragType type);
    }
}
