package net.saintmc.anticheat.alert;

import org.bukkit.entity.Player;

public interface AlertController {
	
	void alert(Player player, Alert alert, int alertIndex);
	
	void autoban(Player player, Alert alert, long time);

}
