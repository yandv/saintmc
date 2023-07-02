package br.com.saintmc.hungergames.abilities.register;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityCow;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import net.minecraft.server.v1_8_R3.EntityOcelot;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntitySilverfish;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntitySquid;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.World;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@SuppressWarnings("deprecation")
public class ChameleonAbility extends Ability {

	private Map<UUID, Disguise> playerMap;

	public ChameleonAbility() {
		super("Chameleon", Arrays.asList(new ItemBuilder().name("§aChameleon").type(Material.PAPER).build()));
		playerMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.getAction() != Action.PHYSICAL && isAbilityItem(event.getItem())) {
			if (isCooldown(player))
				return;

			Disguise disguise = playerMap.computeIfAbsent(event.getPlayer().getUniqueId(), v -> new Disguise());

			if (disguise.getEntityList().isEmpty()) {
				player.sendMessage("§cVocê não pode se transformar em nenhum mob!");
				player.sendMessage("§cMate mobs para adiciona-lós a sua lista!");
				return;
			}

			if (disguise.isDisguised()) {
				removerDisguise(player);
				player.sendMessage("§aVocê não está mais disfarçado!");
				disguise.setDisguised(false);
			} else {
				MenuInventory menu = new MenuInventory("§aChameleon", 5);

				menu.setItem(26, new ItemBuilder().name("§aPróxima página").type(Material.ARROW).build());

				int itemIndex = 11;

				for (EntityType entityType : disguise.getEntityList()) {
					ItemStack itemStack = new ItemBuilder()
							.name("§a" + NameUtils.formatString(entityType.name().replace("_", " ")))
							.durability(entityType.getTypeId()).type(Material.MONSTER_EGG).build();

					menu.setItem(itemIndex, itemStack,
							(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) -> {
								if (!disguise.isDisguised()) {
									player.sendMessage("§aVocê se transformou em um(a) "
											+ NameUtils.formatString(entityType.name().replace("_", " ")) + "!");
									player.sendMessage("§aPara transforma-se novamente em humano, clica ");
									player.closeInventory();
									disguise.lastHand = System.currentTimeMillis();
									morf(player, entityType);
									disguise.setDisguised(true);
								}
								return false;
							});

					if (itemIndex % 9 == 6) {
						itemIndex += 4;
						continue;
					}

					itemIndex += 1;
				}

				menu.open(player);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity.getKiller() != null) {
			Player killer = entity.getKiller();

			if (hasAbility(killer)) {
				Disguise disguise = playerMap.computeIfAbsent(killer.getUniqueId(), v -> new Disguise());

				if (getEntityLiving(event.getEntityType()) == null)
					return;

				if (!disguise.getEntityList().contains(event.getEntityType())) {
					disguise.getEntityList().add(event.getEntityType());
					killer.sendMessage("§aAgora você pode se transformar em um "
							+ NameUtils.formatString(event.getEntityType().name()) + "!");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (hasAbility(event.getPlayer())) {
			Disguise disguise = playerMap.computeIfAbsent(event.getPlayer().getUniqueId(), v -> new Disguise());

			if (disguise.isDisguised()) {
				removerDisguise(event.getPlayer());
				disguise.setDisguised(false);
			}
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<UUID, Disguise>> iterator = playerMap.entrySet().stream()
					.filter(entry -> entry.getValue().isDisguised()).collect(Collectors.toList()).iterator();

			while (iterator.hasNext()) {
				Entry<UUID, Disguise> entry = iterator.next();

				Player player = Bukkit.getPlayer(entry.getKey());

				if (player == null)
					continue;

				if (player.getItemInHand().getType() == Material.PAPER)
					entry.getValue().lastHand = System.currentTimeMillis();
				else if (System.currentTimeMillis() - entry.getValue().lastHand >= 5000) {
					entry.getValue().disguised = false;
					player.sendMessage(
							"§cVocê não é mais um mob, pois ficou sem o item do kit na mão por mais de 5 segundos!");
					removerDisguise(player);
					addCooldown(player, 15l);
				}
			}
		}
	}

	public void morf(Player player, EntityType entityType) {
		try {
			PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[] { player.getEntityId() });

			Entity entity = getEntityLiving(entityType).getConstructor(World.class)
					.newInstance(((CraftWorld) player.getWorld()).getHandle());

			entity.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
			entity.d(player.getEntityId());
			EntityInsentient entityInsentient = (EntityInsentient) entity;

			entityInsentient.setCustomName("§a" + player.getName());

			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);

			for (Player pls : Bukkit.getOnlinePlayers()) {
				if (pls == player)
					continue;

				((CraftPlayer) pls).getHandle().playerConnection.sendPacket(destroy);
				((CraftPlayer) pls).getHandle().playerConnection.sendPacket(spawn);
				pls.showPlayer(player);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void removerDisguise(Player player) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[] { player.getEntityId() });
		PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online == player)
				continue;

			((CraftPlayer) online).getHandle().playerConnection.sendPacket(destroy);
			((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);
			online.showPlayer(player);
		}
	}

	@Getter
	public class Disguise {

		private List<EntityType> entityList;
		@Setter
		private boolean disguised;
		@Setter
		private int index;

		private long lastHand;

		public Disguise() {
			entityList = new ArrayList<>();
		}

	}

	public Class<? extends net.minecraft.server.v1_8_R3.Entity> getEntityLiving(EntityType entityType) {
		switch (entityType) {
		case BAT:
			return EntityBat.class;
		case BLAZE:
			return EntityBlaze.class;
		case CAVE_SPIDER:
			return EntityCaveSpider.class;
		case COW:
			return EntityCow.class;
		case CREEPER:
			return EntityCreeper.class;
		case ENDERMAN:
			return EntityEnderman.class;
		case HORSE:
			return EntityHorse.class;
		case IRON_GOLEM:
			return EntityIronGolem.class;
		case MUSHROOM_COW:
			return EntityMushroomCow.class;
		case OCELOT:
			return EntityOcelot.class;
		case PIG:
			return EntityPig.class;
		case PIG_ZOMBIE:
			return EntityPigZombie.class;
		case SHEEP:
			return EntitySheep.class;
		case SILVERFISH:
			return EntitySilverfish.class;
		case SKELETON:
			return EntitySkeleton.class;
		case SLIME:
			return EntitySlime.class;
		case SNOWMAN:
			return EntitySnowman.class;
		case SPIDER:
			return EntitySpider.class;
		case SQUID:
			return EntitySquid.class;
		case VILLAGER:
			return EntityVillager.class;
		case WITCH:
			return EntitySquid.class;
		case WOLF:
			return EntityWolf.class;
		case ZOMBIE:
			return EntityZombie.class;
		case CHICKEN:
			return EntityChicken.class;
		default:
			return null;
		}
	}
}
