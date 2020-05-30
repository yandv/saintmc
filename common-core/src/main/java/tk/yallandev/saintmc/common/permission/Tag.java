package tk.yallandev.saintmc.common.permission;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author yandv
 *
 */

public enum Tag {

	DONO("§4§LDONO§4", Group.DONO, false), DEV("§3§lDEV§3", Group.DEV, true),
	DIRETOR("§4§LDIRETOR§4", Group.DIRETOR, false), ADMIN("§c§lADMIN§c", Group.ADMIN, false),
	GERENTE("§c§lGERENTE§c", Group.GERENTE, false), MODPLUS("§5§LMOD+§5", Group.MODPLUS, false),
	MODGC("§5§lMODGC§5", Group.MODGC, true), MOD("§5§lMOD§5", Group.MOD, false),
	TRIAL("§d§lTRIAL§d", Group.TRIAL, false), YOUTUBERPLUS("§3§lYT+§3", Group.YOUTUBERPLUS, true),
	BUILDER("§e§lBUILDER§e", Group.BUILDER, true), DESIGNER("§2§lDESIGNER§2", Group.DESIGNER, true),
	YOUTUBER("§b§lYT§b", Group.YOUTUBER, true), BETA("§1§lBETA§1", Group.BETA, false),
	SAINT("§d§lSAINT§d", Group.SAINT, false), BLIZZARD("§1§lBLIZZARD§1", Group.BLIZZARD, false),
	LIGHT("§a§lLIGHT§a", Group.LIGHT, false), DONATOR("§d§lDONATOR§d", Group.DONATOR, true),
	MEMBRO("§7", Group.MEMBRO, false), LOGANDO("§8§lLOGANDO§8", null, true), RDM("§6§lRDM§6", null, true);

	private String prefix;
	private Group groupToUse;
	private boolean isExclusive;

	Tag(String prefix, Group toUse, boolean exclusive) {
		this.prefix = prefix;
		this.groupToUse = toUse;
		this.isExclusive = exclusive;
	}

	public String getPrefix() {
		return prefix;
	}

	public Group getGroupToUse() {
		return groupToUse;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	private static final Map<String, Tag> TAG_MAP;

	static {
		Map<String, Tag> map = new ConcurrentHashMap<>();

		for (Tag tag : Tag.values()) {
			map.put(tag.name().toLowerCase(), tag);

			String prefix = ChatColor.stripColor(tag.getPrefix());

			if (!map.containsKey(prefix.toLowerCase()))
				map.put(prefix.toLowerCase(), tag);
		}

		TAG_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 *
	 * @param name String
	 * @return Tag
	 */
	public static Tag getByName(String name) {
		Objects.requireNonNull(name, "Parameter 'name' is null.");
		return TAG_MAP.get(name.toLowerCase());
	}

}