package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.GameState;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class TimelordAbility extends Ability {
	
	public TimelordAbility() {
		super("Timelord", Arrays.asList(new ItemBuilder().name(ChatColor.GOLD + "Timelord").type(Material.WATCH).build()));
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!e.getAction().name().contains("RIGHT"))
			return;
		
		if (!hasAbility(e.getPlayer()))
			return;
		
		if (!GameState.isInvincibility(GameGeneral.getInstance().getGameState()))
			return;
		
		Player player = e.getPlayer();
		
		if (!isAbilityItem(player.getItemInHand()))
			return;
			
		if (CooldownAPI.hasCooldown(player.getUniqueId(), getName())) {
			player.sendMessage(CooldownAPI.getCooldownFormated(player.getUniqueId(), getName()));
			return;
		}
		
		for (Entity entity : e.getPlayer().getNearbyEntities(20, 20, 20)) {
			if (!(entity instanceof Player)) {
				continue;
			}

			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer((Player) entity);

			if (gamer.isNotPlaying())
				continue;

			((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 255), true);
			((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 250), true);
		}
		
//		Location mainBlock = player.getLocation();
//		
//		new BukkitRunnable() {
//			
//			int x = 0;
//			
//			@Override
//			public void run() {
//				if (this.x > 6) {
//					cancel();
//					return;
//				}
//				
//				double x;
//				double z;
//				for (double cXMenor = (double) (-20); cXMenor <= (double) 20; ++cXMenor) {
//					for (x = (double) (-20); x <= (double) 20; ++x) {
//						for (z = 0.0D; z <= (double) 5; ++z) {
//							Location location = new Location(mainBlock.getWorld(), (double) mainBlock.getX() + cXMenor,
//									(double) mainBlock.getY() + z, (double) mainBlock.getZ() + x);
//							
//							PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL, true,
//									(float) location.getX(), (float) location.getY(),
//									(float) location.getZ(), 0, 0, 0, 0, 1);
//							
//							((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
//						}
//					}
//				}
//				
//				this.x++;
//			}
//		}.runTaskTimer(GameMain.getInstance(), 0, 20);
		
		e.setCancelled(true);
		CooldownAPI.addCooldown(player.getUniqueId(), getName(), 12);
	}

}
