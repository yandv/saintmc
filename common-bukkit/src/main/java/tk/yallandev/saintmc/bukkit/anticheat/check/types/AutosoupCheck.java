package tk.yallandev.saintmc.bukkit.anticheat.check.types;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.anticheat.check.Check;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class AutosoupCheck extends Check {
	
	@Override
	public void verify(Player player, FutureCallback<CheckLevel> callback) {
		super.verify(player, callback);
	}
	
}
