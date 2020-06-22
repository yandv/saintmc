package tk.yallandev.saintmc.kitpvp.warp.challenge.shadow;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightStartEvent;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ArmorType;
import tk.yallandev.saintmc.kitpvp.warp.challenge.Challenge;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ChallengeType;

@Getter
public class ShadowChallenge implements Challenge {

	private Player player;
	private Player target;

	private ItemStack sword;
	private ArmorType armorType;

	private Map<Enchantment, Integer> armorEnchantments;
	private ChallengeType challengeType;

	private boolean refil;
	private boolean recraft;
	private boolean speed;
	private boolean strenght;

	private long expire;

	public ShadowChallenge(Player player, Player target, ChallengeType challengeType) {
		this.player = player;
		this.target = target;

		this.sword = new ItemBuilder().name("§aEspada de Diamante!").type(Material.DIAMOND_SWORD)
				.enchantment(Enchantment.DAMAGE_ALL).build();
		this.armorType = ArmorType.IRON;
		this.armorEnchantments = new HashMap<>();
		this.challengeType = challengeType;

		this.expire = System.currentTimeMillis() + 30000l;
	}

	public void setRecraft(boolean recraft) {
		this.recraft = recraft;
		this.challengeType = ChallengeType.SHADOW_CUSTOM;
	}

	public void setRefil(boolean refil) {
		this.refil = refil;
		this.challengeType = ChallengeType.SHADOW_CUSTOM;
	}

	public void setSpeed(boolean speed) {
		this.speed = speed;
		this.challengeType = ChallengeType.SHADOW_CUSTOM;
	}

	public void setStrenght(boolean strenght) {
		this.strenght = strenght;
		this.challengeType = ChallengeType.SHADOW_CUSTOM;
	}

	@Override
	public boolean isInChallenge(Player player) {
		return this.player == player || this.target == player;
	}

	public void createInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.setHealth(20D);
		player.setLevel(0);

		for (PotionEffect pot : player.getActivePotionEffects())
			player.removePotionEffect(pot.getType());

		player.getInventory().setItem(0, getSword());

		if (armorType != ArmorType.NONE) {
			player.getInventory()
					.setArmorContents(new ItemStack[] { new ItemStack(Material.valueOf(armorType.name() + "_BOOTS")),
							new ItemStack(Material.valueOf(armorType.name() + "_LEGGINGS")),
							new ItemStack(Material.valueOf(armorType.name() + "_CHESTPLATE")),
							new ItemStack(Material.valueOf(armorType.name() + "_HELMET")) });
		}

		for (int x = 0; x < (isRefil() ? player.getInventory().getSize() : 8); x++) {
			player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}

		if (isRecraft()) {
			player.getInventory().setItem(13, new ItemStack(Material.RED_MUSHROOM, 64));
			player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 64));
			player.getInventory().setItem(15, new ItemStack(Material.BOWL, 64));
		}
	}

	@Override
	public boolean isExpired() {
		return expire < System.currentTimeMillis();
	}

	@Override
	public void start(Location firstLocation, Location secondLocation) {
		Bukkit.getPluginManager().callEvent(new ShadowFightStartEvent(player, target,
				GameMain.getInstance().getWarpManager().getWarpByName("1v1"), getChallengeType()));

		player.teleport(firstLocation);
		target.teleport(secondLocation);

		createInventory(player);
		createInventory(target);

		VanishAPI.getInstance().hideAllPlayers(player);
		VanishAPI.getInstance().hideAllPlayers(target);

		player.showPlayer(target);
		target.showPlayer(player);
	}

	@Override
	public void finish(Player player) {
		Bukkit.getPluginManager().callEvent(new ShadowFightFinishEvent(getPlayer(), target, player,
				GameMain.getInstance().getWarpManager().getWarpByName("1v1")));

		VanishAPI.getInstance().getHideAllPlayers().remove(getPlayer().getUniqueId());
		VanishAPI.getInstance().updateVanishToPlayer(getPlayer());

		VanishAPI.getInstance().getHideAllPlayers().remove(target.getUniqueId());
		VanishAPI.getInstance().updateVanishToPlayer(target);
	}

}
