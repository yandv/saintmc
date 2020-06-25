package tk.yallandev.saintmc.common.tag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.md_5.bungee.api.ChatColor;
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
	public static final Tag DEV = TagWrapper.create("§3§lDEV§3", Group.DEV);
	public static final Tag ESTRELA = TagWrapper.create("§1§lESTRELA§1", Group.DIRETOR);
	public static final Tag DIRETOR = TagWrapper.create("§4§LDIRETOR§4", Group.DIRETOR);
	public static final Tag ADMIN = TagWrapper.create("§c§lADMIN§c", Group.ADMIN);
	public static final Tag GERENTE = TagWrapper.create("§c§lGERENTE§c", Group.GERENTE);
	public static final Tag MODPLUS = TagWrapper.create("MODPLUS", "§5§LMOD+§5", Group.MODPLUS);
	public static final Tag MODGC = TagWrapper.create("§5§lMODGC§5", Group.MODGC);
	public static final Tag MOD = TagWrapper.create("§5§lMOD§5", Group.MOD);
	public static final Tag TRIAL = TagWrapper.create("§d§lTRIAL§d", Group.TRIAL);
	public static final Tag YOUTUBERPLUS = TagWrapper.create("YOUTUBERPLUS", "§3§lYT+§3", Group.YOUTUBERPLUS, true);
	public static final Tag HELPER = TagWrapper.create("§9§lHELPER§9", Group.HELPER);
	public static final Tag BUILDER = TagWrapper.create("§e§lBUILDER§e", Group.BUILDER, true);
	public static final Tag DESIGNER = TagWrapper.create("§2§lDESIGNER§2", Group.DESIGNER, true);
	public static final Tag YOUTUBER = TagWrapper.create("YOUTUBER", "§b§lYT§b", Group.YOUTUBER, true);
	public static final Tag BETA = TagWrapper.create("§1§lBETA§1", Group.BETA);
	public static final Tag SAINT = TagWrapper.create("§d§lSAINT§d", Group.SAINT);
	public static final Tag BLIZZARD = TagWrapper.create("§b§lBLIZZARD§b", Group.BLIZZARD);
	public static final Tag LIGHT = TagWrapper.create("§a§lLIGHT§a", Group.LIGHT);
	public static final Tag DONATOR = TagWrapper.create("§d§lDONATOR§d", Group.DONATOR, true);
	public static final Tag MEMBRO = TagWrapper.create("MEMBRO", "§7", Group.MEMBRO);
	public static final Tag RDM = TagWrapper.create("§6§lRDM§6", null);

	public int ordinal() {
		return getId();
	}
	
	public abstract String getPrefix();

	public abstract Group getGroupToUse();

	public abstract boolean isExclusive();

	public abstract String getName();

	public abstract int getId();

	public abstract boolean isChroma();

	public abstract Tag setChroma(boolean chroma);

	public abstract Tag clone();
	
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
		if (TAG_MAP.containsKey(tag.getName()))
			throw new IllegalStateException("The tag " + tag.getName() + " already exist!");

		if (TAG_MAP.containsValue(tag))
			throw new IllegalStateException("The tag " + tag + " already exist!");

		TAG_MAP.put(tag.getName(), tag);
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
				&& tag.getGroupToUse() == this.getGroupToUse() && tag.isChroma() == this.isChroma();
	}

}
