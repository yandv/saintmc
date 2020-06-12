package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class MonkKit extends Kit {

	public MonkKit() {
		super("Monk", "Bagunce o inventário de seus inimigos", Material.BLAZE_ROD,
				Arrays.asList(new ItemBuilder().name("§aMonk").type(Material.BLAZE_ROD).build()));
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return;

		Player p = e.getPlayer();

		if (!hasAbility(p))
			return;

		ItemStack item = p.getItemInHand();

		if (!isAbilityItem(item))
			return;

		Player clicked = (Player) e.getRightClicked();

		if (GameMain.getInstance().getGamerManager().getGamer(clicked.getUniqueId()).isSpawnProtection())
			return;

		if (CooldownController.getInstance().hasCooldown(p, getName())) {
//			p.sendMessage(GameMain.getPlugin().getCooldownManager().getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}

		CooldownController.getInstance().addCooldown(p, new Cooldown(getName(), 15l));

		int randomN = new Random().nextInt(36);

		ItemStack atual = (clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null);
		ItemStack random = (clicked.getInventory().getItem(randomN) != null
				? clicked.getInventory().getItem(randomN).clone()
				: null);

		if (random == null) {
			clicked.getInventory().setItem(randomN, atual);
			clicked.setItemInHand(null);
		} else {
			clicked.getInventory().setItem(randomN, atual);
			clicked.getInventory().setItemInHand(random);
		}
	}

}
