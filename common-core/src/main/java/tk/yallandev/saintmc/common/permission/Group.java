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

	MEMBRO, PRO, EXTREME, ULTIMATE, NITRO, BETA, STREAMER, YOUTUBER, YOUTUBERPLUS(new StreamerGroup()), DESIGNER,
	BUILDER, TRIAL(new ModeratorGroup()), MOD(new ModeratorGroup()), INVEST(new ModeratorGroup()),
	MODPLUS(new ModeratorGroup()), INVESTPLUS(new ModeratorGroup()), DEVELOPER(new OwnerGroup()),
	ADMIN(new ModeratorGroup());

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
