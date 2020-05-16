package tk.yallandev.saintmc.game.interfaces;

import tk.yallandev.saintmc.game.constructor.CustomOption;

public interface Optional {
	
	public CustomOption getOption(String abilityName, String optionName);

	public void setOption(String abilityName, String optionName, CustomOption value);
	
}
