package tk.yallandev.saintmc.common.tag;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class TagWrapper extends Tag {
	
	private String name;
	
	private String prefix;
	private Group groupToUse;
	private boolean exclusive;
	
	private int id;
	
	public TagWrapper(String name, String prefix, Group groupToUse, boolean exclusive, int id) {
		this.name = name;
		this.prefix = prefix;
		this.groupToUse = groupToUse;
		this.exclusive = exclusive;
		this.id = id;
	}

	public TagWrapper(String prefix, Group groupToUse, boolean exclusive, int id) {
		this.name = ChatColor.stripColor(prefix);
		this.prefix = prefix;
		this.groupToUse = groupToUse;
		this.exclusive = exclusive;
		this.id = id;
	}

}
