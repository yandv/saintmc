package tk.yallandev.saintmc.game.games.hungergames.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomKit;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.stage.GameStage;

public class DeathListener extends tk.yallandev.saintmc.game.listener.GameListener {

	public static Set<UUID> relogProcess = new HashSet<>();
	private static Set<UUID> playerRelogged = new HashSet<>();
	private static HashMap<UUID, String> deathMessages = new HashMap<>();
	private static Map<String, String> translateMap = new HashMap<>();

	static {
		translateMap.put("death-message-null",
				"\u0026e%player% [%player_Kit%] morreu de forma desconhecida \u00264[%players%]");
		translateMap.put("death-message-entityattackplayer",
				"\u0026e%killed_By% [%killed_By_Kit%] com sua %item_Killed% levou %player% para conhecer jesus \u00264[%players%]");
		translateMap.put("death-message-entityattackentity",
				"\u0026e%player% [%player_Kit%] levou uma rasteira de um %killed_By% \u00264[%players%]");
		translateMap.put("death-message-border",
				"\u0026e%player% [%player_Kit%] morreu para a bordar do mundo \u00264[%players%]");
		translateMap.put("death-message-leave",
				"\u0026e%player% [%player_Kit%] desistiu da partida \u00264[%players%]");
		translateMap.put("death-message-kills", "\u0026e%player% [%player_Kit%] morreu \u00264[%players%]");
		translateMap.put("death-message-lava", "\u0026e%player% [%player_Kit%] morreu na lava \u00264[%players%]");
		translateMap.put("death-message-took-too-long",
				"\u0026e%player% [%player_Kit%] demorou muito para relogar e foi desclassificado \u00264[%players%]");
		translateMap.put("death-message-fall",
				"\u0026e%player% [%player_Kit%] esqueceu de abrir os paraquedas \u00264[%players%]");
		translateMap.put("death-message-entityexplosion",
				"\u0026e%player% [%player_Kit%] morreu explodido por um mob \u00264[%players%]");
		translateMap.put("death-message-suffocation",
				"\u0026e%player% [%player_Kit%] morreu sufocado \u00264[%players%]");
		translateMap.put("death-message-fire", "\u0026e%player% [%player_Kit%] morreu pegando fogo \u00264[%players%]");
		translateMap.put("death-message-firetick",
				"\u0026e%player% [%player_Kit%] morreu pegando fogo \u00264[%players%]");
		translateMap.put("death-message-melting",
				"\u0026e%player% [%player_Kit%] morreu de forma desconhecidas \u00264[%players%]");
		translateMap.put("death-message-blockexplosion",
				"\u0026e%player% [%player_Kit%] morreu explodido \u00264[%players%]");
		translateMap.put("death-message-lightning",
				"\u0026e%player% [%player_Kit%] morreu por raios que cairam do ceu \u00264[%players%]");
		translateMap.put("death-message-suicide", "\u0026e%player% [%player_Kit%] se matou \u00264[%players%]");
		translateMap.put("death-message-starvation",
				"\u0026e%player% [%player_Kit%] morreu de fome \u00264[%players%]");
		translateMap.put("death-message-poison", "\u0026e%player% [%player_Kit%] morreu envenenado \u00264[%players%]");
		translateMap.put("death-message-magic", "\u0026e%player% [%player_Kit%] morreu por magia \u00264[%players%]");
		translateMap.put("death-message-wither", "\u0026e%player% [%player_Kit%] secou até a morte \u00264[%players]");
		translateMap.put("death-message-fallingblock",
				"\u0026e%player% [%player_Kit%] foi esmagado por um bloco \u00264[%players%]");
		translateMap.put("death-message-thorns",
				"\u0026e%player% [%player_Kit%] foi espetado até a morte \u00264[%players%]");
		translateMap.put("death-message-projectileentity",
				"\u0026e%player% [%player_Kit%] morreu flechado por um esqueleto \u00264[%players%]");
		translateMap.put("death-message-projectileplayer",
				"\u0026e%killed_By% [%killed_By_Kit%] com seu arco levou %player% para conhecer jesus \u00264[%players%]");
		translateMap.put("death-message-contact", "\u0026e%player% [%player_Kit%] morreu \u00264[%players");
	}

	private HungerGamesMode hg;

