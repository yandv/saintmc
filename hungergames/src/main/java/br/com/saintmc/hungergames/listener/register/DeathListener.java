package br.com.saintmc.hungergames.listener.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.player.PlayerItemReceiveEvent;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.utils.item.ItemUtils;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class DeathListener extends GameListener {

	private static Map<String, String> translateMap = new HashMap<>();

	static {
		translateMap.put("death-message-null", "§e%player% [%player_Kit%] morreu de forma desconhecida §4[%players%]");
		translateMap.put("death-message-entityattackplayer",
				"§e%killed_By% [%killed_By_Kit%] com sua %item_Killed% levou %player% para conhecer jesus §4[%players%]");
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
		translateMap.put("death-message-wither", "§e%player% [%player_Kit%] secou até a morte §4[%players]");
		translateMap.put("death-message-fallingblock",
				"§e%player% [%player_Kit%] foi esmagado por um bloco §4[%players%]");
		translateMap.put("death-message-thorns", "§e%player% [%player_Kit%] foi espetado até a morte §4[%players%]");
		translateMap.put("death-message-projectileentity",
				"§e%player% [%player_Kit%] morreu flechado por um esqueleto §4[%players%]");
		translateMap.put("death-message-projectileplayer",
				"§e%killed_By% [%killed_By_Kit%] com seu arco levou %player% para conhecer jesus §4[%players%]");
		translateMap.put("death-message-contact", "§e%player% [%player_Kit%] morreu §4[%players");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player entity = event.getEntity();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(entity);

		List<ItemStack> list = event.getDrops().stream().filter(item -> !gamer.isAbilityItem(item))
				.collect(Collectors.toList());

		ItemUtils.dropItems(list, entity.getKiller() == null ? entity.getLocation() : entity.getKiller().getLocation());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {

		/*
		 * Variables
		 */

		Player player = event.getEntity();
		Gamer gamer = getGameGeneral().getGamerController().getGamer(player.getUniqueId());

		event.setDeathMessage(null);
		event.getDrops().clear();

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
		
		/*
		 * Teleport player to a random location
		 */

		int x = 100 + CommonConst.RANDOM.nextInt(400);
		int z = 100 + CommonConst.RANDOM.nextInt(400);

		if (CommonConst.RANDOM.nextBoolean())
			x = -x;

		if (CommonConst.RANDOM.nextBoolean())
			z = -z;

		World world = player.getWorld();
		int y = world.getHighestBlockYAt(x, z);
		Location loc = new Location(world, x, y, z);

		if (!loc.getChunk().isLoaded()) {
			loc.getChunk().load();
		}

		player.teleport(loc.clone().add(0, 0.5, 0));
		
		/*
		 * Check if have a killer and add xp to his
		 */

		if (player.getKiller() != null) {
			getGameGeneral().getGamerController().getGamer(player.getKiller().getUniqueId()).addKill();
			Member battleKiller = CommonGeneral.getInstance().getMemberManager()
					.getMember(player.getKiller().getUniqueId());

			if (battleKiller != null) {
				int amount = CommonConst.RANDOM.nextInt(5) + 1;
				battleKiller.addXp(amount);
				player.getKiller().sendMessage("§a§l> §fVocê ganhou §a%amount% xp§f!".replace("%amount%", "" + amount));
			}
		}
		
		/*
		 * Respawn player if have GameMain.RESPAWN_GROUP group permission and the game time is less than 300
		 */

		if (member.hasGroupPermission(ServerConfig.getInstance().getRespawnGroup()) && GameGeneral.getInstance().getTime() <= 300) {
			/*
			 * Fire PlayerItemReceiveEvent
			 */
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
				}
			}.runTaskLater(GameMain.getInstance(), 10l);
			return;
		}
		
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
					killer = player.getKiller();
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
		replaces.put("%player_Kit%", GameMain.DOUBLEKIT
				? gamer.hasKit(KitType.PRIMARY) && gamer.hasKit(KitType.SECONDARY)
						? NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)) + "/"
								+ NameUtils.formatString(gamer.getKitName(KitType.SECONDARY))
						: gamer.hasKit(KitType.PRIMARY) ? NameUtils.formatString(gamer.getKitName(KitType.PRIMARY))
								: NameUtils.formatString(gamer.getKitName(KitType.SECONDARY))
				: NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)));
		gamer.getStatus().addDeath();

		if (killer != null) {
			Gamer killerGamer = getGameGeneral().getGamerController().getGamer(killer.getUniqueId());
			killerGamer.addKill();
			replaces.put("%killed_By%", killer.getName());

			replaces.put("%killed_By_Kit%",
					GameMain.DOUBLEKIT
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
			if (!member.hasGroupPermission(ServerConfig.getInstance().getSpectatorGroup()) && !player.hasPermission("tag.winner")) {
				int number = GameGeneral.getInstance().getPlayersInGame() + 1;

				if (number <= 10) {
					player.sendMessage("§cVocê ficou entre os ultimos §c10 jogadores§f!");
					player.sendMessage("§cVocê morreu: " + kickMessage);
					GameMain.getInstance().sendPlayerToLobby(player);
					return;
				}

				player.sendMessage("§cVocê morreu: " + kickMessage);
				GameMain.getInstance().sendPlayerToLobby(player);
			}
	}

	public static String deathMessage(Player player, String messageId, HashMap<String, String> replaces) {
		String messageReturn = "";

		for (Player p : Bukkit.getOnlinePlayers()) {
			String message = t(messageId);

			for (Entry<String, String> entry : replaces.entrySet()) {
				message = message.replace(entry.getKey(), entry.getValue());
			}

			p.sendMessage(message.replace("%players%", (GameGeneral.getInstance().getPlayersInGame() - 1) + ""));
			if (player.getUniqueId() == p.getUniqueId()) {
				messageReturn = message;
			}
		}

		return messageReturn;
	}

	private static String t(String messageId) {
		if (translateMap.containsKey(messageId)) {
			return translateMap.get(messageId);
		}

		System.out.println("Missing id " + messageId);
		return translateMap.get("death-message-null").replace("&", "§");
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

		ItemUtils.dropAndClear(p, items, p.getLocation());
	}

	public static String getItemName(ItemStack item) {
		if (item == null)
			item = new ItemStack(Material.AIR);
		return NameUtils.getName(item.getType().name());
	}

	private static boolean checkNotNull(ItemStack item) {
		return item != null && item.getType() != Material.AIR;
	}

}
