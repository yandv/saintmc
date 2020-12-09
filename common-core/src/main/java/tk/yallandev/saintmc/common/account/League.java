package tk.yallandev.saintmc.common.account;

import lombok.Getter;

@Getter
public enum League {

	INICIANTE("§f", "-", "Iniciante", 1000), CASUAL("§a", "✫", "Casual", 2500), ADVANCED("§a", "✠", "Avançado", 3000),
	EXPERIENTE("§a", "✤", "Experiente", 7000), PROFISSIONAL("§6", "⍟", "Profissional", 9000),
	VICIADO("§6", "✶", "Viciado", 15000), LENDA("§6", "✯", "Lenda", 25000), CLOUTH("§4", "✵", "Clouth", 50000);

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
		return ordinal() + 1 <= CLOUTH.ordinal() ? League.values()[ordinal() + 1] : CLOUTH;
	}

	public League getPreviousLeague() {
		return ordinal() - 1 >= 0 ? League.values()[ordinal() - 1] : INICIANTE;
	}

}
