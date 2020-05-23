package tk.yallandev.saintmc.game.games.hungergames.abilitie;

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

import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class ThorAbility extends Ability implements Disableable {
	
	HashMap<UUID, Long> damageRaio = new HashMap<>();

	public ThorAbility() {
		super(new ItemStack(Material.WOOD_AXE), AbilityRarity.COMMON);
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), 2, 4, 6, 10));
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.WOOD_AXE), ChatColor.GOLD + "Thor"));
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

		ItemStack THOR_ITEM = getOption(p, "ITEM").getItemStack();

		if (!ItemUtils.isEquals(p.getItemInHand(), THOR_ITEM))
			return;

		if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR))
			return;

		if (hasAbility(p)) {
			if (CooldownAPI.hasCooldown(p.getUniqueId(), getName())) {
				p.sendMessage(CooldownAPI.getCooldownFormated(p.getUniqueId(), getName()));
				return;
			}

			Location loc = p.getTargetBlock((Set<Material>) null, 20).getLocation();
			loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

			damageRaio.put(p.getUniqueId(), System.currentTimeMillis() + 4000l);
			p.getWorld().strikeLightning(loc);

			if (loc.getBlock().getY() >= 110) {
				Location newLocation = loc.clone();

				if (newLocation.getBlock().getType() == Material.NETHERRACK) {
					newLocation.getWorld().createExplosion(newLocation, 2.5F);
				} else {
					loc.clone().add(0, 1, 0).getBlock().setType(Material.NETHERRACK);
				}
			}

			CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 10 + (2 * (getOption("COOLDOWN", map).getValue()));
	}

}
