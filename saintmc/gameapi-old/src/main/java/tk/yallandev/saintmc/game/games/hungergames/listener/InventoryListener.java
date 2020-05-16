package tk.yallandev.saintmc.game.games.hungergames.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractHandler;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameStartEvent;
import tk.yallandev.saintmc.game.games.hungergames.inventory.KitSelectorMenu;
import tk.yallandev.saintmc.game.games.hungergames.map.PregameMap;
import tk.yallandev.saintmc.game.games.hungergames.util.GameKit;

public class InventoryListener extends tk.yallandev.saintmc.game.listener.GameListener {
	
	public static final Set<UUID> TESTING = new HashSet<>();
	public static final Set<UUID> ALREADY_RANDOM_KIT = new HashSet<>();
	
	private ActionItemStack kitSelector;
	private ActionItemStack options;
	private ActionItemStack randomKit;

	public InventoryListener(GameMain main) {
		super(main);
		
		this.kitSelector = new ActionItemStack(new ItemBuilder().type(Material.CHEST).name("§%kitSelector-item-name%§").lore("§%kitSelector-item-lore%§").build(), new InteractHandler() {
			
			@Override
			public boolean onInteract(Player player, ItemStack item, Action action) {
				KitSelectorMenu.open(player, 1);
				return false;
			}
		});
		
		
		this.options = new ActionItemStack(new ItemBuilder().type(Material.NETHER_STAR).name("§%options-item-name%§").lore("§%options-item-lore%§").build(), new InteractHandler() {
			
			@Override
			public boolean onInteract(Player player, ItemStack item, Action action) {
//				BattlePlayer p = BattlePlayer.getPlayer(player.getUniqueId());
				
//				if (p.hasGroupPermission(Group.LIGHT))
//				new OptionsMenu(player, p);
//				else
//					player.sendMessage("§%need-light-or-higher%§");
				return false;
			}
		});
		
		this.randomKit = new ActionItemStack(new ItemBuilder().type(Material.ENDER_CHEST).name("§%random-kit-item-name%§").lore("§%random-kit-item-lore%§").build(), new InteractHandler() {
			
			@Override
			public boolean onInteract(Player player, ItemStack item, Action action) {
				if (ALREADY_RANDOM_KIT.contains(player.getUniqueId())) {
					player.sendMessage("§%random-kit-already-used%§");
					return false;
				}
				
				ALREADY_RANDOM_KIT.add(player.getUniqueId());
				new GameKit(player);
				return false;
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().getInventory().setItem(0, kitSelector.getItemStack());
		event.getPlayer().getInventory().setItem(1, options.getItemStack());
		event.getPlayer().getInventory().setItem(3, randomKit.getItemStack());
		
		MapView map = Bukkit.createMap(Bukkit.getWorlds().get(0));
		
		for (MapRenderer renderer : map.getRenderers()) {
            map.removeRenderer(renderer);
        }
		
		map.addRenderer(new PregameMap());
		
		ItemStack item = new ItemStack(Material.MAP, 1);
		MapMeta meta = (MapMeta) item.getItemMeta();
		meta.setDisplayName("§%map-item-name%§");
		meta.setScaling(true);
		item.setItemMeta(meta);
		item.setDurability(map.getId());
		event.getPlayer().getInventory().setItem(2, item);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (!TESTING.contains(e.getPlayer().getUniqueId()))
			return;
		
		Location location = e.getFrom();
		
		location.setPitch(0);
		location.setYaw(90);
		
		e.setTo(location);
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		HandlerList.unregisterAll(this);
	}

}
