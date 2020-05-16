package tk.yallandev.saintmc.common.account;

import lombok.Getter;

@Getter
public enum League {
	
	UNRANKED("§f", "-", "Unranked", 500),
    IRON("§8", "⚌", "Ferro", 1800),
    BRONZE("§e", "\u2733", "Bronze", 2000),
    SILVER("§f", "✠", "Silver", 2200),
    GOLD("§6", "✜", "Gold", 2500),
    PLATINUM("§3", "✥", "Platinum", 3000),
    DIAMOND("§b", "✦", "Diamond", 3300),
    EMERALD("§2", "✥", "Emerald", 4000),
    CRYSTAL("§b", "❉", "Crystal", 4200),
    MASTER("§5", "❁", "Master", 4500),
    GRAND_MASTER("§c", "✹", "Grand Master", 5000),
    CHALLENGER("§4", "✫", "Challenger", Integer.MAX_VALUE);
	
	private String color;
	private String symbol;
	private String name;
	private int maxXp;
	
	private League(String color, String symbol, String name, int maxXp) {
		this.symbol = symbol;
		this.color = color;
		this.name = name;
		this.maxXp = maxXp;
	}
	
    public League getNextLeague() {
        return ordinal() + 1 <= CHALLENGER.ordinal() ? League.values()[ordinal() + 1] : CHALLENGER;
    }

    public League getPreviousLeague() {
        return ordinal() - 1 >= 0 ? League.values()[ordinal() - 1] : UNRANKED;
    }

}
