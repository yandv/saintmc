package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class AjninAbility extends Ability implements Disableable {

	private HashMap<String, NinjaHit> ninjaHits;

	public AjninAbility() {
		super(new ItemStack(Material.NETHER_STAR), AbilityRarity.EPIC);
		ninjaHits = new HashMap<>();
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), -1, 5, 10, 15));
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

		if ((p.getLocation().distance(target.getLocation()) > 40)) {
			p.sendMessage("§a§l> §fO ultimo jogador hitado está muito longe!");
			return;
		}

		if (CooldownController.hasCooldown(p.getUniqueId(), getName())) {
			p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
			p.sendMessage(CooldownController.getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}

		if (gamer.getKit() != null)
			if (gamer.hasAbility("kangaroo")) {
				if (ItemUtils.isEquals(p.getItemInHand(),
						gamer.getAbility("kangaroo").getOption(p, "ITEM").getItemStack())) {
					return;
				}
			}

		target.teleport(p.getLocation());
		p.sendMessage("§a§l> §fJogador teletransportado!");
		p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5F, 1.0F);
		CooldownController.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
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
