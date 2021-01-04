package tk.yallandev.saintmc.common.permission;

import tk.yallandev.saintmc.common.permission.group.GroupInterface;
import tk.yallandev.saintmc.common.permission.group.ModeratorGroup;
import tk.yallandev.saintmc.common.permission.group.OwnerGroup;
import tk.yallandev.saintmc.common.permission.group.SimpleGroup;
import tk.yallandev.saintmc.common.permission.group.StreamerGroup;

/**
 * 
 * @author yandv
 * 
 */

public enum Group {

	MEMBRO, PRO, ELITE, NORD, BETA, STREAMER, PARTNER, YOUTUBER, YOUTUBERPLUS(new StreamerGroup()), BUILDER,
	AJUDANTE(new ModeratorGroup()), MOD(new ModeratorGroup()), MODGC(new ModeratorGroup()),
	MODPLUS(new ModeratorGroup()), ADMIN(new OwnerGroup()), DEVELOPER(new OwnerGroup()),
	DONO(new OwnerGroup());

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

}
