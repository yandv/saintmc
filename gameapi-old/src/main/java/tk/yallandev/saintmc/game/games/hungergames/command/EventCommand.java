package tk.yallandev.saintmc.game.games.hungergames.command;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;

public class EventCommand implements CommandClass {

	@Command(name = "evento", groupToUse = Group.MODPLUS)
	public void abilityList(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;
		
		new EventInventory(args.getPlayer(), CommonGeneral.getInstance().getMemberManager().getMember(args.getPlayer().getUniqueId()));
	}
	
	public class EventInventory {
		
		private int time = 1;
		private String armor = "Nenhum";
		private String sword = "Nenhuma";
		
		private boolean soup = false;
		private boolean recraft = false;
		
		public EventInventory(Player p, Member player, int t, String a, String s, boolean ss, boolean r) {
			this.time = t;
			this.armor = a;
			this.sword = s;
			this.soup = ss;
			this.recraft = r;
			
			MenuInventory menu = new MenuInventory("§nEvento", 2);
			
			menu.setItem(0, new ItemBuilder().type(Material.WOOL).durability(14).name("§c§%cancel%§").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					p.closeInventory();
					p.sendMessage("§%cancelled-success%§");
				}
				
			});
			
			menu.setItem(9, new ItemBuilder().type(Material.WOOL).durability(14).name("§c§%cancel%§").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					p.closeInventory();
					p.sendMessage("§%cancelled-success%§");
				}
				
			});
			
			menu.setItem(3, new ItemBuilder().type(Material.WATCH).name("§a§%event-invincibility-time%§ §7" + time + " minuto(s)").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					if (time == 1) {
						time = 2;
					} else if (time == 2) {
						time = 5;
					} else if (time == 5) {
						time = 10;
					} else if (time == 10) {
						time = 15;
					} else if (time == 15) {
						time = 1;
					}
					
					new EventInventory(p, player, time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.setItem(4, new ItemBuilder().type(armor.equalsIgnoreCase("Nenhum") ? Material.GLASS : armor.equalsIgnoreCase("couro") ? Material.LEATHER_CHESTPLATE : armor.equalsIgnoreCase("ferro") ? Material.IRON_CHESTPLATE : Material.DIAMOND_CHESTPLATE).name("§a§%event-armor-type%§ §7Nenhum").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					if (armor.equalsIgnoreCase("Nenhum")) {
						armor = "Couro";
					} else if (armor.equalsIgnoreCase("couro")) {
						armor = "Ferro";
					} else if (armor.equalsIgnoreCase("ferro")) {
						armor = "Diamante";
					} else if (armor.equalsIgnoreCase("diamante")) {
						armor = "Nenhum";
					}
					
					new EventInventory(p, player, time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.setItem(5, new ItemBuilder().type(sword.equalsIgnoreCase("nenhuma") ? Material.GLASS : sword.equalsIgnoreCase("madeira") ? Material.WOOD_SWORD : sword.equalsIgnoreCase("pedra") ? Material.STONE_SWORD : sword.equalsIgnoreCase("ferro") ? Material.IRON_SWORD : Material.DIAMOND_SWORD).name("§a§%event-sword-type%§ §7Nenhuma").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					
					if (sword.equalsIgnoreCase("nenhuma")) {
						sword = "Madeira";
					} else if (sword.equalsIgnoreCase("madeira")) {
						sword = "Pedra";
					} else if (sword.equalsIgnoreCase("pedra")) {
						sword = "Ferro";
					} else if (sword.equalsIgnoreCase("ferro")) {
						sword = "Diamante";
					} else if (sword.equalsIgnoreCase("diamante")) {
						sword = "Nenhuma";
					}
					
					new EventInventory(p, player, time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.setItem(12, new ItemBuilder().type(soup ? Material.MUSHROOM_SOUP : Material.BOWL).name("§a§%event-give-soup%§ §7" + (soup ? "Sim" : "N§o")).build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					soup = !soup;
					new EventInventory(p, player, time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.setItem(13, new ItemBuilder().type(recraft ? Material.RED_MUSHROOM : Material.GLASS).name("§a§%event-give-recraft%§ §7" + (recraft ? "Sim" : "N§o")).build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					recraft = !recraft;
					new EventInventory(p, player, time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.setItem(8, new ItemBuilder().type(Material.WOOL).durability(5).name("§a§%confirm%§").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					p.closeInventory();
					start(time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.setItem(17, new ItemBuilder().type(Material.WOOL).durability(5).name("§a§%confirm%§").build(), new MenuClickHandler() {
				
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					p.closeInventory();
					start(time, armor, sword, soup, recraft);
				}
				
			});
			
			menu.open(p);
		}
		
		public EventInventory(Player p, Member player) {
			this(p, player, 1, "Nenhum", "Nenhuma", false, false);
		}
	}
	
	public class Evento {
		
	}
	
	public void start(int time, String armor, String sword, boolean soup, boolean recraft) {
		HungerGamesMode.INVINCIBILITY_TIME = time;
	}
	
}
