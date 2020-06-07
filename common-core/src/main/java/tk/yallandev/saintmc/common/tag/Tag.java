package tk.yallandev.saintmc.common.tag;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.permission.Group;

/**
 * 
 * Apenas um teste, ainda estou aprendendo a lógica de fazer isso Mas serve para
 * criar novas tags e deixar as que já existem estáticas assim como o enum faz
 * 
 * @author yandv
 *
 */

public abstract class Tag {

	public static final Tag DONO = new TagWrapper("§4§lDONO§4", Group.DONO, false, 0);
	public static final Tag DEV = new TagWrapper("§3§lDEV§3", Group.DEV, false, 1);
	public static final Tag DIRETOR = new TagWrapper("§4§LDIRETOR§4", Group.DIRETOR, false, 2);
	public static final Tag ADMIN = new TagWrapper("§c§lADMIN§c", Group.ADMIN, false, 3);
	public static final Tag GERENTE = new TagWrapper("§c§lGERENTE§c", Group.GERENTE, false, 4);
	public static final Tag MODPLUS = new TagWrapper("§5§LMOD+§5", Group.MODPLUS, false, 5);
	public static final Tag MODGC = new TagWrapper("§5§lMODGC§5", Group.MODGC, false, 6);
	public static final Tag MOD = new TagWrapper("§5§lMOD§5", Group.MOD, false, 7);
	public static final Tag TRIAL = new TagWrapper("§d§lTRIAL§d", Group.TRIAL, false, 8);
	public static final Tag YOUTUBERPLUS = new TagWrapper("§3§lYT+§3", Group.YOUTUBERPLUS, false, 9);
	public static final Tag BUILDER = new TagWrapper("§e§lBUILDER§e", Group.BUILDER, false, 10);
	public static final Tag DESIGNER = new TagWrapper("§2§lDESIGNER§2", Group.DESIGNER, false, 11);
	public static final Tag YOUTUBER = new TagWrapper("§b§lYT§b", Group.YOUTUBER, false, 12);
	public static final Tag BETA = new TagWrapper("§1§lBETA§1", Group.BETA, false, 13);
	public static final Tag SAINT = new TagWrapper("§d§lSAINT§d", Group.SAINT, false, 14);
	public static final Tag BLIZZARD = new TagWrapper("§1§lBLIZZARD§1", Group.BLIZZARD, false, 15);
	public static final Tag LIGHT = new TagWrapper("§a§lLIGHT§a", Group.LIGHT, false, 16);
	public static final Tag DONATOR = new TagWrapper("§d§lDONATOR§d", Group.DONATOR, false, 17);
	public static final Tag MEMBRO = new TagWrapper("§7", Group.MEMBRO, false, 18);
	public static final Tag LOGANDO = new TagWrapper("§8§lLOGANDO§8", null, false, 19);
	public static final Tag RDM = new TagWrapper("§6§lRDM§6", null, false, 20);

	public int ordinal() {
		return getId();
	}

	public static void main(String[] args) {
		for (Tag tag : values()) {
			System.out.println(CommonConst.GSON.toJson(tag));
			System.out.println(tag.getPrefix());
		}
	}

	private static final Map<String, Tag> TAG_MAP;

	static {
		Map<String, Tag> map = new HashMap<>();

		for (Field field : Tag.class.getFields()) {
			if (field.getType() == Tag.class) {
				try {
					Tag tag = (Tag) field.get(null);
					map.put(tag.getName().toLowerCase(), tag);

					String prefix = ChatColor.stripColor(tag.getPrefix());

					if (!map.containsKey(prefix.toLowerCase()))
						map.put(prefix.toLowerCase(), tag);
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

	public static void registerTag(Tag tag) {
		if (TAG_MAP.containsKey(tag.getName()))
			throw new IllegalStateException("The tag " + tag.getName() + " already exist!");
		
		if (TAG_MAP.containsValue(tag))
			throw new IllegalStateException("The tag " + tag + " already exist!");
		
		TAG_MAP.put(tag.getName(), tag);
	}
	
	public static Collection<Tag> values() {
		return TAG_MAP.values();
	}
	
	public static Tag valueOf(String name) {
		return TAG_MAP.containsKey(name.toLowerCase()) ? TAG_MAP.get(name.toLowerCase()) : null;
	}

	public abstract String getPrefix();

	public abstract Group getGroupToUse();

	public abstract boolean isExclusive();
	
	public abstract String getName();
	
	public abstract int getId();

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Tag))
			return false;

		Tag tag = (Tag) obj;

		return tag.ordinal() == ordinal() && tag.getPrefix().equals(getPrefix())
				&& tag.getGroupToUse() == this.getGroupToUse();
	}

}
