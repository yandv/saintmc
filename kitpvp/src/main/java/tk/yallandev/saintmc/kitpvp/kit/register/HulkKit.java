package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class HulkKit extends Kit {

	public HulkKit() {
		super("Hulk", "Pegue seus inimigos em suas costas e lançe-os para longe", Material.SADDLE, new ArrayList<>());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player))
			return;	
		
		if (GameMain.getInstance().getGamerManager().getGamer(event.getRightClicked().getUniqueId()).isSpawnProtection())
			return;
		
		Player p = event.getPlayer();
		
		if (!hasAbility(p))
			return;
		
		if (p.getPassenger() != null)
			return;
		
		if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
			if (CooldownController.getInstance().hasCooldown(p, getName())) {
//				p.sendMessage(GameMain.getPlugin().getCooldownManager().getCooldownFormated(p.getUniqueId(), getName()));
				return;
			}
			
			p.setPassenger(event.getRightClicked());
			CooldownController.getInstance().addCooldown(p, new Cooldown(getName(), 12l));
		}
	}

}
