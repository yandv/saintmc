package tk.yallandev.saintmc.skwyars.game.chest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

@AllArgsConstructor
@Getter
public class Chest {

	private static final Random RANDOM = new Random();

	private static ActionItemStack rocket = new ActionItemStack(
			new ItemBuilder().type(Material.FIREWORK).name("§aFoguete").build(), new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {

					if (CooldownController.getInstance().hasCooldown(player, "Foguete")) {
						player.sendMessage("§cVocê precisa esperar mais um pouco para usar o foguete novamente!");
						return false;
					}

					if (item.getAmount() - 1 <= 0)
						player.setItemInHand(null);
					else
						item.setAmount(item.getAmount() - 1);

					final org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) player.getWorld()
							.spawnEntity(player.getLocation().add(0.5, 1.2, 0.5), EntityType.FIREWORK);
					Builder builder = FireworkEffect.builder();
					FireworkMeta m = firework.getFireworkMeta();
					builder.withColor(Color.RED);
					builder.with(Type.CREEPER);
					builder.withFade(Color.GREEN);
					m.addEffect(builder.build());
					m.setPower(1);
					firework.setFireworkMeta(m);
					firework.setPassenger(player);

					player.setMetadata("nofall", new FixedMetadataValue(GameMain.getInstance(), 6l));
					CooldownController.getInstance().addCooldown(player.getUniqueId(), "Foguete", 6l);
					return false;
				}
			});

	private static ActionItemStack strenghtFlower = new ActionItemStack(
			new ItemBuilder().name("§aFlor da Força").type(Material.RED_ROSE).build(), new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {

					if (item.getAmount() - 1 <= 0)
						player.setItemInHand(null);
					else
						item.setAmount(item.getAmount() - 1);

					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 0));
					return true;
				}
			});

	private static ActionItemStack energetic = new ActionItemStack(
			new ItemBuilder().name("§aEnergético").type(Material.SUGAR).build(), new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {

					if (item.getAmount() - 1 <= 0)
						player.setItemInHand(null);
					else
						item.setAmount(item.getAmount() - 1);

					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1));
					return true;
				}
			});

