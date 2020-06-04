package tk.yallandev.saintmc.bukkit.api.item;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import lombok.Getter;

public class ActionItemStack {

	private static final HashMap<Integer, Interact> handlers = new HashMap<>();

	@Getter
	private Interact interactHandler;
	@Getter
	private ItemStack itemStack;

	public ActionItemStack(ItemStack stack, Interact handler) {
		itemStack = setTag(stack, register(handler));
		
		if (itemStack == null)
			itemStack = stack;
		
		interactHandler = handler;
	}

	public static int register(Interact handler) {
		handlers.put(handlers.size() + 1, handler);
		return handlers.size();
	}

	public static void unregister(Integer id) {
		handlers.remove(id);
	}

	public static Interact getHandler(Integer id) {
		return handlers.get(id);
	}

	public static ItemStack setTag(ItemStack stack, int id) {
		try {
			if (stack == null || stack.getType() == Material.AIR)
				throw new Exception();
			Constructor<?> caller = MinecraftReflection.getCraftItemStackClass()
					.getDeclaredConstructor(ItemStack.class);
			caller.setAccessible(true);
			ItemStack item = (ItemStack) caller.newInstance(stack);
			NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(item);
			compound.put("interactHandler", id);
			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Getter
	public static abstract class Interact {
		
		private InteractType interactType;
		
		public Interact() {
			this.interactType = InteractType.CLICK;
		}
		
		public Interact(InteractType interactType) {
			this.interactType = interactType;
		}

		public abstract boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action);
		
	}
	
	public enum ActionType {
		
		CLICK_PLAYER, RIGHT, LEFT;
		
	}
	
	public enum InteractType {
		
		PLAYER, CLICK;
		
	}

}
