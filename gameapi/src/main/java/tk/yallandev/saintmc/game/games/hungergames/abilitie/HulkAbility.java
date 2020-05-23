package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.stage.GameStage;

public class HulkAbility extends Ability implements Disableable {
	
	public HulkAbility() {
		super(new ItemStack(Material.DISPENSER), AbilityRarity.LEGENDARY);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = Gamer.getGamer(p);
		if (hasAbility(p) && (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) && event.getRightClicked() instanceof Player && p.getPassenger() == null && !p.isInsideVehicle() && event.getRightClicked().getPassenger() == null && !event.getRightClicked().isInsideVehicle()) {
			if (GameStage.isInvincibility(GameMain.getPlugin().getGameStage())) {
				return;
			}
			
			if (gamer.isNotPlaying())
				return;
			
			if (CooldownAPI.hasCooldown(p.getUniqueId(), getName())) {
				p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
				p.sendMessage(CooldownAPI.getCooldownFormated(p.getUniqueId(), getName()));
				return;
			}
			
			p.setPassenger(event.getRightClicked());
			CooldownAPI.addCooldown(p.getUniqueId(), getName(), getOption(p, "COOLDOWN").getValue());
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 0;
	}

}
