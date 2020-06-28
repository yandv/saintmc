package br.com.saintmc.hungergames.listener.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.player.PlayerDeathDropItemEvent;
import br.com.saintmc.hungergames.event.player.PlayerItemReceiveEvent;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.utils.item.ItemUtils;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class DeathListener extends GameListener {

	private Map<String, String> translateMap = new HashMap<>();

	/**
	 * 
	 * The player only can kill other and count match kill a unique time
	 * 
	 */

	private Map<UUID, List<UUID>> killMap;

	public DeathListener() {
		killMap = new HashMap<>();

		translateMap.put("death-message-null", "§e%player% [%player_Kit%] morreu de forma desconhecida §4[%players%]");
		translateMap.put("death-message-entityattackplayer",
				"§e%killed_By%[%killed_By_Kit%] matou %player%[%player_Kit%] §4[%players%]");
		translateMap.put("death-message-entityattackentity",
				"§e%player% [%player_Kit%] levou uma rasteira de um %killed_By% §4[%players%]");
		translateMap.put("death-message-border",
				"§e%player% [%player_Kit%] morreu para a bordar do mundo §4[%players%]");
		translateMap.put("death-message-leave", "§e%player% [%player_Kit%] desistiu da partida §4[%players%]");
		translateMap.put("death-message-kills", "§e%player% [%player_Kit%] morreu §4[%players%]");
		translateMap.put("death-message-lava", "§e%player% [%player_Kit%] morreu na lava §4[%players%]");
		translateMap.put("death-message-took-too-long",
				"§e%player% [%player_Kit%] demorou muito para relogar e foi desclassificado §4[%players%]");
		translateMap.put("death-message-fall",
				"§e%player% [%player_Kit%] esqueceu de abrir os paraquedas §4[%players%]");
		translateMap.put("death-message-entityexplosion",
				"§e%player% [%player_Kit%] morreu explodido por um mob §4[%players%]");
		translateMap.put("death-message-suffocation", "§e%player% [%player_Kit%] morreu sufocado §4[%players%]");
		translateMap.put("death-message-fire", "§e%player% [%player_Kit%] morreu pegando fogo §4[%players%]");
		translateMap.put("death-message-firetick", "§e%player% [%player_Kit%] morreu pegando fogo §4[%players%]");
		translateMap.put("death-message-melting",
				"§e%player% [%player_Kit%] morreu de forma desconhecidas §4[%players%]");
		translateMap.put("death-message-blockexplosion", "§e%player% [%player_Kit%] morreu explodido §4[%players%]");
		translateMap.put("death-message-lightning",
				"§e%player% [%player_Kit%] morreu por raios que cairam do ceu §4[%players%]");
		translateMap.put("death-message-suicide", "§e%player% [%player_Kit%] se matou §4[%players%]");
		translateMap.put("death-message-starvation", "§e%player% [%player_Kit%] morreu de fome §4[%players%]");
		translateMap.put("death-message-poison", "§e%player% [%player_Kit%] morreu envenenado §4[%players%]");
		translateMap.put("death-message-magic", "§e%player% [%player_Kit%] morreu por magia §4[%players%]");
		translateMap.put("death-message-wither", "§e%player% [%player_Kit%] secou até a morte §4[%players%]");
		translateMap.put("death-message-fallingblock",
				"§e%player% [%player_Kit%] foi esmagado por um bloco §4[%players%]");
		translateMap.put("death-message-thorns", "§e%player% [%player_Kit%] foi espetado até a morte §4[%players%]");
		translateMap.put("death-message-projectileentity",
				"§e%player% [%player_Kit%] morreu flechado por um esqueleto §4[%players%]");
		translateMap.put("death-message-projectileplayer",
				"§e%killed_By% [%killed_By_Kit%] com seu arco levou %player% para conhecer jesus §4[%players%]");
		translateMap.put("death-message-contact", "§e%player% [%player_Kit%] morreu §4[%players%]");
	}

	public Location spawnLocation(World world) {
		Location loc = null;

		if (BukkitMain.getInstance().getLocation().containsKey("respawn"))
			loc = BukkitMain.getInstance().getLocation().get("respawn");
		else {
			int x = 80 + CommonConst.RANDOM.nextInt(400);
			int z = 80 + CommonConst.RANDOM.nextInt(400);

			if (CommonConst.RANDOM.nextBoolean())
				x = -x;

			if (CommonConst.RANDOM.nextBoolean())
				z = -z;

			int y = world.getHighestBlockYAt(x, z);

			loc = new Location(world, x, y, z);
			if (!loc.getChunk().isLoaded()) {
				loc.getChunk().load();
			}
		}

		return loc;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.getPlayer().teleport(spawnLocation(event.getPlayer().getWorld()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		/*
		 * Variables
		 */

		Player player = event.getEntity();
		Gamer gamer = getGameGeneral().getGamerController().getGamer(player.getUniqueId());

		event.setDeathMessage(null);

		List<ItemStack> list = new ArrayList<>(event.getDrops()).stream().filter(item -> !gamer.isAbilityItem(item))
				.collect(Collectors.toList());
		event.getDrops().clear();

		player.closeInventory();
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		/*
		 * Check if player is Gamemaker or Spectator
		 */

		if (gamer.isGamemaker() || gamer.isSpectator())
			return;

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		/*
		 * Reset and respawn player manually
		 */

		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.setLevel(0);
		player.setExp(0);
		player.setVelocity(new Vector(0, 0, 0));

		PlayerDeathDropItemEvent playerDeathDropItemEvent = new PlayerDeathDropItemEvent(player,
				player.getLocation() == null ? player.getKiller().getLocation() : player.getLocation());

		Bukkit.getPluginManager().callEvent(playerDeathDropItemEvent);

		if (!playerDeathDropItemEvent.isCancelled()) {
			ItemUtils.dropItems(list, playerDeathDropItemEvent.getLocation());
		}

		/*
		 * Check if have a killer and add xp to his
		 */

		if (player.getKiller() != null) {
			Member battleKiller = CommonGeneral.getInstance().getMemberManager()
					.getMember(player.getKiller().getUniqueId());

			if (battleKiller != null) {

				int winnerXp = CommonConst.RANDOM.nextInt(5) + 12;
				int winnerMoney = 50 * (battleKiller.isGroup(Group.VIP) ? 2 : 1);
				battleKiller.addXp(winnerXp);
				battleKiller.addMoney(winnerMoney);

				player.getKiller().spigot()
						.sendMessage(
								new MessageBuilder("§aVocê matou " + player.getName() + "!")
										.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												TextComponent.fromLegacyText(
														"§a+" + winnerXp + "\n" + "§6+" + winnerMoney + " coins!")))
										.create());

				Gamer killerGamer = getGameGeneral().getGamerController().getGamer(battleKiller.getUniqueId());

				if (member.hasGroupPermission(ServerConfig.getInstance().getRespawnGroup())) {
					if (!killMap.computeIfAbsent(battleKiller.getUniqueId(), v -> new ArrayList<>())
							.contains(player.getUniqueId())) {
						killMap.get(battleKiller.getUniqueId()).add(player.getUniqueId());
						killerGamer.addKill();
					}
				} else
					killerGamer.addKill();
			}
		}

		/*
		 * Respawn player if have ServerConfig.getInstance().getRespawnGroup() group
		 * permission and the game time is less than 300
		 */

		if (ServerConfig.getInstance().isRespawnEnabled()
				&& member.hasGroupPermission(ServerConfig.getInstance().getRespawnGroup())
				&& GameGeneral.getInstance().getTime() <= 300) {
			/*
			 * Fire PlayerItemReceiveEvent
			 */

			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
				}
			}.runTaskLater(GameMain.getInstance(), 10l);

			/*
			 * Teleport player to a random location
			 */

			player.teleport(spawnLocation(player.getWorld()));
		} else {

			/*
			 * Get
			 */

			DamageCause cause = null;
			Player killer = null;

			if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != null)
				cause = player.getLastDamageCause().getCause();

			String causeString = cause.toString().toLowerCase();
			HashMap<String, String> replaces = new HashMap<>();

			switch (cause) {
			case PROJECTILE:
				if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) player.getLastDamageCause();

					if (e.getDamager() instanceof Projectile) {
						Projectile projectile = (Projectile) e.getDamager();
						ProjectileSource shooter = projectile.getShooter();

						if (shooter instanceof Player) {
							killer = (Player) shooter;
							causeString = "projectile_player";
						} else if (shooter instanceof Entity) {
							causeString = "projectile_entity";
							replaces.put("%killed_By%",
									((Entity) shooter).getType().toString().replace("_", "").toLowerCase());
						} else {
							causeString = null;
						}

					} else {
						causeString = null;
					}
				} else {
					causeString = null;
				}
				break;
			case ENTITY_ATTACK:
				if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) player.getLastDamageCause();

					if (e.getDamager() instanceof Player) {
						causeString = "entity_attack_player";
						killer = (Player) e.getDamager();
					} else {
						causeString = "entity_attack_entity";
						replaces.put("%killed_By%", e.getDamager().getType().toString().replace("_", "").toLowerCase());
					}
				} else {
					causeString = null;
				}
				break;
			case CUSTOM:
				causeString = "border";
			default:
				break;
			}

			String deathMessageId = "death-message-" + (causeString != null ? causeString.replace("_", "") : "null");

			replaces.put("%player%", player.getName());
			replaces.put("%player_Kit%", GameMain.getInstance().isDoubleKit()
					? gamer.hasKit(KitType.PRIMARY) && gamer.hasKit(KitType.SECONDARY)
							? NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)) + "/"
									+ NameUtils.formatString(gamer.getKitName(KitType.SECONDARY))
							: gamer.hasKit(KitType.PRIMARY) ? NameUtils.formatString(gamer.getKitName(KitType.PRIMARY))
									: NameUtils.formatString(gamer.getKitName(KitType.SECONDARY))
					: NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)));
			gamer.getStatus().addDeath();

			if (killer != null) {
				Gamer killerGamer = getGameGeneral().getGamerController().getGamer(killer.getUniqueId());
				replaces.put("%killed_By%", killer.getName());

				replaces.put("%killed_By_Kit%",
							 GameMain.getInstance().isDoubleKit()
								? killerGamer.hasKit(KitType.PRIMARY) && killerGamer.hasKit(KitType.SECONDARY)
										? NameUtils.formatString(killerGamer.getKitName(KitType.PRIMARY)) + "/"
												+ NameUtils.formatString(killerGamer.getKitName(KitType.SECONDARY))
										: killerGamer.hasKit(KitType.PRIMARY)
												? NameUtils.formatString(killerGamer.getKitName(KitType.PRIMARY))
												: NameUtils.formatString(killerGamer.getKitName(KitType.SECONDARY))
								: NameUtils.formatString(killerGamer.getKitName(KitType.PRIMARY)));

				replaces.put("%item_Killed%", getItemName(killer.getItemInHand()));
			}

			String kickMessage = deathMessage(player, deathMessageId, replaces);

			if (member.hasGroupPermission(Group.TRIAL)) {
				AdminMode.getInstance().setAdmin(player, Member.getMember(player.getUniqueId()));
				gamer.setGamemaker(true);
			} else {
				gamer.setSpectator(true);
			}

			if (!member.hasGroupPermission(Group.TRIAL))
				if (!member.hasGroupPermission(ServerConfig.getInstance().getSpectatorGroup()) && !gamer.isWinner()) {
					int number = GameGeneral.getInstance().getPlayersInGame() + 1;

					if (number <= 10)
						player.sendMessage("§cVocê ficou entre os ultimos §c10 jogadores§f!");

					player.sendMessage("§cVocê morreu: " + kickMessage.replace("%players%",
							(GameGeneral.getInstance().getPlayersInGame() - 1) + ""));
					BukkitMain.getInstance().sendPlayerToLobby(player);
				}
		}
	}

	public String deathMessage(Player player, String messageId, HashMap<String, String> replaces) {
		String messageReturn = "";

		String message = t(messageId);

		for (Entry<String, String> entry : replaces.entrySet()) {
			message = message.replace(entry.getKey(), entry.getValue());
		}

		message = message.replace("%players%", (GameGeneral.getInstance().getPlayersInGame() - 1) + "");

		for (Player p : Bukkit.getOnlinePlayers()) {

			p.sendMessage(message);

			if (player.getUniqueId() == p.getUniqueId()) {
				messageReturn = message;
			}
		}

		System.out.println(message);

		return messageReturn;
	}

	private String t(String messageId) {
		if (translateMap.containsKey(messageId)) {
			return translateMap.get(messageId);
		}

		System.out.println("Missing id " + messageId);
		return translateMap.get("death-message-null").replace("&", "§");
	}

	public static String getItemName(ItemStack item) {
		if (item == null)
			item = new ItemStack(Material.AIR);
		return NameUtils.getName(item.getType().name());
	}

}
