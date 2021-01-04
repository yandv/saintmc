package tk.yallandev.saintmc.common.account;

import lombok.Getter;

@Getter
public enum League {

	IRON("§7", "✧", "Iron I", 1000), IRON_II("§7", "✧", "Iron II", 2000), IRON_III("§7", "✧", "Iron III", 4000),
	IRON_IV("§7", "✧", "Iron IV", 5000), /* iron */

	GOLD("§6", "✱", "Gold I", 6000), GOLD_II("§6", "✱", "Gold II", 8000), GOLD_III("§6", "✱", "Gold III", 10000),
	GOLD_IV("§6", "✱", "Gold IV", 15000), /* gold */

	SILVER("§f", "✠", "Silver I", 17000), SILVER_II("§f", "✠", "Silver II", 19000),
	SILVER_III("§f", "✠", "Silver III", 23000), SILVER_IV("§f", "✠", "Silver IV", 28000), /* silver */

	PLATINUM("§8", "✇", "Platinum I", 31000), PLATINUM_II("§8", "✇", "Platinum II", 35000),
	PLATINUM_III(
			"§8", "✇", "Platinum III", 41000), PLATINUM_IV("§8", "✇", "Platinum IV", 48000), /* platinum */

	DIAMOND("§3", "✪", "Diamond I", 55000), DIAMOND_II("§3", "✪", "Diamond II", 63000),
	DIAMOND_III("§3", "✪", "Diamond III", 69000), /* diamond */

	MASTER("§c", "✇", "Master I", 73000), MASTER_II("§c", "✇", "Master II", 79000),
	MASTER_III("§c", "✇", "Master III", 85000), /* master */

	MYTH("§e", "✺", "Myth I", 95000), MYTH_I("§e", "✺", "Myth II", 150000), /* myth */

	LEGEND("§4", "✟", "Legend", Integer.MAX_VALUE);

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
		return ordinal() + 1 <= values()[values().length - 1].ordinal() ? League.values()[ordinal() + 1]
				: values()[values().length - 1];
	}

	public League getPreviousLeague() {
		return ordinal() - 1 >= 0 ? values()[ordinal() - 1] : values()[0];
	}

}
