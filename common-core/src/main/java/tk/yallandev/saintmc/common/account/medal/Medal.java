package tk.yallandev.saintmc.common.account.medal;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Medal {

	NONE,
	EARLY_ACCESS("Acesso antecipado",
			"Essa medalha foi distribuída para aqueles que entraram na primeira fase do servidor, não pode mais ser adquirida em nenhum lugar.",
			MedalRarity.COMMON, ChatColor.GOLD, '⌚', new String[] { "early", "access", "antecipado" }),
	BUG("Bug", "Medalha para jogadores que reportaram bugs no servidor", MedalRarity.RARE, ChatColor.YELLOW, '☭'),
	BETA("Beta", "Medalha para jogadores que compraram BETA na fase inicial", MedalRarity.EPIC, ChatColor.BLUE, 'Ⓑ'),
	BOOSTER("Booster", "Medalha para jogadores que ajudaram o discord doando BOOST", MedalRarity.VERY_RARE,
			ChatColor.LIGHT_PURPLE, 'β'),
	SUGGESTION("Sugestão", "Medalha para jogadores que deram uma sugestão para o servidor", MedalRarity.RARE,
			ChatColor.DARK_BLUE, 'ⓢ'),
	BDF("Bdf", "Medalha simbolica: Para jogadores que possuem BDF no nick!", MedalRarity.RARE, ChatColor.DARK_BLUE,
			'\u2622'),

	YING_YANG("Ying Yang", "Medalha simbolica: Ying Yang", MedalRarity.EPIC, ChatColor.DARK_AQUA, '☯',
			new String[] { "ying", "yang", "ying-yang" }),
	TOXIC("Tóxico", "Medalha simbolica: Tóxico", MedalRarity.EPIC, ChatColor.GREEN, '☣', new String[] { "toxico" }),
	HEART("Coração", "Medalha simbolica: Coração", MedalRarity.EPIC, ChatColor.RED, '\u2764',
			new String[] { "coracao" });

	private String medalName;
	private String medalDescription;
	private MedalRarity medalRarity;

	private ChatColor chatColor;
	private char medalIcon;
	private String[] aliases;

	Medal(String medalName, String medalDescription, MedalRarity medalRarity, ChatColor chatColor, char medalIcon) {
		this.medalName = medalName;
		this.medalDescription = medalDescription;
		this.medalRarity = medalRarity;
		this.medalIcon = medalIcon;
		this.chatColor = chatColor;
		this.aliases = new String[] {};
	}

	private static final Map<String, Medal> MAP;

	static {
		MAP = new HashMap<>();

		for (Medal medal : values()) {

			MAP.put(medal.name().toLowerCase(), medal);

			if (medal.aliases != null)
				for (String aliases : medal.aliases)
					MAP.put(aliases.toLowerCase(), medal);
		}
	}

	public static Medal getMedalByName(String medalName) {
		return MAP.get(medalName.toLowerCase());
	}
}
