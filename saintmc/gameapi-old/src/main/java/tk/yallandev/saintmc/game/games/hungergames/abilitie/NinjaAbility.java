package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.constructor.Gamer;
import br.com.battlebits.game.games.hungergames.util.ItemUtils;
import br.com.battlebits.game.interfaces.Disableable;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;

public class NinjaAbility extends Ability implements Disableable {

	private HashMap<String, NinjaHit> ninjaHits;

	public NinjaAbility() {
		super(new ItemStack(Material.EMERALD), AbilityRarity.EPIC);
		ninjaHits = new HashMap<>();
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), -1, 5, 10, 15));
	}

	@EventHandler
	public void onNinjaHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			final Player damager = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			if (hasAbility(damager)) {
				NinjaHit ninjaHit = ninjaHits.get(damager.getName());
				if (ninjaHit == null)
					ninjaHit = new NinjaHit(damaged);
				else
					ninjaHit.setTarget(damaged);
				ninjaHits.put(damager.getName(), ninjaHit);
			}
		}
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if (!event.isSneaking())
			return;
		
		if (!hasAbility(p))
			return;
		
		Gamer gamer = Gamer.getGamer(p);
		
		if (gamer.isNotPlaying())
			return;
		
		if (!ninjaHits.containsKey(p.getName()))
			return;
		
		NinjaHit ninjaHit = ninjaHits.get(p.getName());
		Player target = ninjaHit.getTarget();
		
		if (target.isDead())
			return;
		
		if (ninjaHit.getTargetExpires() < System.currentTimeMillis())
			return;
		
		if ((p.getLocation().distance(target.getLocation()) > 50)) {
			p.sendMessage("§%ninja-too-far-away%§");
			return;
		}
		
		if (CooldownAPI.hasCooldown(p.getUniqueId(), getName())) {
			p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
			p.sendMessage(CooldownAPI.getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}
		
		if (gamer.getKit() != null)
			if (gamer.getKit().hasAbility("kangaroo")) {
				if (ItemUtils.isEquals(p.getItemInHand(), gamer.getKit().getAbility("kangaroo").getOption(p, "ITEM").getItemStack())) {
					return;
				}
			}
		
		p.teleport(target.getLocation());
		p.sendMessage("§%ninja-teleported%§");
		p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5F, 1.0F);
		CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (!ninjaHits.containsKey(p.getName()))
			return;
		ninjaHits.remove(p.getName());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (!ninjaHits.containsKey(p.getName()))
			return;
		ninjaHits.remove(p.getName());
	}

	private static class NinjaHit {
		private Player target;
		private long targetExpires;

		public NinjaHit(Player target) {
			this.target = target;
			this.targetExpires = System.currentTimeMillis() + 15000;
		}

		public Player getTarget() {
			return target;
		}

		public long getTargetExpires() {
			return targetExpires;
		}

		public void setTarget(Player player) {
			this.target = player;
			this.targetExpires = System.currentTimeMillis() + 20000;
		}

	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 60 - (3 * getOption("COOLDOWN", map).getValue());
	}

}
