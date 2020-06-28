package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.enums.ClanDisplayType;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.tag.Tag;

@Getter
public class PlayerChangeTagEvent extends PlayerCancellableEvent {

	private Member member;

	private Tag oldTag;
	@Setter
	private Tag newTag;
	private boolean forced;

	public PlayerChangeTagEvent(Player player, Member member, Tag oldTag, Tag newTag, boolean forced) {
		super(player);
		this.member = member;
		this.oldTag = oldTag;
		this.newTag = newTag;
		this.forced = forced;
	}

	public boolean isClanTag() {
		if (member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.ALL
				|| (CommonGeneral.getInstance().getServerType() == ServerType.LOBBY
						&& member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.LOBBY))
			return member.getClan() != null;
		return false;
	}

}
