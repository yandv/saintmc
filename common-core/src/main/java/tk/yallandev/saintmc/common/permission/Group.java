package tk.yallandev.saintmc.common.permission;

import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.common.permission.group.GroupInterface;
import tk.yallandev.saintmc.common.permission.group.ModeratorGroup;
import tk.yallandev.saintmc.common.permission.group.OwnerGroup;
import tk.yallandev.saintmc.common.permission.group.SimpleGroup;
import tk.yallandev.saintmc.common.permission.group.StreamerGroup;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

import java.util.Optional;

/**
 * @author yandv
 */

public enum Group {

    MEMBRO,
    VIP,
    PENTA,
    BETA,
    PARTNER,
    STREAMER,
    YOUTUBER,
    YOUTUBERPLUS(new StreamerGroup()),
    TRIAL(new ModeratorGroup()),
    MOD(new ModeratorGroup()),
    MODPLUS(new ModeratorGroup()),
    ADMIN(new OwnerGroup());

    private GroupInterface group;

    Group() {
        this(new SimpleGroup());
    }

    Group(GroupInterface group) {
        this.group = group;
    }

    public GroupInterface getGroup() {
        return group;
    }

    public String getColor() {
        Optional<Tag> optional = Optional.empty();

        try {
            optional = Optional.ofNullable(Tag.valueOf(name()));
        } catch (Exception ignored) {

        }

        return optional.map(Tag::getPrefix).map(StringUtils::getLastColors).orElse(ChatColor.GRAY.toString());
    }
}
