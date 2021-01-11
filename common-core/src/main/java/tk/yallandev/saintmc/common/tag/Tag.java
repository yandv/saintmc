package tk.yallandev.saintmc.common.tag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.permission.Group;

/**
 * 
 * Fake Enum class to can add Tags in server
 * 
 * @author yandv
 *
 */

public abstract class Tag {

	public static final Tag DONO = TagWrapper.create("§4§lDONO§4", Group.DONO);
	public static final Tag DEV = TagWrapper.create("§3§lDEVELOPER§3", Group.DONO);
	public static final Tag ADMIN = TagWrapper.create("§c§lADMIN§c", Group.ADMIN);
	public static final Tag MODPLUS = TagWrapper.create("§5§LMOD+§5", Group.MODPLUS);
	public static final Tag MODGC = TagWrapper.create("§5§LMODGC§5", Group.MODGC);
	public static final Tag MOD = TagWrapper.create("§5§lMOD§5", Group.MOD);
	public static final Tag AJUDANTE = TagWrapper.create("§e§lAJUDANTE§e", Group.AJUDANTE);
	public static final Tag BUILDER = TagWrapper.create("§2§lBUILDER§2", Group.BUILDER, true);
	public static final Tag YOUTUBERPLUS = TagWrapper.create("§3§lYT+§3", Group.YOUTUBERPLUS, true);
	public static final Tag YOUTUBER = TagWrapper.create("§b§lYT§b",
			Arrays.asList(Group.YOUTUBER, Group.YOUTUBERPLUS), true);
	public static final Tag PARTNER = TagWrapper.create("§b§lPARTNER§b",
			Arrays.asList(Group.YOUTUBER, Group.YOUTUBERPLUS, Group.PARTNER), true);
	public static final Tag STREAMER = TagWrapper.create("§3§lSTREAMER§3", Group.STREAMER, true);
	public static final Tag BETA = TagWrapper.create("§1§lBETA§1", Group.BETA, true);
	public static final Tag NORD = TagWrapper.create("§6§lNORD§6", Group.NORD);
	public static final Tag ELITE = TagWrapper.create("§c§lELITE§c", Group.ELITE);
	public static final Tag PRO = TagWrapper.create("§e§lPRO§e", Group.PRO);

	public static final Tag COPA = TagWrapper.create("§e§lCOPA§e", null);
	public static final Tag WARRIOR = TagWrapper.create("§7§lWARRIOR§7", null);
	public static final Tag T2021 = TagWrapper.create("§b§l2021§b", null);
	public static final Tag T2020 = TagWrapper.create("§b§l2020§b", null);

	public static final Tag MEMBRO = TagWrapper.create("MEMBRO", "§7", Group.MEMBRO);

	public int ordinal() {
		return getId();
	}

	@Getter
	private boolean custom;

	public abstract String getPrefix();

	public abstract List<Group> getGroupToUse();

	public abstract boolean isExclusive();

	public abstract String getName();

	public abstract int getId();

	public abstract boolean isChroma();

	public abstract Tag setChroma(boolean chroma);

	public abstract Tag clone();

	public Tag setCustom(boolean custom) {
		this.custom = custom;
		return this;
	}

	public Group getDefaultGroup() {
		return getGroupToUse().stream().findFirst().orElse(null);
	}

	/*
	 * Static
	 */

	private static final Map<String, Tag> TAG_MAP;

	static {
		Map<String, Tag> map = new LinkedHashMap<>();

		int ordinal = 0;

		for (Field field : Tag.class.getFields()) {
			if (field.getType() == Tag.class) {
				try {
					Tag tag = (Tag) field.get(null);

					map.put(field.getName().toLowerCase(), tag);
					map.put(tag.getName().toLowerCase(), tag);

					String prefix = ChatColor.stripColor(tag.getPrefix());

					if (!map.containsKey(prefix.toLowerCase()))
						map.put(prefix.toLowerCase(), tag);

					if (tag instanceof TagWrapper)
						((TagWrapper) tag).setId(ordinal);

					ordinal++;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		TAG_MAP = map;
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

	/**
	 * 
	 * Create and add tag to map To get the created tag use getByName method
	 * 
	 * @param tag
	 */

	public static void registerTag(Tag tag) {
		if (TAG_MAP.containsKey(tag.getName().toLowerCase()))
			throw new IllegalStateException("The tag " + tag.getName() + " already exist!");

		if (TAG_MAP.containsValue(tag))
			throw new IllegalStateException("The tag " + tag.getName() + " already exist!");

		TAG_MAP.put(tag.getName().toLowerCase(), tag);
		CommonGeneral.getInstance().debug("The tag " + tag.getName() + " has been registered!");
	}

	public static Collection<Tag> values() {
		List<Tag> list = new ArrayList<>();

		for (Tag tag : TAG_MAP.values())
			if (!list.contains(tag))
				list.add(tag);

		return list;
	}

	public static Tag valueOf(String name) {
		return TAG_MAP.containsKey(name.toLowerCase()) ? TAG_MAP.get(name.toLowerCase()) : null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Tag))
			return false;

		Tag tag = (Tag) obj;

		return tag.ordinal() == ordinal() && tag.getPrefix().equals(getPrefix())
				&& tag.getDefaultGroup() == this.getDefaultGroup() && tag.isChroma() == this.isChroma();
	}

}
