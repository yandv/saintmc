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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.tablist.Tablist;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.tournment.TournamentGroup;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.event.PlayerItemReceiveEvent;
import tk.yallandev.saintmc.lobby.gamer.Gamer;
import tk.yallandev.saintmc.lobby.menu.collectable.CollectableInventory;
import tk.yallandev.saintmc.lobby.menu.collectable.CollectableInventory.Page;
import tk.yallandev.saintmc.lobby.menu.profile.ProfileInventory;
import tk.yallandev.saintmc.lobby.menu.server.LobbyInventory;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory;
import tk.yallandev.saintmc.lobby.menu.tournament.TournamentInventory;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

	@Getter
	private static PlayerListener playerListener;

	private Tablist tablist;

	private ActionItemStack compass;
	private ActionItemStack lobbies;
	private ActionItemStack collectable;
	private ActionItemStack tournament;

	public PlayerListener() {
		tablist = new Tablist(
				"\n§6§lSAINT§f§lMC\n§f\n§7Nome: §f%name% §9- §7Grupo: %group%\n§f                                                 §f",
				"\n§a" + CommonConst.SITE + "\n§b" + CommonConst.DISCORD.replace("http://", "") + "\n§f ") {

			@Override
			public String[] replace(Player player, String header, String footer) {
				Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

				header = header.replace("%group%", member.getGroup() == Group.MEMBRO ? "§7§lMEMBRO"
						: Tag.valueOf(member.getGroup().name()).getPrefix());
				header = header.replace("%name%", member.getPlayerName());

				footer = footer.replace("%name%", member.getPlayerName());
				footer = footer.replace(".br/", "");

				return new String[] { header, footer };
			}

		};

		compass = new ActionItemStack(
				new ItemBuilder().name("§eSelecionar jogo §7(Clique Aqui)").type(Material.COMPASS).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new ServerInventory(player);
						return false;
					}
				});

		lobbies = new ActionItemStack(
				new ItemBuilder().name("§eSelecionar lobby §7(Clique Aqui)").type(Material.NETHER_STAR).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new LobbyInventory(player);
						return false;
					}
				});

		collectable = new ActionItemStack(
				new ItemBuilder().name("§eColetáveis §7(Clique Aqui)").type(Material.ENDER_CHEST).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new CollectableInventory(player, Page.FIRST);
						return false;
					}

				});

		tournament = new ActionItemStack(
				new ItemBuilder().name("§eTorneio §7(Clique Aqui)").glow().type(Material.DIAMOND).build(),
				new ActionItemStack.Interact(InteractType.CLICK) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new TournamentInventory(player, null, false, false);
						return false;
					}

				});

		playerListener = this;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.hasGroupPermission(Group.LIGHT)) {

			player.setAllowFlight(true);
			player.setFlying(true);

			if (member.getGroup().ordinal() <= Group.YOUTUBER.ordinal()
					&& member.getGroup().ordinal() >= Group.BLIZZARD.ordinal())
				Bukkit.broadcastMessage(Tag.valueOf(member.getGroup().name()).getPrefix() + " " + player.getName()
						+ " §6entrou no lobby!");

		} else {
			for (Gamer gamer : LobbyMain.getInstance().getPlayerManager().getGamers())
				if (!gamer.isSeeing())
					gamer.getPlayer().hidePlayer(player);

			player.setFlying(false);
			player.setAllowFlight(false);
		}

		if (member.getTournamentGroup() == null || member.getTournamentGroup() == TournamentGroup.NONE)
			if (player.hasPermission("tag.torneioplus") && !member.hasGroupPermission(Group.TRIAL)) {
				member.sendMessage("§aVocê comprou a tag " + Tag.TORNEIOPLUS.getPrefix()
						+ "§a mas ainda não selecionou seu grupo!");
				member.sendMessage("§aClique no diamante da hotbar para selecionar o grupo!");
				member.sendMessage("§c§nCaso você não escolha o grupo, você não participará do torneio!");
			}

		player.teleport(
				member.getLoginConfiguration().isLogged() ? BukkitMain.getInstance().getLocationFromConfig("spawn")
						: BukkitMain.getInstance().getLocationFromConfig("login"));

		tablist.addViewer(player);
		addItem(player, member);
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

		tablist.removeViewer(e.getPlayer());
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.CUSTOM)
			return;

		if (e.getEntity() instanceof Player)
			return;

		e.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		e.setCancelled(true);
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

	@EventHandler
	public void onBlockBreak(BlockPlaceEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.DEVELOPER)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler
	public void onBucket(PlayerBucketEmptyEvent event) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.DEVELOPER)) {
				event.setCancelled(false);
				return;
			}

		event.setCancelled(true);
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
	public void onItemSpawn(ItemSpawnEvent event) {
		event.getEntity().remove();
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		addItem(event.getPlayer(),
				CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()));
		event.setRespawnLocation(BukkitMain.getInstance().getLocationFromConfig("spawn"));
	}

	@EventHandler
	public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				tablist.updateTab(event.getPlayer());
			}
		}.runTaskLater(LobbyMain.getInstance(), 10l);
	}

	@EventHandler
	public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				tablist.updateTab(event.getPlayer());
			}
		}.runTaskLater(LobbyMain.getInstance(), 10l);
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

		if (member.getLeague() == League.CHALLENGER)
			player.setExp(1f);
		else
			player.setExp(percentage);

		player.getInventory().setItem(0, compass.getItemStack());
		player.getInventory().setItem(1,
				new ActionItemStack(
						new ItemBuilder().name("§eSeu perfil §7(Clique Aqui)").skin(member.getPlayerName())
								.durability(3).type(Material.SKULL_ITEM).build(),
						new ActionItemStack.Interact(InteractType.CLICK) {

							@Override
							public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
									ActionType action) {
								new ProfileInventory(player);
								return false;
							}

						}).getItemStack());
		player.getInventory().setItem(4, tournament.getItemStack());
		player.getInventory().setItem(7, collectable.getItemStack());
		player.getInventory().setItem(8, lobbies.getItemStack());
		player.updateInventory();
	}

	public TextComponent createClickable(String message, String hoverMessage, String url) {
		TextComponent textComponent = new TextComponent(message);

		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		textComponent
				.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverMessage)));

		return textComponent;
	}
}
