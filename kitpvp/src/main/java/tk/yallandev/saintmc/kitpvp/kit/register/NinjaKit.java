package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class NinjaKit extends Kit {

	private HashMap<String, NinjaHit> ninjaHits;

	public NinjaKit() {
		super("Ninja", "Como um ninja teletransporte-se para as costas de seus inimigos", Material.EMERALD);
		ninjaHits = new HashMap<>();
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

		if (!ninjaHits.containsKey(p.getName()))
			return;

		NinjaHit ninjaHit = ninjaHits.get(p.getName());
		Player target = ninjaHit.getTarget();

		if (target.isDead())
			return;

		if (ninjaHit.getTargetExpires() < System.currentTimeMillis())
			return;

		if ((p.getLocation().distance(target.getLocation()) > 50)) {
			p.sendMessage("§a§l> §fO jogador está muito longe§f!");
			return;
		}

		if (CooldownController.getInstance().hasCooldown(p, getName())) {
			p.sendMessage(CooldownController.getInstance().getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}

		p.teleport(target.getLocation());
		p.sendMessage("§a§l> §fTeletransportado até o §a" + target.getName() + "§f!");
		CooldownController.getInstance().addCooldown(p, new Cooldown(getName(), 6l));
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();

		if (p.getKiller() != null)
			for (Entry<String, NinjaHit> entry : ninjaHits.entrySet())
				if (entry.getValue().target == p.getKiller())
					ninjaHits.remove(entry.getKey());

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
	public void applyKit(Player player) {
		// TODO Auto-generated method stub

	}

}