package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class AjninAbility extends Ability {

	private HashMap<String, NinjaHit> ninjaHits;

	public AjninAbility() {
		super("Ajnin", new ArrayList<>());
		ninjaHits = new HashMap<>();
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player damaged = event.getPlayer();
		Player damager = event.getDamager();
		
		if (hasAbility(damager)) {
			NinjaHit ninjaHit = ninjaHits.get(damager.getName());
			if (ninjaHit == null)
				ninjaHit = new NinjaHit(damaged);
			else
				ninjaHit.setTarget(damaged);
			ninjaHits.put(damager.getName(), ninjaHit);
		}
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if (!event.isSneaking())
			return;

		if (!hasAbility(p))
			return;

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(p);

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

		if ((p.getLocation().distance(target.getLocation()) > 40)) {
			p.sendMessage("§a§l> §fO ultimo jogador hitado está muito longe!");
			return;
		}

		if (isCooldown(p)) {
			return;
		}
		
		if (gamer.hasAbility("kangaroo"))
			if (gamer.getAbility("kangaroo").isAbilityItem(p.getItemInHand()))
				return;

		target.teleport(p.getLocation());
		p.sendMessage("§a§l> §fJogador teletransportado!");
		addCooldown(p.getUniqueId(), 8l);
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

}
