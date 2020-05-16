package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.GameMain;
import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.constructor.Gamer;
import br.com.battlebits.game.interfaces.Disableable;
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
