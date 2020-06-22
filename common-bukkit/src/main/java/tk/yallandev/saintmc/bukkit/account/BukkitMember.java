package tk.yallandev.saintmc.bukkit.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeTagEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;

@Getter
public class BukkitMember extends Member {

	@Setter
	private transient Player player;
	private transient List<Tag> tags;
	private transient Scoreboard scoreboard;
	@Setter
	private transient boolean buildEnabled;

	@Setter
	private transient boolean cacheOnQuit;
	@Setter
	private transient UUID lastTell;

	public BukkitMember(MemberModel memberModel) {
		super(memberModel);
	}

	public BukkitMember(String playerName, UUID uniqueId) {
		super(playerName, uniqueId);
	}

	@Override
	public void setJoinData(String playerName, String hostString) {
		super.setJoinData(playerName, hostString);
		loadTags();
	}

	@Override
	public void sendMessage(String message) {
		if (player != null)
			player.sendMessage(message);
	}

	@Override
	public void sendMessage(BaseComponent message) {
		if (player != null)
			player.spigot().sendMessage(message);
	}

	@Override
	public void sendMessage(BaseComponent[] message) {
		if (player != null)
			player.spigot().sendMessage(message);
	}

	public void setScoreboard(Scoreboard scoreboard) {
		if (this.scoreboard == null || this.scoreboard != scoreboard) {
			this.scoreboard = scoreboard;
			this.scoreboard.createScoreboard(getPlayer());
		}
	}

	@Override
	public boolean setTag(Tag tag) {
		if (!BukkitMain.getInstance().isTagControl())
			return false;

		return setTag(tag, false);
	}

	public boolean setTag(Tag tag, boolean forcetag) {
		if (!tags.contains(tag) && !forcetag) {
			tag = getDefaultTag();
		}

		PlayerChangeTagEvent event = new PlayerChangeTagEvent(player, getTag(), tag, forcetag);
		BukkitMain.getInstance().getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			if (!forcetag)
				super.setTag(tag);
		}

		return !event.isCancelled();
	}

	@Override
	public void setXp(int xp) {
//		League nextLeague = getLeague();
		super.setXp(xp);
		
		
		if (xp >= getLeague().getMaxXp()) {
			setLeague(getLeague().getNextLeague());
		} else if (getLeague() != League.UNRANKED) {
			if (xp < getLeague().getPreviousLeague().getMaxXp()) {
				setLeague(getLeague().getPreviousLeague());
			}
		}
		
//		if (getXp() >= getLeague().getMaxXp()) {
//			xp = getXp() - getLeague().getMaxXp();
//			nextLeague = getLeague().getNextLeague();
//		} else if (getXp() < 0) {
//			nextLeague = getLeague().getPreviousLeague();
//
//			if (nextLeague == League.UNRANKED) {
//				xp = 0;
//				super.setXp(0);
//			} else
//				xp = nextLeague.getMaxXp() + getXp();
//		}
//
//		if (nextLeague != getLeague()) {
//			setLeague(nextLeague);
//			setXp(xp);
//		}
	}

	@Override
	public void setLeague(League liga) {
		PlayerChangeLeagueEvent event = new PlayerChangeLeagueEvent(getPlayer(), this, getLeague(), liga);
		BukkitMain.getInstance().getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			super.setLeague(liga);
			setTag(getTag());
		}
	}

	public void loadTags() {
		tags = new ArrayList<>();
		for (Tag tag : Tag.values()) {

			if (hasPermission("tag." + tag.getName().toLowerCase())) {
				tags.add(tag);
				continue;
			}

			if (tag.getGroupToUse() == null)
				continue;

			if (tag.isExclusive()) {
				if (tag.getGroupToUse() == getServerGroup() || hasRank(tag.getGroupToUse())
						|| getServerGroup().ordinal() >= Group.ADMIN.ordinal()) {
					tags.add(tag);
				}
				continue;
			}

			if (getServerGroup().ordinal() >= tag.getGroupToUse().ordinal())
				tags.add(tag);
		}

	}

	public Tag getDefaultTag() {
		return tags.get(0);
	}

	public List<Tag> getTags() {
		return tags;
	}

	public boolean hasTag(Tag tag) {
		if (tags.contains(tag))
			return true;

		for (Tag t : tags) {
			if (t.getName().equals(tag.getName()))
				return true;
		}

		return false;
	}

}