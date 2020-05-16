package tk.yallandev.saintmc.game.ability;

import net.md_5.bungee.api.ChatColor;

public enum AbilityRarity {
	
	MYSTIC(ChatColor.DARK_RED), //
	LEGENDARY(ChatColor.RED), //
	EPIC(ChatColor.DARK_PURPLE), //
	RARE(ChatColor.GOLD), //
	COMMON(ChatColor.RESET);

	private ChatColor color;

	private AbilityRarity(ChatColor color) {
		this.color = color;
	}

	public ChatColor getColor() {
		return color;
	}
}
