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
import tk.yallandev.saintmc.common.utils.string.StringUtils;

/**
 * Fake Enum class to can add Tags in server
 *
 * @author yandv
 */

public abstract class Tag {

    public static final Tag ADMIN = TagWrapper.create("§4§lADMIN§4", Group.ADMIN);

    public static final Tag MODPLUS = TagWrapper.create("Mod+", "§5§lMOD+§5", Group.MODPLUS);

    public static final Tag MOD = TagWrapper.create("Mod", "§5§lMOD§5", Group.MOD);

    public static final Tag TRIAL = TagWrapper.create("Trial", "§5§lTRIAL§5", Group.TRIAL);

    public static final Tag YOUTUBERPLUS = TagWrapper.create("Plus", "§3§lPLUS§3", Group.YOUTUBERPLUS, true);

    public static final Tag YOUTUBER = TagWrapper.create("§b§lYOUTUBER§b", Group.YOUTUBER, true);

    public static final Tag STREAMER = TagWrapper.create("§b§lSTREAMER§b", Group.STREAMER, true);
    public static final Tag PARTNER = TagWrapper.create("§b§lPARTNER§b", Group.PARTNER, true);

    public static final Tag CHAMPION = TagWrapper.create("§6§lCHAMPION§6", Group.ADMIN, true);

    public static final Tag BETA = TagWrapper.create("§1§lBETA§1", Group.BETA, true);

    public static final Tag T2023 = TagWrapper.create("2023", "§b§l2023§b", Group.ADMIN, true);

    public static final Tag NATAL = TagWrapper.create("Natal", "§c§lNATAL§c", Group.ADMIN, true);

    public static final Tag FERIAS = TagWrapper.create("§a§lFERIAS§a", Group.ADMIN, true);

    public static final Tag PENTA = TagWrapper.create("§d§lPENTA§d", Group.PENTA);

    public static final Tag VIP = TagWrapper.create("Vip", "§a§lVIP§a", Group.VIP);

    public static final Tag MEMBRO = TagWrapper.create("Membro", "§7", Group.MEMBRO);

    public int ordinal() {
        return getId();
    }

    @Getter
    private boolean custom;

    public String getStrippedTag() {
        String lastColor = StringUtils.getLastColors(getPrefix());
        String stripColor = StringUtils.formatString(ChatColor.stripColor(getPrefix()));

        return lastColor + stripColor;
    }

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

                    if (!map.containsKey(prefix.toLowerCase())) {
                        map.put(prefix.toLowerCase(), tag);
                    }

                    if (tag instanceof TagWrapper) {
                        ((TagWrapper) tag).setId(ordinal);
                    }

                    ordinal++;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        TAG_MAP = map;
    }

    /**
     * @param name String
     * @return Tag
     */
    public static Tag getByName(String name) {
        Objects.requireNonNull(name, "Parameter 'name' is null.");
        return TAG_MAP.get(name.toLowerCase());
    }

    /**
     * Create and add tag to map To get the created tag use getByName method
     *
     * @param tag
     */

    public static void registerTag(Tag tag) {
        if (TAG_MAP.containsKey(tag.getName().toLowerCase())) {
            throw new IllegalStateException("The tag " + tag.getName() + " already exist!");
        }

        if (TAG_MAP.containsValue(tag)) {
            throw new IllegalStateException("The tag " + tag.getName() + " already exist!");
        }

        TAG_MAP.put(tag.getName().toLowerCase(), tag);
        CommonGeneral.getInstance().debug("The tag " + tag.getName() + " has been registered!");
    }

    public static Collection<Tag> values() {
        List<Tag> list = new ArrayList<>();

        for (Tag tag : TAG_MAP.values())
            if (!list.contains(tag)) {
                list.add(tag);
            }

        return list;
    }

    public static Tag valueOf(String name) {
        return TAG_MAP.containsKey(name.toLowerCase()) ? TAG_MAP.get(name.toLowerCase()) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Tag)) {
            return false;
        }

        Tag tag = (Tag) obj;

        return tag.ordinal() == ordinal() && tag.getPrefix().equals(getPrefix())
               && tag.getDefaultGroup() == this.getDefaultGroup() && tag.isChroma() == this.isChroma();
    }

}
