package tk.yallandev.saintmc.bukkit.api.item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.Interact;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;

public class ActionItemListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getItem() == null)
			return;

		ItemStack stack = event.getItem();

		try {
			if (stack.getType() == Material.AIR)
				throw new Exception();

			NbtCompound compound = getNbtCompound(stack);

			if (!compound.containsKey("interactHandler")) {
				return;
			}

			Interact handler = ActionItemStack.getHandler(compound.getInteger("interactHandler"));

			if (handler == null || handler.getInteractType() == InteractType.PLAYER)
				return;

			Player player = event.getPlayer();
			Action action = event.getAction();

			event.setCancelled(handler.onInteract(player, null, event.getClickedBlock(), stack,
					action.name().contains("RIGHT") ? ActionType.RIGHT : ActionType.LEFT));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() == Material.AIR)
			return;

		ItemStack stack = event.getPlayer().getItemInHand();

		try {
			NbtCompound compound = (NbtCompound) getNbtCompound(stack);

			if (!compound.containsKey("interactHandler")) {
				return;
			}

			Interact handler = ActionItemStack.getHandler(compound.getInteger("interactHandler"));

			if (handler == null || handler.getInteractType() == InteractType.CLICK)
				return;

			Player player = event.getPlayer();

			event.setCancelled(
					handler.onInteract(player, event.getRightClicked(), null, stack, ActionType.CLICK_PLAYER));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;

		ItemStack stack = event.getCurrentItem();

		try {
			NbtCompound compound = getNbtCompound(stack);

			if (!compound.containsKey("interactHandler")) {
				return;
			}

			Interact handler = ActionItemStack.getHandler(compound.getInteger("interactHandler"));
			
			if (handler == null || handler.getInteractType() == InteractType.PLAYER || !handler.isInventoryClick())
				return;

			Player player = (Player) event.getWhoClicked();

			player.closeInventory();
			event.setCancelled(handler.onInteract(player, null, null, stack, ActionType.LEFT));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand() == null)
			return;

		ItemStack stack = event.getItemInHand();

		try {
			if (stack.getType() == Material.AIR)
				throw new Exception();

			NbtCompound compound = getNbtCompound(stack);

			if (!compound.containsKey("interactHandler")) {
				return;
			}

			Block b = event.getBlock();
			int id = compound.getInteger("interactHandler");
			b.setMetadata("interactHandler", new FixedMetadataValue(BukkitMain.getInstance(), id));
			b.getDrops().clear();
			b.getDrops().add(ActionItemStack
					.setTag(new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData()), id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();

		if (!b.hasMetadata("interactHandler"))
			return;

		b.getDrops().clear();
		b.getDrops()
				.add(ActionItemStack.setTag(new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData()),
						b.getMetadata("interactHandler").get(0).asInt()));
	}

	private NbtCompound getNbtCompound(ItemStack stack) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?> caller = MinecraftReflection.getCraftItemStackClass().getDeclaredConstructor(ItemStack.class);
		caller.setAccessible(true);
		ItemStack item = (ItemStack) caller.newInstance(stack);
		NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(item);
		return compound;
	}
}
