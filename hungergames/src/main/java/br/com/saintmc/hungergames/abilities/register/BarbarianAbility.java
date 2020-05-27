package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BarbarianAbility extends Ability {

	public BarbarianAbility() {
		super("Barbarian", Arrays.asList(new ItemBuilder().type(Material.WOOD_SWORD).name("§aEspada do barbarian").glow().enchantment(Enchantment.DURABILITY).build()));
	}

	private HashMap<UUID, Integer> kills = new HashMap<UUID, Integer>();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if ((e.getEntity().getKiller() != null) && (e.getEntity().getKiller() instanceof Player)
				&& (hasAbility(e.getEntity().getKiller()))) {
			Player p = e.getEntity().getKiller();

			if (this.kills.containsKey(p.getUniqueId())) {
				this.kills.put(p.getUniqueId(), this.kills.get(p.getUniqueId()) + 1);
			} else {
				this.kills.put(p.getUniqueId(), 1);
			}

			if (p.getItemInHand() == null || !p.getItemInHand().hasItemMeta()
					|| !p.getItemInHand().getItemMeta().hasDisplayName())
				return;

			if (p.getItemInHand().getItemMeta().getDisplayName().contains("Espada do barbarian")) {
				switch (this.kills.get(p.getUniqueId())) {
				case 2:
					p.getItemInHand().setType(Material.STONE_SWORD);
					p.getItemInHand().setDurability((short) 0);
					break;
				case 4:
					p.getItemInHand().setType(Material.IRON_SWORD);
					p.getItemInHand().setDurability((short) 0);
					break;
				case 6:
					p.getItemInHand().setType(Material.DIAMOND_SWORD);
					p.getItemInHand().setDurability((short) 0);
					break;
				case 8:
					p.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
					p.getItemInHand().setDurability((short) 0);
					break;
				case 12:
					p.getItemInHand().addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
					p.getItemInHand().setDurability((short) 0);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();

		if (!hasAbility(player))
			return;

		if (player.getItemInHand() == null || !player.getItemInHand().hasItemMeta()
				|| !player.getItemInHand().getItemMeta().hasDisplayName())
			return;

		if (player.getItemInHand().getItemMeta().getDisplayName().contains("Espada do barbarian")) {
			player.getItemInHand().setDurability((short) 0);
			player.updateInventory();
		}
	}

}
