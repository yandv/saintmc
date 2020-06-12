package tk.yallandev.saintmc.bukkit.api.tag;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.common.tag.Tag;

@Getter
public class Chroma {

	private String teamId;

	private String defaultPrefix;
	private String prefix;
	private boolean onlyTag;

	private String suffix;

	public Chroma(String teamId, String prefix, String suffix) {
		this.teamId = teamId;
		this.defaultPrefix = ChatColor.stripColor(prefix).trim();
		this.onlyTag = defaultPrefix.length() > 0 ? false : true;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String getPrefix(char color) {
		return onlyTag ? "§" + color : "§" + color + "§l" + defaultPrefix + "§" + color + " ";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chroma) {
			Chroma chroma = (Chroma) obj;

			return chroma.prefix.equals(prefix) && chroma.getPrefix().equals(prefix)
					&& chroma.getSuffix().equals(suffix);
		}

		if (obj instanceof Tag) {
			Tag tag = (Tag) obj;

			return ChatColor.stripColor(tag.getPrefix()).trim().equals(defaultPrefix);
		}

		return false;
	}
}