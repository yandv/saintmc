package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.tablist.Tablist;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.gamer.Gamer;
import tk.yallandev.saintmc.lobby.menu.collectable.CollectableInventory;
import tk.yallandev.saintmc.lobby.menu.collectable.CollectableInventory.Page;
import tk.yallandev.saintmc.lobby.menu.profile.ProfileInventory;
import tk.yallandev.saintmc.lobby.menu.server.LobbyInventory;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory;

public class PlayerListener implements Listener {

	@Getter
	private static PlayerListener playerListener;

	private Tablist tablist;

	private ActionItemStack compass;
	private ActionItemStack lobbies;
	private ActionItemStack collectable;

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
	
		playerListener = this;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

//		if (member.getSessionTime() <= 5000) {
//			for (int x = 0; x < 100; x++)
//				member.sendMessage(" ");
//
//			member.sendMessage(StringCenter.centered("§a§l> §fSeja bem-vindo ao §aLobby§f! §a§l<"));
//			member.sendMessage(" ");
//			member.sendMessage("§e§l> §fEstamos em fase de §3desenvolvimento§f, qualquer bug report!");
//
//			TextComponent text = new TextComponent("§e§l> §fEntre em nosso ");
//			text.addExtra(createClickable("§bdiscord", CommonConst.DISCORD, "§bClique para ir ao discord!"));
//			text.addExtra("§f para ficar por dentro das atualizações§7!");
//
//			member.sendMessage(text);
//			member.sendMessage(" ");
//
//			text = new TextComponent("§c§l> §fPara obter maiores informações sobre o servidor acesse nosso ");
//			text.addExtra(createClickable("§esite", CommonConst.WEBSITE, "§eClique para ir ao site!"));
//			text.addExtra("§7!");
//
//			member.sendMessage(text);
//
//			member.sendMessage(" ");
//		}

		if (!member.hasGroupPermission(Group.LIGHT)) {
			for (Gamer gamer : LobbyMain.getInstance().getPlayerManager().getGamers())
				if (!gamer.isSeeing())
					gamer.getPlayer().hidePlayer(player);
		}

		tablist.addViewer(player);
		addItem(player, member);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		LobbyMain.getInstance().getPlayerManager().removeGamer(e.getPlayer());

		if (LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(e.getPlayer()))
			LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().remove(e.getPlayer());

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
			if (player.hasGroupPermission(Group.DEV)) {
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
			if (player.hasGroupPermission(Group.DEV)) {
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
			if (player.hasGroupPermission(Group.DEV)) {
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
		if (LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(event.getPlayer()))
			event.getItemDrop().remove();
		else
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		event.getEntity().remove();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if (LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(player)) {
			LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().remove(player);

			if (player.getKiller() != null) {
				player.getKiller().getInventory().addItem(new ItemStack(Material.RED_MUSHROOM, 16));
				player.getKiller().getInventory().addItem(new ItemStack(Material.BROWN_MUSHROOM, 16));
				player.getKiller().getInventory().addItem(new ItemStack(Material.BOWL, 16));
			}
		}

		event.getDrops().clear();
		event.setDroppedExp(0);

		event.setDeathMessage(null);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		addItem(event.getPlayer(),
				CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()));
		event.setRespawnLocation(BukkitMain.getInstance().getLocationFromConfig("spawn"));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (event.getCause() == DamageCause.FALL) {
			if (!LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(player))
				if (player.getLocation().getX() > -5 && player.getLocation().getY() < 118
						&& player.getLocation().getZ() < -40) {
					LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().add(player);

					player.getInventory().clear();
					player.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));

					for (int x = 0; x < 15; x++)
						player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
					
					player.updateInventory();
					ActionBarAPI.send(player, "§cVocê entrou na área de combate!");
				}
		}

		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

			if (damageEvent.getDamager() instanceof Player) {
				Player damager = (Player) damageEvent.getDamager();

				if (LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(damager)
						&& LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(player)) {
					event.setCancelled(false);

					if (damager.getItemInHand().getType() != null
							&& damager.getItemInHand().getType().name().contains("SWORD")) {
						damager.getItemInHand().setDurability((short) 0);
						event.setDamage(4.0D);
						damager.updateInventory();
					}

					return;
				}
			}
		}

		if (event.getCause() == DamageCause.VOID) {
			if (LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().contains(player)) {
				LobbyMain.getInstance().getPlayerManager().getPlayersInCombat().remove(player);
			}

			addItem(player, Member.getMember(player.getUniqueId()));
			event.getEntity()
					.teleport(Member.isLogged(player.getUniqueId())
							? BukkitMain.getInstance().getLocationFromConfig("spawn")
							: BukkitMain.getInstance().getLocationFromConfig("login"));
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItem();

		if (item == null || item.getType() == Material.AIR)
			return;

		if (item.getType() == Material.MUSHROOM_SOUP) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (((Damageable) p).getHealth() < ((Damageable) p).getMaxHealth() || p.getFoodLevel() < 20) {
					int restores = 7;

					event.setCancelled(true);

					if (((Damageable) p).getHealth() < ((Damageable) p).getMaxHealth())
						if (((Damageable) p).getHealth() + restores <= ((Damageable) p).getMaxHealth())
							p.setHealth(((Damageable) p).getHealth() + restores);
						else
							p.setHealth(((Damageable) p).getMaxHealth());
					else if (p.getFoodLevel() < 20)
						if (p.getFoodLevel() + restores <= 20) {
							p.setFoodLevel(p.getFoodLevel() + restores);
							p.setSaturation(5);
						} else {
							p.setFoodLevel(20);
							p.setSaturation(5);
						}

					item = new ItemStack(Material.BOWL);
					p.setItemInHand(item);
				}
			}
		}
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

	public void addItem(Player player, Member member) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.setHealth(20D);
		player.setFoodLevel(20);
		player.setLevel(member.getXp());
		
		float percentage = ((member.getXp() * 100) / member.getLeague().getMaxXp())/(float)100;
		
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
