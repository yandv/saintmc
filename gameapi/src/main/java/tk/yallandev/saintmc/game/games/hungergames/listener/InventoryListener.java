package tk.yallandev.saintmc.game.games.hungergames.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.event.game.GameStartEvent;
import tk.yallandev.saintmc.game.games.hungergames.inventory.KitSelectorMenu;
import tk.yallandev.saintmc.game.games.hungergames.inventory.KitSelectorMenu.OrderType;
import tk.yallandev.saintmc.game.games.hungergames.util.GameKit;

public class InventoryListener extends tk.yallandev.saintmc.game.listener.GameListener {

	public static final Set<UUID> TESTING = new HashSet<>();
	public static final Set<UUID> ALREADY_RANDOM_KIT = new HashSet<>();

	private ActionItemStack primarySelector;
	private ActionItemStack secondarySelector;
	
	private ActionItemStack options;
	private ActionItemStack randomKit;

	public InventoryListener(GameMain main) {
		super(main);

		this.primarySelector = new ActionItemStack(
				new ItemBuilder().type(Material.CHEST).name("§aKit 1 §7(Clique)").build(),
				new ActionItemStack.Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new KitSelectorMenu(player, 1, 1, OrderType.ALPHABET);
						return false;
					}

				});
		
		this.secondarySelector = new ActionItemStack(
				new ItemBuilder().type(Material.CHEST).name("§aKit 2 §7(Clique)").build(),
				new ActionItemStack.Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new KitSelectorMenu(player, 1, 1, OrderType.ALPHABET);
						return false;
					}

				});

		this.options = new ActionItemStack(
				new ItemBuilder().type(Material.NETHER_STAR).name("§aOpções §7(Clique)").build(),
				new ActionItemStack.Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
//						Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
//
//						if (member.hasGroupPermission(Group.LIGHT))
//							new OptionsMenu(member, player);
//						else
//							player.sendMessage("§%need-light-or-higher%§");
						return false;
					}
				});

		this.randomKit = new ActionItemStack(
				new ItemBuilder().type(Material.ENDER_CHEST).name("§aKit da partida §7(Clique)").build(),
				new ActionItemStack.Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						if (ALREADY_RANDOM_KIT.contains(player.getUniqueId())) {
							player.sendMessage("§a§l> §fKit aleatório já foi utilizado!");
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
		event.getPlayer().getInventory().setItem(0, primarySelector.getItemStack());
		event.getPlayer().getInventory().setItem(1, secondarySelector.getItemStack());
		
		
//		event.getPlayer().getInventory().setItem(1, options.getItemStack());
//		event.getPlayer().getInventory().setItem(3, randomKit.getItemStack());
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
