package tk.yallandev.saintmc.game.constructor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class CustomOption {

	private transient String name;
	private transient ItemStack icon;
	private int value;
	private Material material;
	private short durability;
	private int amount;
	private transient String itemName;
	private transient int defaultValue;
	private transient int maxValue;
	private transient int minValue;
	private transient int multiplier;

	public CustomOption() {

	}

	public CustomOption(String name, ItemStack changeIcon, String itemName) {
		this.name = name;
		this.itemName = itemName;
		this.material = changeIcon.getType();
		this.durability = changeIcon.getDurability();
		this.amount = changeIcon.getAmount();
		this.value = -2;
	}

	public CustomOption(int value) {
		this.value = value;
	}

	public void setItem(ItemStack item) {
		this.material = item.getType();
		this.durability = item.getDurability();
	}

	public boolean isItem() {
		return value < 0;
	}

	public Material getMaterial() {
		return material;
	}

	public short getDurability() {
		return durability;
	}

	public ItemStack getItemStack() {
		return new ItemBuilder().name(itemName).amount(amount).type(material).durability(durability).build();
	}

	public CustomOption(String name, ItemStack icon, int multiplier, int minValue, int defaultValue, int maxValue) {
		this.name = name;
		this.icon = icon;
		this.value = defaultValue;
		this.defaultValue = defaultValue;
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.multiplier = multiplier;
	}

	public String getName() {
		return name;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public CustomOption copy(CustomOption value) {
		CustomOption COPY = new CustomOption(name, icon, multiplier, minValue, defaultValue, maxValue);
		COPY.material = value.getMaterial();
		COPY.durability = value.getDurability();
		COPY.value = value.getValue();
		COPY.itemName = this.itemName;
		return COPY;
	}

	public CustomOption clone() {
		CustomOption COPY = new CustomOption();
		COPY.defaultValue = defaultValue;
		COPY.material = material;
		COPY.itemName = itemName;
		COPY.durability = durability;
		COPY.value = value;
		COPY.maxValue = maxValue;
		COPY.minValue = minValue;
		COPY.icon = icon;
		COPY.name = name;
		COPY.multiplier = multiplier;
		return COPY;
	}

}
