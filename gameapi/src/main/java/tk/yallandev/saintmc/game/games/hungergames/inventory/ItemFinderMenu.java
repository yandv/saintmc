package tk.yallandev.saintmc.game.games.hungergames.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import net.minecraft.server.v1_8_R3.Item;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.game.games.hungergames.inventory.finder.ItemFinder;
import tk.yallandev.saintmc.game.util.SearchUtils;

@SuppressWarnings("deprecation")
public class ItemFinderMenu {
	
	private static int itemsPerPage = 36;

	public static void open(Player player, MenuInventory topInventory, ItemFinder result, int page, String search, boolean hasLimite) {
		List<ItemStack> items = itemList;
		
		if (search != null && !search.isEmpty())
			items = SearchUtils.searchItemStacks(search, items);
		
		List<Material> bugados = Arrays.asList(Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER, Material.FIRE, Material.SOIL, Material.BURNING_FURNACE, Material.PORTAL, Material.ENDER_PORTAL);
		Iterator<ItemStack> iterator = items.iterator();
		
		while (iterator.hasNext()) {
			ItemStack item = iterator.next();
			if (bugados.contains(item.getType()))
				iterator.remove();
		}
		
		if (hasLimite) {
			iterator = items.iterator();
			while (iterator.hasNext()) {
				ItemStack item = iterator.next();
				if (hasLimitString(item.getType()))
					iterator.remove();
			}
		}
		
		if (items.size() == 1) {
			result.onSelect(items.get(0));
			return;
		}
		
		MenuInventory menu = new MenuInventory("�%item-finder%� [" + page + "/" + ((int) Math.ceil(items.size() / itemsPerPage) + 1) + "]", 6, true);
		ItemStack nullItem = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();
		
		menu.setItem(new MenuItem(new ItemBuilder().type(Material.BED).name("�%back%�").build(), new MenuClickHandler() {
			@Override
			public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
				if (topInventory != null)
					topInventory.open(arg0);
				else
					arg0.closeInventory();
			}
		}), 4);
		
