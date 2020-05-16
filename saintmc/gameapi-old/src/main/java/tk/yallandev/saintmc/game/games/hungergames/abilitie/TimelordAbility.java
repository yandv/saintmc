package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.battlebits.game.GameMain;
import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.constructor.Gamer;
import br.com.battlebits.game.games.hungergames.util.ItemUtils;
import br.com.battlebits.game.interfaces.Disableable;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.game.stage.GameStage;

public class TimelordAbility extends Ability implements Disableable {
	
	public TimelordAbility() {
		super(new ItemStack(Material.WATCH), AbilityRarity.COMMON);
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), 5, 30, 45, 70));
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.WATCH), ChatColor.GOLD + "Timelord"));
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!e.getAction().name().contains("RIGHT"))
			return;
		
		if (!hasAbility(e.getPlayer()))
			return;
		
		if (!GameStage.isInvincibility(GameMain.getPlugin().getGameStage()))
			return;
		
		Player p = e.getPlayer();
		
		ItemStack THOR_ITEM = getOption(p, "ITEM").getItemStack();
		
		if (!ItemUtils.isEquals(p.getItemInHand(), THOR_ITEM))
			return;
		
		if (CooldownAPI.hasCooldown(p.getUniqueId(), getName())) {
			p.sendMessage(CooldownAPI.getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}
		

		for (Entity entity : e.getPlayer().getNearbyEntities(20, 20, 20)) {
			if (!(entity instanceof Player)) {
				continue;
			}

			Gamer gamer = Gamer.getGamer((Player) entity);

			if (gamer.isNotPlaying())
				continue;

			((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 255), true);
			((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 250), true);
		}
		
		e.setCancelled(true);
		CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 15 + (getOption("COOLDOWN", map).getValue() - 10);
	}


}
