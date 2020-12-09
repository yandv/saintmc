package tk.yallandev.saintmc.skwyars.game.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.game.SkywarsType;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

@Getter
public class Team {

	private UUID uniqueId;
	private List<Gamer> gamerList;

	public Team(Gamer gamer, UUID uniqueId) {
		this.uniqueId = uniqueId;
		this.gamerList = new ArrayList<>();
		this.gamerList.add(gamer);
		gamer.setTeam(this);
	}

	public int getMembers() {
		return gamerList.size();
	}

	public boolean isInTeam(Gamer gamer) {
		return gamerList.contains(gamer);
	}

	public void removeTeam(Gamer gamer) {
		if (gamerList.contains(gamer))
			gamerList.remove(gamer);
	}

	public void addTeam(Gamer gamer) {
		if (!gamerList.contains(gamer))
			gamerList.add(gamer);
	}

	public boolean isAlive() {
		for (Gamer gamer : gamerList)
			if (gamer.isPlaying())
				return true;

		return false;
	}

	public boolean isFull() {
		return gamerList.size() >= (GameMain.getInstance().getSkywarsType() == SkywarsType.SOLO ? 1 : 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Team) {
			Team team = (Team) obj;

			return team.getUniqueId().equals(getUniqueId());
		}

		return super.equals(obj);
	}

}
