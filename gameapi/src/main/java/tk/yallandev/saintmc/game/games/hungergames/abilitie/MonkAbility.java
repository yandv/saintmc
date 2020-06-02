package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.stage.GameStage;

public class MonkAbility extends Ability implements Disableable {

	public MonkAbility() {
		super(new ItemBuilder().type(Material.BLAZE_ROD).build(), AbilityRarity.RARE);
		options.put("COOLDOWN", new CustomOption("COOLDOWN", new ItemStack(Material.WATCH), -1, 8, 15, 22));
		options.put("ITEM", new CustomOption("ITEM", new ItemBuilder().type(Material.BLAZE_ROD).build(), "�aMonk Rod"));
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return;
		
		Player p = e.getPlayer();
		
		if (!hasAbility(p))
			return;
		
		ItemStack item = p.getItemInHand();
		ItemStack KANGAROO_ITEM = getOption(p, "ITEM").getItemStack();
		
		if (!ItemUtils.isEquals(item, KANGAROO_ITEM))
			return;

		Player clicked = (Player) e.getRightClicked();

		if (GameStage.isInvincibility(GameMain.getPlugin().getGameStage())) {
			p.sendMessage("�cVoc� n�o pode usar isto agora!");
			return;
		}

		if (CooldownController.hasCooldown(p.getUniqueId(), getName())) {
			p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
			p.sendMessage(CooldownController.getCooldownFormated(p.getUniqueId(), getName()));
			return;
		}

		CooldownController.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());

		int randomN = new Random().nextInt(36);

		ItemStack atual = (clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null);
		ItemStack random = (clicked.getInventory().getItem(randomN) != null ? clicked.getInventory().getItem(randomN).clone() : null);

		if (random == null) {
			clicked.getInventory().setItem(randomN, atual);
			clicked.setItemInHand(null);
		} else {
			clicked.getInventory().setItem(randomN, atual);
			clicked.getInventory().setItemInHand(random);
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 0;
	}

}