//	private static ActionItemStack blackPearl = new ActionItemStack(
//			new ItemBuilder().name("§aPérola Negra").type(Material.EYE_OF_ENDER).build(),
//			new ActionItemStack.Interact() {
//
//				@Override
//				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
//						ActionType action) {
//
//					if (entity instanceof Player) {
//						((Player) entity)
//								.addPotionEffect(new PotionEffect(Arrays
//										.asList(PotionEffectType.HUNGER, PotionEffectType.POISON,
//												PotionEffectType.WEAKNESS, PotionEffectType.WITHER)
//										.stream().findAny().orElse(null), 20 * 5, 1));
//						if (item.getAmount() - 1 <= 0)
//							player.setItemInHand(null);
//						else
//							item.setAmount(item.getAmount() - 1);
//					}
//					return false;
//				}
//			});

	private static ActionItemStack healFlower = new ActionItemStack(
			new ItemBuilder().name("§aFlor da Vida").type(Material.YELLOW_FLOWER).build(),
			new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {

					if (item.getAmount() - 1 <= 0)
						player.setItemInHand(null);
					else
						item.setAmount(item.getAmount() - 1);

					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 1));
					return true;
				}
			});

	private int x;
	private int y;
	private int z;

	private ChestType chestType;

	/**
	 * Clear chest
	 * 
	 * @param world
	 */

	public void clear(World world) {
		getChest(world).getBlockInventory().clear();
	}

	/**
	 * Fill the chest
	 * 
	 * @param world
	 */

	public void fill(World world) {
		org.bukkit.block.Chest chest = getChest(world);

		if (chest == null)
			return;

		Iterator<ItemStack> iterator = generateItems().iterator();

		while (iterator.hasNext()) {
			Inventory inv = chest.getBlockInventory();

			if (inv.firstEmpty() == -1)
				break;

			ItemStack item = iterator.next();

			if (item.getAmount() >= 0) {
				int slot = RANDOM.nextInt(inv.getSize());

				while (!(chest.getBlockInventory().getItem(slot) == null
						|| chest.getBlockInventory().getItem(slot).getType() == Material.AIR)) {
					slot = RANDOM.nextInt(inv.getSize());
				}

				chest.getBlockInventory().setItem(slot, item);

				chest.update();
			}

			iterator.remove();
		}
	}

	public org.bukkit.block.Chest getChest(World world) {
		Block block = new Location(world, x, y, z).getBlock();

		if (block.getType() != Material.CHEST)
			return null;

		return (org.bukkit.block.Chest) block.getState();
	}

	public List<ItemStack> generateItems() {
		List<ItemStack> feastItems = new ArrayList<>();

		switch (chestType) {
		case DEFAULT: {

			if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
					.name("§cEspada de Diamante").build(), 30, feastItems))
				if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).name("§aEspada de Diamante").build(), 70,
						feastItems))
					addItem(new ItemBuilder().type(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
							.name("§aEspada de Ferro").build(), 100, feastItems);

			if (!addItem(new ItemStack(Material.DIAMOND_HELMET), 40, feastItems))
				addItem(new ItemStack(Material.IRON_HELMET), 100, feastItems);

			if (!addItem(new ItemStack(Material.DIAMOND_LEGGINGS), 30, feastItems))
				addItem(new ItemStack(Material.IRON_LEGGINGS), 100, feastItems);

			addItem(new ItemStack(Material.STONE, RANDOM.nextBoolean() ? 16 : 32), 80, feastItems);

			if (GameMain.getInstance().isLuckyWars())
				addItem(new ItemBuilder().name("§aLucky Block").glow().type(Material.STAINED_GLASS).durability(4).build(), 100,
						feastItems, 3, 7);
			break;
		}
		case DEFAULT_2: {

			if (!addItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 30, feastItems))
				addItem(new ItemStack(Material.IRON_CHESTPLATE), 100, feastItems);

			if (!addItem(new ItemStack(Material.DIAMOND_BOOTS), 40, feastItems))
				addItem(new ItemStack(Material.IRON_BOOTS), 100, feastItems);

			addItem(new ItemStack(Material.IRON_PICKAXE), 90, feastItems);

			addItem(new ItemStack(Material.COOKED_BEEF, RANDOM.nextBoolean() ? 8 : 16), 80, feastItems);

			if (addItem(new ItemStack(Material.FISHING_ROD), 40, feastItems))
				addItem(new ItemStack(Material.SNOW_BALL, RANDOM.nextBoolean() ? 8 : 16), 100, feastItems);

			addItem(new ItemStack(Material.WOOD, RANDOM.nextBoolean() ? 16 : 32), 100, feastItems);

			if (!addItem(new ItemBuilder().type(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE).build(), 50,
					feastItems))
				addItem(new ItemBuilder().type(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3).build(), 20,
						feastItems);
			break;
		}
		case DEFAULT_3: {
			if (!addItem(new ItemStack(Material.DIAMOND_AXE), 60, feastItems))
				addItem(new ItemStack(Material.IRON_AXE), 100, feastItems);

			addItem(new ItemStack(Material.EXP_BOTTLE, 32), 80, feastItems);
			break;
		}
		case MINIFEAST: {

			addItem(rocket.getItemStack(), 30, feastItems, 1, 2);
			addItem(strenghtFlower.getItemStack(), 60, feastItems, 1, 2);
			addItem(energetic.getItemStack(), 40, feastItems, 1, 2);
			addItem(healFlower.getItemStack(), 30, feastItems, 1, 2);

			if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
					.enchantment(Enchantment.FIRE_ASPECT).name("§4Espada de Diamante").build(), 25, feastItems))
				addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
						.name("§4Espada de Diamante").build(), 75, feastItems);

			addItem(new ItemStack(Material.DIAMOND_HELMET), 49, feastItems);
			addItem(new ItemStack(Material.DIAMOND_CHESTPLATE), 35, feastItems);
			addItem(new ItemStack(Material.DIAMOND_LEGGINGS), 36, feastItems);
			addItem(new ItemStack(Material.DIAMOND_BOOTS), 42, feastItems);

			addItem(new ItemStack(Material.ARROW), 55, feastItems, 16, 32);

			addItem(new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextBoolean() ? 3 : 6), 64, feastItems);
			addItem(new ItemStack(Material.ENDER_PEARL, RANDOM.nextBoolean() ? 1 : 2), 28, feastItems);
			addItem(new ItemStack(Material.ARROW, RANDOM.nextBoolean() ? 16 : 32), 100, feastItems);
			addItem(new ItemStack(Material.EXP_BOTTLE, RANDOM.nextBoolean() ? 16 : 32), 82, feastItems);
			addItem(new ItemStack(Material.COMPASS), 91, feastItems);

			if (GameMain.getInstance().isLuckyWars())
				addItem(new ItemBuilder().name("§aLucky Block").glow().type(Material.STAINED_GLASS).durability(4).build(), 80,
						feastItems, 4, 12);

			break;
		}
		case FEAST: {
			addItem(rocket.getItemStack(), 45, feastItems, 1, 4);
			addItem(strenghtFlower.getItemStack(), 55, feastItems, 1, 4);
			addItem(energetic.getItemStack(), 55, feastItems, 1, 4);
			addItem(healFlower.getItemStack(), 45, feastItems, 1, 4);

			if (!addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
					.enchantment(Enchantment.FIRE_ASPECT).name("§4Espada de Diamante").build(), 40, feastItems))
				addItem(new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1)
						.name("§4Espada de Diamante").build(), 90, feastItems);

			addItem(new ItemBuilder().type(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 5).name("§cArco")
					.build(), 63, feastItems);

			addItem(new ItemBuilder().type(Material.ARROW).build(), 80, feastItems, 32, 64);

			addItem(new ItemBuilder().type(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
					.build(), 52, feastItems);
			addItem(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE)
					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build(), 45, feastItems);
			addItem(new ItemBuilder().type(Material.DIAMOND_LEGGINGS)
					.enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build(), 44, feastItems);
			addItem(new ItemBuilder().type(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
					.build(), 56, feastItems);

			addItem(new ItemStack(Material.ENDER_PEARL), 40, feastItems, 5, 7);
			addItem(new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextBoolean() ? 3 : 5), 38, feastItems);
			addItem(new ItemStack(Material.COMPASS), 35, feastItems);

			addItem(new ItemStack(Material.EXP_BOTTLE, RANDOM.nextBoolean() ? 32 : 64), 82, feastItems);

			if (GameMain.getInstance().isLuckyWars())
				addItem(new ItemBuilder().name("§aLucky Block").glow().type(Material.STAINED_GLASS).durability(4).build(), 90,
						feastItems, 6, 16);

			break;
		}
		}

		Collections.shuffle(feastItems);
		return feastItems;
	}

	private boolean addItem(ItemStack mat, int chance, List<ItemStack> items) {
		if (RANDOM.nextInt(100) + 1 <= chance) {
			items.add(mat);
			return true;
		}

		return false;
	}

	private boolean addItem(ItemStack itemStack, int chance, List<ItemStack> items, int maxAmount, int minAmount) {
		if (RANDOM.nextInt(100) + 1 <= chance) {
			itemStack.setAmount(minAmount + (CommonConst.RANDOM.nextInt(maxAmount) + 1));
			items.add(itemStack);
			return true;
		}

		return false;
	}

	/**
	 * Prevent duplicated chest
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chest) {
			Chest chest = (Chest) obj;

			return chest.getX() == getX() && chest.getY() == getY() && chest.getZ() == getZ();
		}

		return super.equals(obj);
	}

}
