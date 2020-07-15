package net.saintmc.anticheat.alert;

import org.bukkit.entity.Player;

public interface AlertController {
	
	void alert(Player player, Alert alert);
	
	void autoban(Player player, long time);

}
