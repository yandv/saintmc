package tk.yallandev.anticheat.test.types;

import org.bukkit.entity.Player;

import tk.yallandev.anticheat.test.Test;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class AutosoupTest extends Test {
	
	@Override
	public void verify(Player player, FutureCallback<CheckLevel> callback) {
		super.verify(player, callback);
	}
	
}
