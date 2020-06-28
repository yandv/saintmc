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
	BUG_HUNTER("Bug HUnter", "blalalblad", MedalRarity.COMMON, ChatColor.DARK_BLUE,
			'⚒'),

	HEART("Coração do Amor", "asdkaoiweja", MedalRarity.RARE, ChatColor.DARK_RED,
			'⚒'),// p atualizar as medalhas tem q buildar o common-core e dps o common-bungee e common-bukkit e atualizar os plugins

	TOXIC("Tóxico", "blalalblad", MedalRarity.COMMON, ChatColor.YELLOW,
			'☣'),// so mexer ai, além disso mais algo?

	RAY("Raio Dourado", "blalalblad", MedalRarity.RARE, ChatColor.GOLD,
			'⚡'),

	TOP("TOP", "Medalha de TOP 1", MedalRarity.COMMON, ChatColor.AQUA,
			'❂');

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