		menu.setItem(new MenuItem(new ItemBuilder().type(Material.COMPASS).name("�%search%�").build(), new MenuClickHandler() {
			@Override
			public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
//				InputAPI.openAnvilGui(arg0, "", new ItemBuilder().type(Material.COMPASS)
//						.name("�%search%�").lore("�%search-for-item%�").build(), new InputHandler() {
//
//							@Override
//							public void onDone(Player p, String name) {
//								if (name.isEmpty()) {
//									menu.open(p);
//								} else {
//									try {
//										if (name.contains(":")) {
//											String s = getMaterialAndDurability(name);
//											Material mat = Material.valueOf(s.split(":")[0]);
//											short durability = Short.valueOf(s.split(":")[1]);
//											if (mat != null && mat != Material.AIR) {
//												result.onSelect(new ItemStack(mat, 1, durability));
//											}
//										}
//										ItemFinderMenu.open(player, topInventory, result, 1, name, hasLimite);
//									} catch (Exception e) {
//										ItemFinderMenu.open(player, topInventory, result, 1, name, hasLimite);
//									}
//								}
//							}
//
//							@Override
//							public void onClose(Player p) {
//								menu.open(p);
//							}
//						});
			}
		}), 6);

		int pageStart = 0;
		int pageEnd = itemsPerPage;
		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}
		if (pageEnd > items.size()) {
			pageEnd = items.size();
		}
		if (page == 1) {
			menu.setItem(new ItemBuilder().type(Material.INK_SACK).durability(8).name("�%page-last-dont-have%�").build(), 0);
		} else {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.INK_SACK).durability(10).name("�%page-last-page%�").lore(Arrays.asList("�%page-last-click-here%�")).build(), new MenuClickHandler() {
				@Override
				public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
					ItemFinderMenu.open(arg0, topInventory, result, page - 1, null, hasLimite);
				}
			}), 0);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.INK_SACK).durability(10).name("�%page-next-page%�").lore(Arrays.asList("�%page-next-click-here%�")).build(), new MenuClickHandler() {
				@Override
				public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
					ItemFinderMenu.open(arg0, topInventory, result, page + 1, null, hasLimite);
				}
			}), 8);
		} else {
			menu.setItem(new ItemBuilder().type(Material.INK_SACK).durability(8).name("�%page-next-dont-have%�").build(), 8);
		}
		MenuClickHandler itemHandler = new MenuClickHandler() {
			@Override
			public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
				result.onSelect(arg3);
			}
		};
		int w = 9;
		for (int i = pageStart; i < pageEnd; i = i + 1) {
			ItemStack item = items.get(i);
			menu.setItem(new MenuItem(item, itemHandler), w);
			w += 1;
		}
		while (w < 45) {
			menu.setItem(new ItemStack(Material.AIR), w);
			w += 1;
		}
		if (items.size() == 0) {
			menu.setItem(new ItemBuilder().type(Material.PAINTING).name("�c�lOps!").lore(Arrays.asList("�%nothing-found%�")).build(), 31);
		}

		for (int i = 0; i < 9; i++) {
			if (menu.getItem(i) == null)
				menu.setItem(nullItem, i);
		}
		for (int i = 45; i < 54; i++) {
			if (menu.getItem(i) == null)
				menu.setItem(nullItem, i);
		}
		menu.open(player);
	}

	private static boolean hasLimitString(Material mat) {
		String[] blocked = new String[] { "_SWORD", //
				"_SPADE", //
				"_PICKAXE", //
				"_AXE", //
				"_HOE", //
				"_HELMET", //
				"_CHESTPLATE", //
				"_LEGGINGS", //
				"_BOOTS", //
				"BOW", //
				"FLINT_AND_STEEL", //
				"BOWL", //
				"MUSHROOM_SOUP", //
				"COMPASS", //
				"WATER_BUCKET", //
				"LAVA_BUCKET", //
				"GOLDEN_APPLE", //
				"SHEARS", //
				"ENDER_PEARL" };
		for (String s : blocked) {
			if (mat.name().contains(s)) {
				return true;
			}
		}
		return false;
	}

	private static List<ItemStack> itemList = getAllMinecraftItems();

	private static List<ItemStack> getAllMinecraftItems() {
		List<ItemStack> items = new ArrayList<>();

		Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();

		while (recipeIterator.hasNext()) {
			Recipe recipe = recipeIterator.next();
			ItemStack result = recipe.getResult();
			result.setAmount(1);
			
			if (Item.getById(result.getTypeId()) == null)
				continue;
			
			if (!containsItem(items, result.getType(), result.getDurability()))
				items.add(result);
		}

		for (Material m : Material.values()) {
			if (Item.getById(m.getId()) == null)
				continue;
			
			ItemStack result = new ItemStack(m);
			
			if (!containsItem(items, result.getType(), result.getDurability()))
				items.add(result);
		}
		
		Collections.sort(items, new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack arg0, ItemStack arg1) {
				if (arg0.getTypeId() == arg1.getTypeId())
					return arg0.getDurability() - arg1.getDurability();
				
				return arg0.getTypeId() - arg1.getTypeId();
			}
		});

		return items;
	}

	public static boolean containsItem(List<ItemStack> list, Material material, int durability) {
		for (ItemStack item : list) {
			if (item.getType() == material && item.getDurability() == (short) durability)
				return true;
		}
		return false;
	}

	public static String getMaterialAndDurability(String materialStr) {
		materialStr = materialStr.toLowerCase();
		Material material = Material.AIR;
		short durability = 0;
		if (materialStr.contains("wool") || materialStr.contains("inksack") || materialStr.contains("hclay") || materialStr.contains("hardenedclay") || materialStr.contains("glasspane") || materialStr.contains("carpet") || materialStr.contains("glass")) {
			if (materialStr.contains("wool")) {
				material = Material.WOOL;
			} else if (materialStr.contains("inksack")) {
				material = Material.INK_SACK;
			} else if (materialStr.contains("hclay") || materialStr.contains("hardenedclay")) {
				material = Material.STAINED_CLAY;
			} else if (materialStr.contains("glasspane")) {
				material = Material.STAINED_GLASS_PANE;
			} else if (materialStr.contains("carpet")) {
				material = Material.CARPET;
			} else if (materialStr.contains("glass")) {
				material = Material.STAINED_GLASS;
			}
			if (materialStr.startsWith("white")) {
				durability = 0;
			} else if (materialStr.startsWith("orange")) {
				durability = 1;
			} else if (materialStr.startsWith("magenta")) {
				durability = 2;
			} else if (materialStr.startsWith("lightblue")) {
				durability = 3;
			} else if (materialStr.startsWith("yellow")) {
				durability = 4;
			} else if (materialStr.startsWith("lime")) {
				durability = 5;
			} else if (materialStr.startsWith("pink")) {
				durability = 6;
			} else if (materialStr.startsWith("gray")) {
				durability = 7;
			} else if (materialStr.startsWith("lightgray")) {
				durability = 8;
			} else if (materialStr.startsWith("cyan")) {
				durability = 9;
			} else if (materialStr.startsWith("purple")) {
				durability = 10;
			} else if (materialStr.startsWith("blue")) {
				durability = 11;
			} else if (materialStr.startsWith("brown")) {
				durability = 12;
			} else if (materialStr.startsWith("green")) {
				durability = 13;
			} else if (materialStr.startsWith("red")) {
				durability = 14;
			} else if (materialStr.startsWith("black")) {
				durability = 15;
			} else {
				if (material == Material.STAINED_CLAY) {
					material = Material.HARD_CLAY;
				} else if (material == Material.STAINED_GLASS_PANE) {
					material = Material.THIN_GLASS;
				} else if (material == Material.STAINED_GLASS) {
					material = Material.GLASS;
				}
			}
		} else if (materialStr.contains("wood")) {
			if (materialStr.contains("planks")) {
				material = Material.WOOD;
			} else if (materialStr.contains("stair")) {
				if (materialStr.startsWith("oak")) {
					material = Material.WOOD_STAIRS;
				} else if (materialStr.startsWith("spruce")) {
					material = Material.SPRUCE_WOOD_STAIRS;
				} else if (materialStr.startsWith("birch")) {
					material = Material.BIRCH_WOOD_STAIRS;
				} else if (materialStr.startsWith("jungle")) {
					material = Material.JUNGLE_WOOD_STAIRS;
				} else if (materialStr.startsWith("acacia")) {
					material = Material.ACACIA_STAIRS;
				} else if (materialStr.startsWith("dark")) {
					material = Material.DARK_OAK_STAIRS;
				}
			} else if (materialStr.contains("slab")) {
				material = Material.WOOD_STEP;
			} else if (materialStr.contains("sapling")) {
				material = Material.SAPLING;
			} else if (materialStr.contains("leave")) {
				material = Material.LEAVES;
			} else {
				material = Material.LOG;
			}
			if (!materialStr.contains("stair")) {
				if (materialStr.startsWith("oak")) {
					durability = 0;
				} else if (materialStr.startsWith("spruce")) {
					durability = 1;
				} else if (materialStr.startsWith("birch")) {
					durability = 2;
				} else if (materialStr.startsWith("jungle")) {
					durability = 3;
				} else if (materialStr.startsWith("acacia")) {
					if (material == Material.LOG) {
						material = Material.LOG_2;
						durability = 0;
					} else if (material == Material.LEAVES) {
						material = Material.LEAVES_2;
						durability = 0;
					} else {
						durability = 4;
					}
				} else if (materialStr.startsWith("dark")) {
					if (material == Material.LOG) {
						material = Material.LOG_2;
						durability = 1;
					} else if (material == Material.LEAVES) {
						material = Material.LEAVES_2;
						durability = 1;
					} else {
						durability = 5;
					}
				}
			}
		} else if (materialStr.contains("red") && materialStr.contains("sand")) {
			material = Material.SAND;
			durability = 1;
		} else {
			if (materialStr.contains(":")) {
				try {
					durability = Short.valueOf(materialStr.split(":")[1]);
				} catch (Exception e) {
				}
				materialStr = materialStr.split(":")[0];
			}
			boolean eInteger = false;
			int id = 0;
			try {
				id = Integer.valueOf(materialStr);
				eInteger = true;
			} catch (Exception e) {
			}
			if (eInteger) {
				try {
					material = Material.getMaterial(id);
				} catch (Exception e) {
				}
			} else {
				try {
					material = Material.valueOf(materialStr.toUpperCase());
				} catch (Exception e) {
				}
			}
		}
		return material.name().toUpperCase() + ":" + durability;
	}
}
