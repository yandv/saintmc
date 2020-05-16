package tk.yallandev.saintmc.game.games.hungergames.inventory;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.account.Member;

public class OptionsMenu {
	
	public OptionsMenu(Player p, Member player) {
		MenuInventory menu = new MenuInventory("§%options-menu%§", 3);
//		Gamer gamer = Gamer.getGamer(p.getUniqueId());
//		
//		menu.setItem(11, new ItemBuilder().name("§e§l§%fly-change%§").type(Material.FEATHER).lore(p.getAllowFlight() ? "§%fly-disabled-lore%§" : "§%fly-enabled-lore%§").build(), new MenuClickHandler() {
//			
//			@Override
//			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//				p.setAllowFlight(!p.getAllowFlight());
//				stack.getItemMeta().setLore(Arrays.asList(p.getAllowFlight() ? "§%fly-disabled-lore%§" : "§%fly-enabled-lore%§"));
//				p.sendMessage("§%fly-" + (p.getAllowFlight() ? "enable" : "disable") + "%§");
//				p.closeInventory();
//			}
//			
//		});
//		
//		menu.setItem(13, new ItemBuilder().name("§e§l§%invisible-change%§").type(Material.EMERALD).lore(gamer.isInvisible() ? "§%invisible-disabled-lore%§" : "§%invisible-enabled-lore%§").build(), new MenuClickHandler() {
//			
//			@Override
//			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//				if (!player.hasGroupPermission(Group.PREMIUM)) {
//					p.sendMessage("§%need-premium-or-higher%§");
//					return;
//				}
//				
//				if (AdminMode.getInstance().isAdmin(p)) {
//					p.sendMessage("§%command-admin-prefix%§ §%command-admin-in-admin%§");
//					return;
//				}
//				
//				if (gamer.isSpectator()) {
//					p.sendMessage("§%spectator-mode-in-spectator%§");
//					return;
//				}
//				
//				if (GameMain.getPlugin().getTimer() <= 30) {
//					p.sendMessage("§%invisible-change-cant-now%§");
//					return;
//				}
//				
//				gamer.setInvisible(!gamer.isInvisible());
//				
//				if (gamer.isInvisible()) {
//					for (Player online : Bukkit.getOnlinePlayers())
//						if (online != null)
//							if (online.isOnline())
//								if (online.getUniqueId() != p.getUniqueId())
//									if (online.canSee(p))
//										online.hidePlayer(p);
//					
//					VanishAPI.getInstance().setPlayerVanishToGroup(p, Group.YOUTUBER);
//				} else {
//					VanishAPI.getInstance().removeVanish(p);
//					
//					for (Player online : Bukkit.getOnlinePlayers())
//						if (!online.canSee(p))
//							online.showPlayer(p);
//				}
//				
//				stack.getItemMeta().setLore(Arrays.asList(gamer.isInvisible() ? "§%invisible-disabled-lore%§" : "§%invisible-enabled-lore%§"));
//				p.sendMessage("§%invisible-" + (gamer.isInvisible() ? "enable" : "disable") + "%§");
//				p.closeInventory();
//			}
//			
//		});
//		
//		menu.setItem(15, new ItemBuilder().name("§e§l§%spectator-mode-change%§").type(Material.POTION).lore(gamer.isSpectator() ? "§%spectator-mode-disabled-change%§" : "§%spectator-mode-enabled-change%§").build(), new MenuClickHandler() {
//			
//			@Override
//			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//				if (player.hasGroupPermission(Group.DEV)) {
//					p.sendMessage("§c§lADMIN §fVoc§ possui a §cpermiss§o§f de entrar no modo §c§lADMIN§f.");
//					return;
//				}
//				
//				gamer.setSpectator(gamer.isSpectator());
//				
//				if (!gamer.isSpectator()) {
//					p.setGameMode(GameMode.SURVIVAL);
//					p.setAllowFlight(false);
//				}
//				
//				stack.getItemMeta().setLore(Arrays.asList(gamer.isSpectator() ? "§%spectator-mode-disabled-change%§" : "§%spectator-mode-enabled-change%§"));
//				p.sendMessage("§%spectator-mode-" + (p.getAllowFlight() ? "enable" : "disable") + "%§");
//				p.closeInventory();
//			}
//			
//		});
		
		menu.open(p);
	}

}
