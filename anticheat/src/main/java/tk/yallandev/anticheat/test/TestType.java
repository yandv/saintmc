package tk.yallandev.anticheat.test;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.anticheat.test.Test.CheckLevel;
import tk.yallandev.anticheat.test.types.AutoclickTest;
import tk.yallandev.anticheat.test.types.AutosoupTest;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

@Getter
public enum TestType {
	
	AUTOSOUP(new AutosoupTest()) {},
	AUTOCLICK(new AutoclickTest());
	
	private Test check;
	private String name;
	
	private TestType(Test check) {
		this.check = check;
		this.name = check.getClass().getSimpleName().replace("Check", "");
	}
	
	public void check(Player player, FutureCallback<CheckLevel> futureCallback) {
		this.check.verify(player, futureCallback);
	}
	
}