	public DeathListener(GameMain main, HungerGamesMode hg) {
		super(main);
		this.hg = hg;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (!GameStage.isPregame(getGameMain().getGameStage())) {
			Gamer gamer = GameMain.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (!gamer.isSpectator() && !gamer.isGamemaker()) {
				if (relogProcess.contains(p.getUniqueId()))
					event.allow();
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();

		if (relogProcess.contains(p.getUniqueId())) {
			playerRelogged.add(p.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					relogProcess.remove(p.getUniqueId());
				}
			}.runTaskLater(getGameMain(), 60 * 20);
			event.setJoinMessage("§e" + p.getName() + " entrou no torneio");
			return;
		}

		event.setJoinMessage(null);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		Gamer gamer = getGameMain().getGamerManager().getGamer(p.getUniqueId());
		String originalDeathMessage = event.getDeathMessage();
		event.setDeathMessage(null);
		event.getDrops().clear();

		if (gamer.isGamemaker() || gamer.isSpectator())
			return;

		deathPlayer(p);
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (player.hasGroupPermission(Group.LIGHT)) {
			if (GameMain.getPlugin().getTimer() <= 300) {
				p.setHealth(p.getMaxHealth());
				p.setFoodLevel(20);
				p.setSaturation(5);
				p.setFireTicks(0);
				Random r = new Random();
				int x = 100 + r.nextInt(400);
				int z = 100 + r.nextInt(400);

				if (r.nextBoolean())
					x = -x;

				if (r.nextBoolean())
					z = -z;

				World world = p.getWorld();
				int y = world.getHighestBlockYAt(x, z);
				Location loc = new Location(world, x, y, z);

				if (!loc.getChunk().isLoaded()) {
					loc.getChunk().load();
				}

				p.teleport(loc.clone().add(0, 0.5, 0));

				new BukkitRunnable() {
					@Override
					public void run() {
						p.setFireTicks(0);
						p.getInventory().addItem(new ItemStack(Material.COMPASS));

						if (player.hasGroupPermission(Group.BLIZZARD))
							for (Kit playerKit : getGameMain().getKitManager().getPlayerKit(p)) {
								for (Ability ability : playerKit.getAbilities()) {
									ability.giveItems(p);
								}
							}
					}
				}.runTaskLater(getGameMain(), 1);

				if (p.getKiller() != null) {
					getGameMain().getGamerManager().getGamer(p.getKiller().getUniqueId()).addKill();
					Member battleKiller = CommonGeneral.getInstance().getMemberManager()
							.getMember(p.getKiller().getUniqueId());

					if (battleKiller != null) {
						int amount = new Random().nextInt(5) + 1;
						battleKiller.addXp(amount);
						p.getKiller()
								.sendMessage("§a§l> §fVocê ganhou §a%amount% xp§f!".replace("%amount%", "" + amount));
					}
				}

				return;
			}
		}

		DamageCause cause = null;
		Player killer = null;

		if (p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null)
			cause = p.getLastDamageCause().getCause();

		String causeString = cause.toString().toLowerCase();
		HashMap<String, String> replaces = new HashMap<>();

		switch (cause) {
		case PROJECTILE:
			if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) p.getLastDamageCause();

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
			if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) p.getLastDamageCause();

				if (e.getDamager() instanceof Player) {
					causeString = "entity_attack_player";
					killer = p.getKiller();
				} else {
					causeString = "entity_attack_entity";
					replaces.put("%killed_By%", e.getDamager().getType().toString().replace("_", "").toLowerCase());
				}
			} else {
				causeString = null;
			}
			break;
		case CUSTOM:
			if (event.getDeathMessage() != null)
				if (originalDeathMessage.contains("desistiu")) {
					causeString = "leave";
					break;
				}
			causeString = "border";
		default:
			break;
		}

		String deathMessageId = "death-message-" + (causeString != null ? causeString.replace("_", "") : "null");

		replaces.put("%player%", p.getName());
		replaces.put("%player_Kit%",
				(gamer.getKit() != null ? ((gamer.getKit() instanceof CustomKit) ? ChatColor.DARK_GRAY : "") : "")
						+ NameUtils.formatString(gamer.getKitName())); // TODO
		// KITS
		gamer.addDeath();

		if (killer != null) {
			Gamer killerGamer = getGameMain().getGamerManager().getGamer(killer.getUniqueId());
			killerGamer.addKill();
			replaces.put("%killed_By%", killer.getName());
			replaces.put("%killed_By_Kit%",
					(killerGamer.getKit() != null
							? ((killerGamer.getKit() instanceof CustomKit) ? ChatColor.DARK_GRAY : "")
							: "") + NameUtils.formatString(killerGamer.getKitName())); // TODO
			// KITS
//			replaces.put("%item_Killed%", NameUtils.getItemName(killer.getItemInHand()));

			Member battleKiller = CommonGeneral.getInstance().getMemberManager().getMember(killerGamer.getUniqueId());

			if (battleKiller != null) {
				int amount = new Random().nextInt(7) + 1;
				battleKiller.addXp(amount);
				p.getKiller().sendMessage("§a§l> §fVocê ganhou §a%amount% xp§f!".replace("%amount%", "" + amount));
			}
		}

		String kickMessage = deathMessage(p, deathMessageId, replaces);

		if (player.hasGroupPermission(Group.TRIAL)) {
			AdminMode.getInstance().setAdmin(p, Member.getMember(p.getUniqueId()));
			gamer.setGamemaker(true);
		} else {
			gamer.setSpectator(true);
		}

		hg.checkWinner();

		if (!(player.hasGroupPermission(Group.SAINT) && player.hasPermission("tag.winner") && GameListener.SPECTATOR)
				&& !player.hasGroupPermission(Group.TRIAL)) {
			int number = GameMain.getPlugin().playersLeft() + 1;

			if (number <= 10) {
				p.sendMessage("§cVocê ficou entre os ultimos §c10 jogadores§f!");
				p.sendMessage("§cVocê morreu: " + kickMessage);
				GameMain.getPlugin().sendPlayerToLobby(p);
				return;
			}

			p.sendMessage("§cVocê morreu: " + kickMessage);
			GameMain.getPlugin().sendPlayerToLobby(p);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		final Player p = event.getPlayer();

		if (p.isDead()) {
			event.setQuitMessage(null);
			return;
		}

		// TODO left the game
		final Gamer gamer = getGameMain().getGamerManager().getGamer(p.getUniqueId());

		if (gamer.isGamemaker() || gamer.isSpectator() || AdminMode.getInstance().isAdmin(p)) {
			event.setQuitMessage(null);
			return;
		}

		event.setQuitMessage("§e" + p.getName() + " saiu do torneio.");

		if (!relogProcess.contains(p.getUniqueId())) {
			relogProcess.add(p.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!playerRelogged.contains(p.getUniqueId())) {
						HashMap<String, String> replaces = new HashMap<>();
						replaces.put("%player%", p.getName());
						replaces.put("%player_Kit%",
								(gamer.getKit() != null
										? ((gamer.getKit() instanceof CustomKit) ? ChatColor.DARK_GRAY : "")
										: "") + NameUtils.formatString(gamer.getKitName()));// TODO
						gamer.addDeath();
						deathMessage(p, "death-message-took-too-long", replaces);
						deathPlayer(p);

						relogProcess.remove(p.getUniqueId());
						hg.checkWinner();

						Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

						if (player == null) {
							gamer.setSpectator(true);
							return;
						}

						if (Member.hasGroupPermission(p.getUniqueId(), Group.TRIAL)) {
							AdminMode.getInstance().setAdmin(p, player);
							gamer.setGamemaker(true);
//							player.removePermission("tag.winner");
						} else {
							gamer.setSpectator(true);
						}

					} else {
						playerRelogged.remove(p.getUniqueId());
					}
				}
			}.runTaskLater(getGameMain(), 60 * 20);
			return;
		}

		if (getGameMain().getGameStage() == GameStage.GAMETIME) {
			HashMap<String, String> replaces = new HashMap<>();
			replaces.put("%player%", p.getName());
			replaces.put("%player_Kit%",
					(gamer.getKit() != null ? ((gamer.getKit() instanceof CustomKit) ? ChatColor.DARK_GRAY : "") : "")
							+ NameUtils.formatString(gamer.getKitName()));
			deathMessage(p, "death-message-leave", replaces);
			deathPlayer(p);
			gamer.addDeath();
			gamer.setGamemaker(gamemaker);

			if (Member.hasGroupPermission(p.getUniqueId(), Group.TRIAL)) {
				AdminMode.getInstance().setAdmin(p, Member.getMember(p.getUniqueId()));
				gamer.setGamemaker(true);
			} else {
				gamer.setSpectator(true);
			}

			hg.checkWinner();
		}
	}

	public static String deathMessage(Player player, String messageId, HashMap<String, String> replaces) {
		String messageReturn = "";

		for (Player p : Bukkit.getOnlinePlayers()) {
			String message = t(messageId);

			for (Entry<String, String> entry : replaces.entrySet()) {
				message = message.replace(entry.getKey(), entry.getValue());
			}

			p.sendMessage(
					message.replace("%players%", (GameMain.getPlugin().playersLeft() + relogProcess.size() - 1) + ""));
			if (player.getUniqueId() == p.getUniqueId()) {
				messageReturn = message;
			}
		}

		deathMessages.put(player.getUniqueId(), messageReturn);
		return messageReturn;
	}

	private static String t(String messageId) {
		if (translateMap.containsKey(messageId)) {
			return translateMap.get(messageId);
		}

		System.out.println("Missing id " + messageId);
		return translateMap.get("death-message-null");
	}

	public static void deathPlayer(Player p) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		PlayerInventory inv = p.getInventory();

		for (ItemStack item : inv.getContents())
			if (checkNotNull(item))
				items.add(item.clone());

		for (ItemStack item : inv.getArmorContents())
			if (checkNotNull(item))
				items.add(item.clone());

		if (checkNotNull(p.getItemOnCursor()))
			items.add(p.getItemOnCursor().clone());

		tk.yallandev.saintmc.game.util.ItemUtils.dropAndClear(p, items, p.getLocation());
	}

	private static boolean checkNotNull(ItemStack item) {
		return item != null && item.getType() != Material.AIR;
	}

	public static boolean containsDeathMessage(UUID uuid) {
		return deathMessages.containsKey(uuid);
	}

	public static String getDeathMessage(UUID uuid) {
		return deathMessages.get(uuid);
	}
}
