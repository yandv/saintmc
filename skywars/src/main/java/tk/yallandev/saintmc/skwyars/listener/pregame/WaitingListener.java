package tk.yallandev.saintmc.skwyars.listener.pregame;

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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.game.team.Team;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;
import tk.yallandev.saintmc.skwyars.menu.kit.SelectorInventory;
import tk.yallandev.saintmc.skwyars.menu.kit.SelectorInventory.InventoryType;
import tk.yallandev.saintmc.skwyars.menu.kit.SelectorInventory.OrderType;

public class WaitingListener implements Listener {

	private boolean lessTime;

	private ActionItemStack kitSelector;
	private ActionItemStack returnLobby;

	public WaitingListener() {
		kitSelector = new ActionItemStack(new ItemBuilder().type(Material.CHEST).name("§aSelecionar kit").build(),
				new ActionItemStack.Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						new SelectorInventory(player, 1, InventoryType.SELECTOR, OrderType.ALPHABET);
						return false;
					}
				});

		returnLobby = new ActionItemStack(new ItemBuilder().type(Material.BED).name("§aRetornar ao Lobby").build(),
				new ActionItemStack.Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						BukkitMain.getInstance().sendPlayerToLobby(player);
						return false;
					}
				});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (!member.hasGroupPermission(Group.BETA)) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
					"§cO servidor está aberto somente para " + Tag.BETA.getPrefix() + "§c!");
			return;
		}

		if (!member.hasGroupPermission(Group.TRIAL))
			if (GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying()) >= GameMain.getInstance()
					.getMaxPlayers())
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO jogo está cheio!");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());

		Team team = GameGeneral.getInstance().getTeamController().findTeam(gamer);

		if (team == null) {
			if (Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.TRIAL)) {
				player.sendMessage("§cVocê não tem time!");
			} else
				player.kickPlayer("§cO jogo está cheio!");
			event.setJoinMessage(null);
		} else
			event.setJoinMessage("§b" + player.getName() + " §eentrou na partida! §7("
					+ GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying()) + "/"
					+ GameMain.getInstance().getMaxPlayers() + ")");

		player.teleport(GameMain.getInstance().getLocationFromConfig("spawn"));
		handleInventory(player);

		if (GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying()) == GameMain.getInstance()
				.getMaxPlayers()) {

			if (lessTime) {
				lessTime = true;

				if (GameGeneral.getInstance().getTime() > 10) {
					Bukkit.broadcastMessage("§eTempo reduzido para §b10 segundos§e!");
					GameGeneral.getInstance().setTime(10);
				}
			}

		} else if (GameGeneral.getInstance().getGamerController()
				.count(g -> g.isPlaying()) >= GameMain.getInstance().getMaxPlayers() / 2) {

			if (!GameGeneral.getInstance().isCountTime())
				GameGeneral.getInstance().setCountTime(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameGeneral.getInstance().getTeamController()
				.handleLeave(GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId()));
		event.setQuitMessage("§b" + event.getPlayer().getName() + " §esaiu da partida! §7("
				+ (GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying()) - 1) + "/"
				+ GameMain.getInstance().getMaxPlayers() + ")");

		if (GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying()) - 1 == 0) {
			GameGeneral.getInstance().setCountTime(false);
			GameGeneral.getInstance().setTime(60);
			lessTime = false;
			return;
		}

		if (!lessTime)
			if (GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying())
					- 1 < GameMain.getInstance().getMaxPlayers() / 4) {
				if (GameGeneral.getInstance().isCountTime()) {
					GameGeneral.getInstance().setCountTime(false);
					GameGeneral.getInstance().setTime(40);
				}
			}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getTo().getY() < 0)
			event.getPlayer().teleport(GameMain.getInstance().getLocationFromConfig("spawn"));
	}

	@EventHandler
	public void onGameTime(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			if (GameGeneral.getInstance().getGamerController()
					.count(g -> g.isPlaying()) < GameMain.getInstance().getMaxPlayers() / 2)
				ActionBarAPI.broadcast("§eAguardando mais "
						+ (GameMain.getInstance().getMaxPlayers() / 2
								- GameGeneral.getInstance().getGamerController().count(g -> g.isPlaying()))
						+ " jogadores!");
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		int time = event.getTime();
		float percentage = ((time * 100) / 20) / (float) 100;
		float realPercentage = percentage > 1f ? 1f : percentage;

		GameGeneral.getInstance().getGamerController().getStoreMap().values().forEach(gamer -> {
			Player player = gamer.getPlayer();
			player.setLevel(time);
			player.setExp(realPercentage);

			ActionBarAPI.broadcast("§eKit selecionado: §a" + (gamer.hasKit() ? gamer.getKit().getName() : "Nenhum")
					+ "  §eHabilidade selecionada: §aNenhuma");
		});
	}

	private void handleInventory(Player player) {
		player.setHealth(20d);
		player.setFoodLevel(20);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.getInventory().setItem(0, kitSelector.getItemStack());
		player.getInventory().setItem(8, returnLobby.getItemStack());
	}

}
