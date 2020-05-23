package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class ReaperAbility extends Ability implements Disableable {

	public ReaperAbility() {
		super(new ItemStack(Material.SKULL_ITEM, 1, (byte) 1), AbilityRarity.RARE);
		options.put("CHANCE", new CustomOption("CHANCE", new ItemStack(Material.GOLD_NUGGET), -1, 1, 3, 5));
		options.put("DURATION", new CustomOption("DURATION", new ItemStack(Material.SKULL_ITEM, 1, (byte) 1), 1, 3, 5, 10));
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.WOOD_HOE), ChatColor.GOLD + "Reaper"));
	}

	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		
		if (!hasAbility(damager))
			return;
		
		ItemStack item = damager.getItemInHand();
		
		if (item == null)
			return;
		
		ItemStack ITEM = getOption(damager, "ITEM").getItemStack();
		
		if (!ItemUtils.isEquals(item, ITEM))
			return;
		
		event.setCancelled(true);
		item.setDurability(ITEM.getDurability());
		damager.updateInventory();
		Random r = new Random();
		CustomOption CHANCE = getOption(damager, "CHANCE");
		Player damaged = (Player) event.getEntity();
		
		if (damaged instanceof Player) {
			if (r.nextInt(CHANCE.getValue()) == 0) {
				damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, getOption(damager, "DURATION").getValue() * 20, 0));
			}
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return getOption("DURATION", map).getValue() * 3 + (18 - (getOption("CHANCE", map).getValue() * 3));
	}
}
