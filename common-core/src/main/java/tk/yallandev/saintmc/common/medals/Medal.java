package tk.yallandev.saintmc.common.medals;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
@AllArgsConstructor
public enum Medal {

	EARLY_ACCESS("Acesso antecipado", "Medalha para jogadores que entraram no servidor na fase inicial", ChatColor.GOLD,
			'⌚', new String[] { "early", "access", "antecipado" }),
	BUG("Bug", "Medalha para jogadores que reportaram bugs no servidor", ChatColor.YELLOW, '☭'),
	BETA("Beta", "Medalha para jogadores que compraram BETA na fase inicial", ChatColor.BLUE, 'Ⓑ'),

	YING_YANG("Desempenho", "Medalha simbolica: Ying Yang", ChatColor.DARK_AQUA, '☯',
			new String[] { "ying", "yang", "ying-yang" }),
	TOXIC("Tóxico", "Medalha simbolica: Tóxico", ChatColor.GREEN, '☢', new String[] { "toxico" }),
	HEART("Coração", "Medalha simbolica: Coração", ChatColor.RED, '\u2764', new String[] { "coracao" });

	private String medalName;
	private String medalDescription;

	private ChatColor chatColor;
	private char medalIcon;
	private String[] aliases;

	Medal(String medalName, String medalDescription, ChatColor chatColor, char medalIcon) {
		this.medalName = medalName;
		this.medalDescription = medalDescription;
		this.medalIcon = medalIcon;
		this.chatColor = chatColor;
		this.aliases = new String[] {};
	}

	private static final Map<String, Medal> MAP;

	static {
		MAP = new HashMap<>();

		for (Medal medal : values()) {

			MAP.put(medal.name().toLowerCase(), medal);

			for (String aliases : medal.aliases)
				MAP.put(aliases.toLowerCase(), medal);
		}
	}

	public static Medal getMedalByName(String medalName) {
		return MAP.get(medalName.toLowerCase());
	}
}
