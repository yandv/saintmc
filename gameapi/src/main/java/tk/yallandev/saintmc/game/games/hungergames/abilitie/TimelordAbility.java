package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;
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
		
		Location mainBlock = p.getLocation();
		
		new BukkitRunnable() {
			
			int x = 0;
			
			@Override
			public void run() {
				if (this.x > 6) {
					cancel();
					return;
				}
				
				double x;
				double z;
				for (double cXMenor = (double) (-20); cXMenor <= (double) 20; ++cXMenor) {
					for (x = (double) (-20); x <= (double) 20; ++x) {
						for (z = 0.0D; z <= (double) 5; ++z) {
							Location location = new Location(mainBlock.getWorld(), (double) mainBlock.getX() + cXMenor,
									(double) mainBlock.getY() + z, (double) mainBlock.getZ() + x);
							
							PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL, true,
									(float) location.getX(), (float) location.getY(),
									(float) location.getZ(), 0, 0, 0, 0, 1);
						}
					}
				}
				
				this.x++;
			}
		}.runTaskTimer(GameMain.getPlugin(), 0, 20);
		
		e.setCancelled(true);
		CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 15 + (getOption("COOLDOWN", map).getValue() - 10);
	}


}
