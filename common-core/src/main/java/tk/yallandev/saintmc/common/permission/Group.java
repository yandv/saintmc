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
 *         In this plugin
 *
 */

public enum Group {

	MEMBRO, DONATOR, LIGHT, BLIZZARD, CREATOR, SAINT, BETA, YOUTUBER, DESIGNER, BUILDER, HELPER,
	YOUTUBERPLUS(new StreamerGroup()), STREAMER(new StreamerGroup()), TRIAL(new ModeratorGroup()), MOD(new ModeratorGroup()),
	MODGC(new ModeratorGroup()), MODPLUS(new ModeratorGroup()), GERENTE(new ModeratorGroup()), ADMIN(new OwnerGroup()),
	DIRETOR(new OwnerGroup()), DEV(new OwnerGroup()), DONO(new OwnerGroup());

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
