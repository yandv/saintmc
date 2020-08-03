package tk.yallandev.saintmc.kitpvp.warp.types;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.firework.FireworkAPI;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeStatus;
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeType;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.lava.PlayerFinishChallengeEvent;
import tk.yallandev.saintmc.kitpvp.event.lava.PlayerStartChallengeEvent;
import tk.yallandev.saintmc.kitpvp.event.lava.PlayerStopChallengeEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeInfo;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeStage;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.types.LavaScoreboard;

public class LavaWarp extends Warp {

	private Map<UUID, Entry<ChallengeStage, ChallengeInfo>> challengeMap;

	public LavaWarp() {
		super("Lava", BukkitMain.getInstance().getLocationFromConfig("lava"), new LavaScoreboard());
		getWarpSettings().setSpawnEnabled(false);
		getScoreboard().setWarp(this);

		challengeMap = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (inWarp(player)) {
				if (event.getCause() == DamageCause.LAVA) {
					Entry<ChallengeStage, ChallengeInfo> entry = challengeMap.computeIfAbsent(player.getUniqueId(),
							v -> new AbstractMap.SimpleEntry<ChallengeStage, ChallengeInfo>(
									getNearestChallenge(player.getLocation()), new ChallengeInfo()));

					if (!entry.getValue().isRunning()) {
						player.sendMessage("§aVocê iniciou o desafio de lava "
								+ NameUtils.formatString(entry.getKey().getName()) + "!");
						entry.getValue().start();
						Bukkit.getPluginManager().callEvent(new PlayerStartChallengeEvent(player, entry.getKey()));
					}

					entry.getValue().setLastDamage(System.currentTimeMillis());
					event.setCancelled(false);
					getScoreboard().updateScore(player, entry.getValue());
				} else {
					if (event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.FIRE) {
						if (challengeMap.containsKey(player.getUniqueId())) {
							Entry<ChallengeStage, ChallengeInfo> entry = challengeMap.get(player.getUniqueId());

							if (entry.getValue().isRunning() && !entry.getValue().isFinished()) {
								if (System.currentTimeMillis() - entry.getValue().getLastDamage() > 3000l) {
									challengeMap.remove(player.getUniqueId());

									if (entry.getKey() == ChallengeStage.TRAINAING)
										Bukkit.getPluginManager().callEvent(
												new PlayerStopChallengeEvent(player, entry.getKey(), entry.getValue()));
									else {
										if (BukkitMain.getInstance()
												.getLocationFromConfig(entry.getKey().getEndConfig())
												.distanceSquared(player.getLocation()) < 5)
											Bukkit.getPluginManager().callEvent(new PlayerFinishChallengeEvent(player,
													entry.getKey(), entry.getValue()));
										else
											Bukkit.getPluginManager().callEvent(new PlayerStopChallengeEvent(player,
													entry.getKey(), entry.getValue()));
									}

									entry.getValue().setFinished(true);
								}
							}
						}
					}

					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onChallengeFinish(PlayerStopChallengeEvent event) {
		Player player = event.getPlayer();

		if (event instanceof PlayerFinishChallengeEvent) {
			ChallengeStatus challengeStatus = CommonGeneral.getInstance().getStatusManager()
					.loadStatus(player.getUniqueId(), StatusType.LAVA, ChallengeStatus.class);
			ChallengeType challengeType = ChallengeType.valueOf(event.getChallengeType().name());

			challengeStatus.addWin(ChallengeType.valueOf(event.getChallengeType().name()));

			int time = (int) ((event.getChallengeInfo().getLastDamage() - event.getChallengeInfo().getStartTime())
					/ 1000);

			player.sendMessage("§aVocê passou o lava challenge "
					+ NameUtils.formatString(event.getChallengeType().getName()) + " em "
					+ formatTime(event.getChallengeInfo().getLastDamage(), event.getChallengeInfo().getStartTime())
					+ "!");

			if (challengeStatus.getTime(challengeType) == 0) {
				player.sendMessage("§aVocê estabeleceu o record de " + StringUtils.formatTime(time) + " nesse modo!");
				challengeStatus.setTime(challengeType, time);
			} else if (time < challengeStatus.getTime(challengeType)) {
				player.sendMessage("§aVocê ultrapassou o seu record nesse modo!");
				challengeStatus.setTime(challengeType, time);
			} else
				player.sendMessage("§aO seu record neste modo é §7"
						+ StringUtils.formatTime(challengeStatus.getTime(challengeType)) + "§a!");

			handleInventory(player);
			player.teleport(getSpawnLocation());

			if (event.getChallengeType().ordinal() >= ChallengeStage.HARD.ordinal()) {
				FireworkAPI.spawn(player.getLocation().add(0, 0, 1), Color.AQUA, true);
				FireworkAPI.spawn(player.getLocation().add(1, 0, 0), Color.AQUA, true);
				FireworkAPI.spawn(player.getLocation().add(-1, 0, 0), Color.AQUA, true);
				FireworkAPI.spawn(player.getLocation().add(0, 0, -1), Color.AQUA, true);
			}

			if (event.getChallengeType() == ChallengeStage.HARDCORE) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage("§9Lava Challenge> §fO jogador §a" + player.getName()
							+ "§f passou o desafio de lava §4Extreme§f!");
					p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
				}
			}
		} else {
			if (event.isDeath()) {
				player.sendMessage(
						"§cVocê morreu no " + NameUtils.formatString(event.getChallengeType().getName()) + "!");
				player.sendMessage("§cVocê ficou vivo por "
						+ formatTime(event.getChallengeInfo().getLastDamage(), event.getChallengeInfo().getStartTime())
						+ "!");

				CommonGeneral.getInstance().getStatusManager()
						.loadStatus(player.getUniqueId(), StatusType.LAVA, ChallengeStatus.class)
						.addAttemp(ChallengeType.valueOf(event.getChallengeType().name()));
			} else {
				player.sendMessage("§cVocê saiu do desafio de lava "
						+ NameUtils.formatString(event.getChallengeType().getName()) + "!");
				player.sendMessage("§cVocê ficou vivo por "
						+ formatTime(event.getChallengeInfo().getLastDamage(), event.getChallengeInfo().getStartTime())
						+ "!");
			}
		}
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		if (event.getWarp() == this)
			handleInventory(event.getPlayer());
	}

	@EventHandler
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		if (event.getWarp() == this) {
			Player player = event.getPlayer();

			if (challengeMap.containsKey(player.getUniqueId())) {
				Entry<ChallengeStage, ChallengeInfo> entry = challengeMap.get(player.getUniqueId());

				if (entry.getValue().isRunning()) {
					challengeMap.remove(player.getUniqueId());
					Bukkit.getPluginManager()
							.callEvent(new PlayerStopChallengeEvent(player, entry.getKey(), entry.getValue()).death());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
		if (event.getWarp() == this)
			handleInventory(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWarpQuit(PlayerWarpQuitEvent event) {
		if (inWarp(event.getPlayer()))
			if (challengeMap.containsKey(event.getPlayer().getUniqueId()))
				challengeMap.remove(event.getPlayer().getUniqueId());
	}

	private void handleInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		player.setLevel(0);
		player.setFoodLevel(20);
		player.setHealth(20D);

		for (int x = 0; x < player.getInventory().getSize(); x++)
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));

		player.getInventory().setItem(13, new ItemStack(Material.RED_MUSHROOM, 64));
		player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 64));
		player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));
		player.updateInventory();
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder().name("§aLava Challenge")
				.lore("\n§7Treine seu refil e recraft complentando\nnos desafios de lava propostos.\n\n§a"
						+ GameMain.getInstance().getGamerManager().filter(gamer -> gamer.getWarp() == this).size()
						+ " jogadores")
				.type(Material.LAVA_BUCKET).build();
	}

	public String formatTime(long startTime, long time) {
		return StringUtils.formatTime((int) ((startTime - time) / 1000));
	}

	public ChallengeStage getNearestChallenge(Location location) {
		List<Entry<ChallengeStage, Location>> locationList = Arrays.asList(
				new AbstractMap.SimpleEntry<ChallengeStage, Location>(ChallengeStage.EASY,
						BukkitMain.getInstance().getLocationFromConfig(ChallengeStage.EASY.getStartConfig())),
				new AbstractMap.SimpleEntry<ChallengeStage, Location>(ChallengeStage.MEDIUM,
						BukkitMain.getInstance().getLocationFromConfig(ChallengeStage.MEDIUM.getStartConfig())),
				new AbstractMap.SimpleEntry<ChallengeStage, Location>(ChallengeStage.HARD,
						BukkitMain.getInstance().getLocationFromConfig(ChallengeStage.HARD.getStartConfig())),
				new AbstractMap.SimpleEntry<ChallengeStage, Location>(ChallengeStage.HARDCORE,
						BukkitMain.getInstance().getLocationFromConfig(ChallengeStage.HARDCORE.getStartConfig())),
				new AbstractMap.SimpleEntry<ChallengeStage, Location>(ChallengeStage.TRAINAING,
						BukkitMain.getInstance().getLocationFromConfig(ChallengeStage.TRAINAING.getStartConfig())));

		return locationList.stream().sorted((o1, o2) -> Double.compare(o1.getValue().distanceSquared(location),
				o2.getValue().distanceSquared(location))).findFirst().orElse(null).getKey();
	}

}
