package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.event.PlayerItemReceiveEvent;
import tk.yallandev.saintmc.lobby.gamer.Gamer;
import tk.yallandev.saintmc.lobby.menu.collectable.CollectableInventory;
import tk.yallandev.saintmc.lobby.menu.profile.ProfileInventory;
import tk.yallandev.saintmc.lobby.menu.server.LobbyInventory;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

	private ActionItemStack compass;
	private ActionItemStack lobbies;
	private ActionItemStack collectable;

	public PlayerListener() {
		compass = new ActionItemStack(new ItemBuilder().name("§aSelecionar jogo").type(Material.COMPASS).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new ServerInventory(player);
						return false;
					}
				});

		lobbies = new ActionItemStack(new ItemBuilder().name("§aSelecionar Lobby").type(Material.NETHER_STAR).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new LobbyInventory(player);
						return false;
					}
				});

		collectable = new ActionItemStack(new ItemBuilder().name("§aColetáveis").type(Material.ENDER_CHEST).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new CollectableInventory(player);
						return false;
					}

				});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.hasGroupPermission(Group.PRO)) {
			player.setAllowFlight(true);
			player.setFlying(true);

			if (member.getGroup().ordinal() <= Group.YOUTUBER.ordinal()
					&& member.getGroup().ordinal() >= Group.PRO.ordinal())
				Bukkit.broadcastMessage(Tag.valueOf(member.getGroup().name()).getPrefix() + " " + player.getName()
						+ " §6entrou no lobby!");
		} else {
			for (Gamer gamer : LobbyMain.getInstance().getPlayerManager().getGamers())
				if (!gamer.isSeeing())
					gamer.getPlayer().hidePlayer(player);

			player.setFlying(false);
			player.setAllowFlight(false);
		}

		player.teleport(
				member.getLoginConfiguration().isLogged() ? BukkitMain.getInstance().getLocationFromConfig("spawn")
						: BukkitMain.getInstance().getLocationFromConfig("login"));

		addItem(player, member);
		Title.clear(player);
	}

	@EventHandler
	public void onPlayerChangeLoginStatus(PlayerChangeLoginStatusEvent event) {
		if (event.isLogged() || event.getMember().getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			event.getPlayer().teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
			addItem(event.getPlayer(), event.getMember());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		LobbyMain.getInstance().getPlayerManager().removeGamer(e.getPlayer());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.DEVELOPER)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	/*
	 * pvp
	 * 
	 */

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (LobbyMain.getInstance().getPlayerManager().isCombat(event.getPlayer()))
			event.getItemDrop().remove();
		else
			event.setCancelled(true);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		addItem(event.getPlayer(),
				CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()));
		event.setRespawnLocation(BukkitMain.getInstance().getLocationFromConfig("spawn"));
	}

	@EventHandler
	public void onPlayerItemReceive(PlayerItemReceiveEvent event) {
		addItem(event.getPlayer(),
				CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()));
	}

	public void addItem(Player player, Member member) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.setHealth(20D);
		player.setFoodLevel(20);
		player.setLevel(member.getXp());

		float percentage = ((member.getXp() * 100) / member.getLeague().getMaxXp()) / (float) 100;

		if (member.getLeague() == League.CLOUTH)
			player.setExp(1f);
		else
			player.setExp(percentage);

		player.getInventory().setItem(0, compass.getItemStack());
		player.getInventory()
				.setItem(1,
						new ActionItemStack(
								new ItemBuilder().name("§aMeu perfil").skin(member.getPlayerName()).durability(3)
										.type(Material.SKULL_ITEM).build(),
								new ActionItemStack.Interact(InteractType.CLICK) {

									@Override
									public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
											ActionType action) {
										new ProfileInventory(player);
										return false;
									}

								}).getItemStack());
		player.getInventory().setItem(7, collectable.getItemStack());
		player.getInventory().setItem(8, lobbies.getItemStack());
		player.updateInventory();
	}

}
