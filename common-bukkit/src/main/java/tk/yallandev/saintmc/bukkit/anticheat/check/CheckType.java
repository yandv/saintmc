package tk.yallandev.saintmc.bukkit.anticheat.check;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.anticheat.check.Check.CheckLevel;
import tk.yallandev.saintmc.bukkit.anticheat.check.types.AutoclickCheck;
import tk.yallandev.saintmc.bukkit.anticheat.check.types.AutosoupCheck;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

@Getter
public enum CheckType {
	
	AUTOSOUP(new AutosoupCheck()) {},
	AUTOCLICK(new AutoclickCheck());
	
	private Check check;
	private String name;
	
	private CheckType(Check check) {
		this.check = check;
		this.name = check.getClass().getSimpleName().replace("Check", "");
	}
	
	public void check(Player player, FutureCallback<CheckLevel> futureCallback) {
		this.check.verify(player, futureCallback);
	}
	
}
