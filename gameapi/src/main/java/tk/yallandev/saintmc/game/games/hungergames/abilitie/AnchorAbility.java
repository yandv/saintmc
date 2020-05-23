package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.stage.GameStage;

public class AnchorAbility extends Ability implements Disableable {

	public AnchorAbility() {
		super(new ItemStack(Material.ANVIL), AbilityRarity.EPIC);
	}

	@EventHandler
	public void onPlayerDamagePlayerListener(EntityDamageByEntityEvent e) {
		if (GameMain.getPlugin().getGameStage() != GameStage.GAMETIME)
			return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
			if (e.getDamager() instanceof Player) {
				Player d = (Player) e.getDamager();
				
				if (hasAbility(p) || hasAbility(d)) {
					if (!Gamer.getGamer(p).isNotPlaying() && !Gamer.getGamer(d).isNotPlaying()) {
						p.getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);
						if (e.getDamage() < ((Damageable) p).getHealth()) {
							e.setCancelled(true);
							p.damage(e.getFinalDamage());
						}
					}
				}
			}
		}
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 30;
	}
}
