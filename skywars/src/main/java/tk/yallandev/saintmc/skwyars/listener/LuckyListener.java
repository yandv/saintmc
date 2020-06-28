package tk.yallandev.saintmc.skwyars.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.skwyars.GameMain;

@Getter
public class LuckyListener implements Listener {

	private List<Lucky> luckyList;

	private Map<ArmorStand, Integer> standMap;

	public LuckyListener() {
		luckyList = new ArrayList<>();
		standMap = new HashMap<>();

		add(new ItemBuilder().name("§aEspada de Diamante").type(Material.DIAMOND_SWORD)
				.enchantment(Enchantment.DAMAGE_ALL).build());

		add(new ItemBuilder().type(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build());
		add(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
				.build());
		add(new ItemBuilder().type(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
				.build());
		add(new ItemBuilder().type(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL).build());

		add(new ItemBuilder().type(Material.GOLDEN_APPLE).durability(1).build());
		add(new ItemBuilder().type(Material.GOLDEN_APPLE).build());
		add(new ItemBuilder().type(Material.DIAMOND_BLOCK).build());

		add(player -> {
			List<EntityType> list = Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.BLAZE,
					EntityType.SLIME, EntityType.SILVERFISH);

			player.getWorld().spawnEntity(player.getLocation().add(4, 0, 0),
					list.get(CommonConst.RANDOM.nextInt(list.size())));
			player.getWorld().spawnEntity(player.getLocation().add(0, 0, 4),
					list.get(CommonConst.RANDOM.nextInt(list.size())));
			player.getWorld().spawnEntity(player.getLocation().add(-4, 0, 0),
					list.get(CommonConst.RANDOM.nextInt(list.size())));
			player.getWorld().spawnEntity(player.getLocation().add(0, 0, -4),
					list.get(CommonConst.RANDOM.nextInt(list.size())));
			player.getWorld().spawnEntity(player.getLocation().add(4, 0, 4),
					list.get(CommonConst.RANDOM.nextInt(list.size())));
		});

		add(player -> {
			player.damage(CommonConst.RANDOM.nextInt(14) + 2);
			player.sendMessage("§cMais sorte na próxima!");
		});
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() == Material.STAINED_GLASS && block.hasMetadata("luckyEntityId")) {
			luckyList.get(CommonConst.RANDOM.nextInt(luckyList.size())).apply(event.getPlayer());

			ArmorStand armorStand = (ArmorStand) block.getMetadata("luckyEntityId").stream().findFirst().orElse(null)
					.value();
			armorStand.remove();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		
//		ItemStack itemStack = event.getItemInHand();
		
		if (block.getType() == Material.STAINED_GLASS && block.getData() == 4) {
			ArmorStand armorStand = (ArmorStand) event.getBlock().getWorld()
					.spawnEntity(event.getBlock().getLocation().add(0.5, -1.2, 0.5), EntityType.ARMOR_STAND);

			armorStand.setGravity(false);
			armorStand.setVisible(false);
			armorStand.setCustomName("§6Lucky Block");
			armorStand.setCustomNameVisible(true);
			armorStand.setCanPickupItems(false);

			armorStand.setHelmet(new ItemBuilder().name("§6Lucky Block").type(Material.SKULL_ITEM).skin("Lucky")
					.durability(3).build());
			standMap.put(armorStand, 0);

			block.setMetadata("luckyEntityId", new FixedMetadataValue(GameMain.getInstance(), armorStand));
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		standMap.entrySet().forEach(entry -> {
			entry.getKey().setHeadPose(new EulerAngle(0f, entry.getValue() * Math.PI / 180, 0f));
			entry.setValue(entry.getValue() + 2);

			if (entry.getValue() > 360)
				entry.setValue(0);
		});
	}

	public interface Lucky {

		void apply(Player player);

	}

	void add(ItemStack itemStack) {
		luckyList.add(player -> player.getWorld().dropItem(player.getLocation(), itemStack));
	}

	void add(Lucky lucky) {
		luckyList.add(lucky);
	}
}
