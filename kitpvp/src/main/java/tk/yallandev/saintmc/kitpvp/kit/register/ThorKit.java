package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class ThorKit extends Kit {
	
	private HashMap<UUID, Long> damageRaio;

	public ThorKit() {
		super("Thor", "Jogue raios em seus inimigos com seu machado", Material.WOOD_AXE);
		damageRaio = new HashMap<>();
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();

		if ((event.getEntity() instanceof LightningStrike)) {
			if (damageRaio.containsKey(player.getUniqueId()) && damageRaio.get(player.getUniqueId()) < System.currentTimeMillis()) {
				event.setDamage(0.0D);
			} else {
				event.setDamage(6.0D);
				event.getEntity().setFireTicks(200);
			}
		}
	}

	@EventHandler
	public void Thorzao(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (p.getItemInHand() == null)
			return;

		if (p.getItemInHand().getType() != Material.WOOD_AXE)
			return;

		if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR))
			return;

		if (hasAbility(p)) {
			if (CooldownController.getInstance().hasCooldown(p, getName())) {
//				p.sendMessage(GameMain.getPlugin().getCooldownManager().getCooldownFormated(p.getUniqueId(), getName()));
				return;
			}

			Location loc = p.getTargetBlock((Set<Material>) null, 20).getLocation();
			loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

			damageRaio.put(p.getUniqueId(), System.currentTimeMillis() + 4000l);
			p.getWorld().strikeLightning(loc);

			CooldownController.getInstance().addCooldown(p, new Cooldown(getName(), 8l));
		}
	}

	@Override
	public void applyKit(Player player) {
		player.getInventory().setItem(1, new ItemStack(Material.WOOD_AXE));
	}

}
