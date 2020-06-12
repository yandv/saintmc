package tk.yallandev.saintmc.common.tag;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class TagWrapper extends Tag {
	
	private String name;
	
	private String prefix;
	private Group groupToUse;
	private boolean exclusive;
	
	/**
	 * The id is only order the tab in Bukkit
	 */
	
	@Setter
	private int id;
	private boolean chroma;
	
	public TagWrapper(String name, String prefix, Group groupToUse, boolean exclusive, int id) {
		this.name = name;
		this.prefix = prefix;
		this.groupToUse = groupToUse;
		this.exclusive = exclusive;
		this.id = id;
	}
	
	public TagWrapper(String name, String prefix, Group groupToUse, boolean exclusive) {
		this(name, prefix, groupToUse, exclusive, 0);
	}

	public TagWrapper(String prefix, Group groupToUse, boolean exclusive, int id) {
		this(ChatColor.stripColor(prefix), prefix, groupToUse, exclusive, id);
	}
	
	public TagWrapper(String prefix, Group groupToUse, boolean exclusive) {
		this(ChatColor.stripColor(prefix), prefix, groupToUse, exclusive, 0);
	}
	
	@Override
	public Tag setChroma(boolean chroma) {
		this.chroma = chroma;
		return this;
	}
	
	public Tag clone() {
		return new TagWrapper(name, prefix, groupToUse, exclusive, id);
	}
	
	public static TagWrapper create(String tagName, String prefix, Group groupToUse) {
		return new TagWrapper(tagName, prefix, groupToUse, false);
	}
	
	public static TagWrapper create(String prefix, Group groupToUse) {
		return new TagWrapper(prefix, groupToUse, false);
	}
	
	public static TagWrapper create(String prefix, Group groupToUse, boolean exclusive) {
		return new TagWrapper(prefix, groupToUse, exclusive);
	}
	
	public static TagWrapper create(String tagName, String prefix, Group groupToUse, boolean exclusive) {
		return new TagWrapper(tagName, prefix, groupToUse, exclusive);
	}

}
