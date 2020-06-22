package tk.yallandev.saintmc.kitpvp.warp.types.party.register;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.party.PartyEndEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.warp.types.PartyWarp;
import tk.yallandev.saintmc.kitpvp.warp.types.party.Party;
import tk.yallandev.saintmc.kitpvp.warp.types.party.PartyType;

public class RdmParty implements Party {

	@Getter
	@Setter
	private int time;
	private MinigameState minigameState;

	private List<Player> playerList;
	private List<Player> spectateList;

	private List<Player> playerInCombat;

	private int maxPlayers;

	public RdmParty() {
		time = 300;
		minigameState = MinigameState.STARTING;

		playerList = new ArrayList<>();
		spectateList = new ArrayList<>();

		playerInCombat = new ArrayList<>();
		maxPlayers = 80;
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			if (minigameState == MinigameState.STARTING) {
				if (time <= 0) {
					GameMain.getInstance().getGamerManager().getGamers()
							.forEach(gamer -> gamer.getPlayer().sendMessage("§6§lRDM §fO evento começou!"));
					start();
					return;
				}

				if (time == 5 || time == 10 || time == 15 || time % 30 == 0) {
					Bukkit.broadcastMessage(
							"§6§lRDM §fO evento começará em §a" + DateUtils.formatDifference(time) + "§f!");
				}

				time--;
			} else
				time++;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
		Player player = playerDeathEvent.getEntity();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.getWarp() instanceof PartyWarp)
			killPlayer(player, player.getKiller());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
		if (!(entityDamageEvent.getEntity() instanceof Player))
			return;

		Player player = (Player) entityDamageEvent.getEntity();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (!(gamer.getWarp() instanceof PartyWarp))
			return;

