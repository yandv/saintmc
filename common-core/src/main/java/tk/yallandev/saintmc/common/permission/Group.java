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

	MEMBRO, TORNEIO, DONATOR, LIGHT, BLIZZARD, SAINT, CREATOR, BETA, YOUTUBER, DESIGNER, BUILDER, HELPER,
	YOUTUBERPLUS(new StreamerGroup()), STREAMER(new StreamerGroup()), TRIAL(new ModeratorGroup()),
	MOD(new ModeratorGroup()), MODGC(new ModeratorGroup()), MODPLUS(new ModeratorGroup()), ADMIN(new ModeratorGroup()),
	GERENTE(new OwnerGroup()), DIRETOR(new OwnerGroup()), DEVELOPER(new OwnerGroup()), DONO(new OwnerGroup());

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