		if (playerInCombat.contains(player)) {
			if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entityDamageEvent;

				if (!(entityDamageByEntityEvent.getDamager() instanceof Player))
					return;

				Player damager = (Player) entityDamageByEntityEvent.getDamager();

				if (playerInCombat.contains(damager))
					entityDamageEvent.setCancelled(false);
				else
					entityDamageEvent.setCancelled(true);
			}
		} else
			entityDamageEvent.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (playerList.contains(player))
			return;

		if (minigameState == MinigameState.STARTING) {
			playerList.remove(player);
		} else {
			if (playerInCombat.contains(player)) {
				killPlayer(player, null);
			}

			if (spectateList.contains(player))
				spectateList.remove(player);

			playerList.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		if (event.getWarp() instanceof PartyWarp)
			System.out.println(event.getPlayer().getName() + " entro estranho mn kk");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerWarpQuit(PlayerWarpQuitEvent event) {
		if (event.getWarp() instanceof PartyWarp)
			if (playerList.contains(event.getPlayer()) || spectateList.contains(event.getPlayer()))
				leave(event.getPlayer());
	}

	public void searchPlayer() {
		if (playerList.size() <= 1) {
			checkWin(playerList.stream().findFirst().orElse(null));
			return;
		}

		Player firstPlayer = playerList.stream().findFirst().orElse(null);
		Player secondPlayer = playerList.stream()
				.filter(player -> !player.getUniqueId().equals(firstPlayer.getUniqueId())).findFirst().orElse(null);

		if (!playerInCombat.contains(firstPlayer))
			playerInCombat.add(firstPlayer);

		if (!playerInCombat.contains(secondPlayer))
			playerInCombat.add(secondPlayer);

		firstPlayer.getInventory().clear();
		firstPlayer.getInventory().setArmorContents(new ItemStack[4]);

		firstPlayer.setHealth(20D);
		firstPlayer.setFoodLevel(20);

		firstPlayer.getInventory().setItem(0, new ItemBuilder().unbreakable().type(Material.DIAMOND_SWORD)
				.enchantment(Enchantment.DAMAGE_ALL, 1).build());
		firstPlayer.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
		firstPlayer.getInventory()
				.setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
		firstPlayer.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
		firstPlayer.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());

		for (int x = 0; x < 8; x++)
			firstPlayer.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		secondPlayer.getInventory().clear();
		secondPlayer.getInventory().setArmorContents(new ItemStack[4]);

		secondPlayer.setHealth(20D);
		secondPlayer.setFoodLevel(20);

		secondPlayer.getInventory().setItem(0, new ItemBuilder().unbreakable().type(Material.DIAMOND_SWORD)
				.enchantment(Enchantment.DAMAGE_ALL, 1).build());
		secondPlayer.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
		secondPlayer.getInventory()
				.setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
		secondPlayer.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
		secondPlayer.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());

		for (int x = 0; x < 8; x++)
			secondPlayer.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		firstPlayer.teleport(BukkitMain.getInstance().getLocationFromConfig("rdm.pos1"));
		secondPlayer.teleport(BukkitMain.getInstance().getLocationFromConfig("rdm.pos2"));

		secondPlayer.sendMessage("§a§lRDM §fVocê irá batalhar contra o §a" + firstPlayer.getName() + "§f!");
		firstPlayer.sendMessage("§a§lRDM §fVocê irá batalhar contra o §a" + secondPlayer.getName() + "§f!");

		VanishAPI.getInstance().hideAllPlayers(firstPlayer);
		VanishAPI.getInstance().hideAllPlayers(secondPlayer);

		secondPlayer.showPlayer(firstPlayer);
		firstPlayer.showPlayer(secondPlayer);

		broadcast("§a§lRDM §fO jogador §a" + firstPlayer.getName() + "§f irá batalhar contra o §a"
				+ secondPlayer.getName() + "§f!");
	}

	private void killPlayer(Player player, Player k) {
		if (playerList.contains(player))
			playerList.remove(player);
		
		if (playerInCombat.contains(player))
			playerInCombat.remove(player);
		else {
			broadcast("§6§lRDM §fO jogador §c" + player.getName() + "§f saiu do evento§f!");
			return;
		}

		if (k == null) {
			if (playerInCombat.contains(player)) {
				k = playerInCombat.stream().filter(p -> !p.getUniqueId().equals(player.getUniqueId())).unordered()
						.findFirst().orElse(null);

			} else {
				k = playerInCombat.get(0);
			}
		}

		Player killer = k;

		if (killer == null)
			broadcast("§6§lRDM §fO jogador §c" + player.getName() + "§f foi eliminado do evento§f!");
		else
			broadcast("§6§lRDM §fO jogador §c" + player.getName() + "§f foi eliminado pelo §a" + killer.getName()
					+ "§f!");
		
		broadcast("§6§lRDM §fAinda há " + playerList.size() + " jogadores vivos!");

		VanishAPI.getInstance().getHideAllPlayers().remove(player.getUniqueId());
		VanishAPI.getInstance().updateVanishToPlayer(player);

		if (killer != null) {
			VanishAPI.getInstance().getHideAllPlayers().remove(killer.getUniqueId());
			VanishAPI.getInstance().updateVanishToPlayer(killer);

			killer.getInventory().setArmorContents(new ItemStack[4]);
			killer.getInventory().clear();
			killer.teleport(BukkitMain.getInstance().getLocationFromConfig("rdm"));
		}

		player.sendMessage("§c§l> §fVocê foi eliminado do evento §aRei da Mesa§f!");

		if (Member.hasGroupPermission(player.getUniqueId(), Group.BLIZZARD)) {
			spectate(GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()));
		} else {
			leave(player);
		}

		searchPlayer();
	}

	public void checkWin(Player winner) {
		for (Player player : Stream.concat(playerList.stream(), spectateList.stream()).toArray(Player[]::new)) {
			leave(player);
		}

		Bukkit.getPluginManager().callEvent(new PartyEndEvent(this, PartyType.RDM, winner));

		if (winner == null) {
			Bukkit.broadcastMessage("§aO evento terminou sem nenhum  jogador!");
			return;
		}

		Bukkit.broadcastMessage("§a" + winner.getName() + " ganhou o evento Rei da Mesa!");
	}

	public void broadcast(String message) {
		Stream.concat(playerList.stream(), spectateList.stream()).forEach(player -> player.sendMessage(message));
	}

	@Override
	public void start() {
		minigameState = MinigameState.GAMETIME;
		time = 0;
		searchPlayer();
	}

	@Override
	public void forceEnd(Player winner) {
		for (Player player : Stream.concat(playerList.stream(), spectateList.stream()).toArray(Player[]::new)) {
			leave(player);
		}
	}

	@Override
	public boolean join(Gamer gamer) {

		Player player = gamer.getPlayer();

		if (hasStarted()) {
			if (Member.hasGroupPermission(gamer.getUuid(), Group.TRIAL)) {
				spectate(gamer);
				return false;
			}

			player.sendMessage("§cO evento já iniciou!");
			return false;
		} else {
			if (!isFull() || isFull() && Member.hasGroupPermission(gamer.getUuid(), Group.LIGHT)) {
				player.sendMessage("§aVocê entrou no evento Rei da Mesa!");
				playerList.add(gamer.getPlayer());

				player.getInventory().clear();
				player.teleport(BukkitMain.getInstance().getLocationFromConfig("rdm"));

				broadcast("§b" + gamer.getPlayer().getName() + " §eentrou no evento! §a(" + playerList.size() + "/"
						+ maxPlayers + ")");
				return true;
			} else {
				player.sendMessage("§cO evento está cheio!");
				return false;
			}
		}
	}

	@Override
	public boolean spectate(Gamer gamer) {
		spectateList.add(gamer.getPlayer());
		return false;
	}

	@Override
	public void leave(Player player) {
		if (playerList.contains(player))
			playerList.remove(player);

		if (spectateList.contains(player.getPlayer()))
			spectateList.remove(player);

		if (!hasStarted()) {
			broadcast("§b" + player.getName() + " §esaiu no evento! §a(" + playerList.size() + "/" + maxPlayers + ")");
		}

		GameMain.getInstance().getWarpManager().setWarp(player, "spawn", true);
	}

	@Override
	public boolean isFull() {
		return playerList.size() >= maxPlayers;
	}

	@Override
	public boolean hasStarted() {
		return minigameState == MinigameState.GAMETIME;
	}

}
