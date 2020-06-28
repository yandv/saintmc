package tk.yallandev.saintmc.skwyars.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import tk.yallandev.saintmc.skwyars.game.team.Team;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

public class TeamController {

	private List<Team> teamList;

	public TeamController() {
		teamList = new ArrayList<>();
	}

	public boolean isTeamFull() {
		return teamList.size() >= 12;
	}

	public void removeTeam(Team team) {
		teamList.remove(team);
	}

	public Team findTeam(Gamer gamer) {
		Team t = null;

		for (Team team : teamList) {
			if (team.isFull())
				continue;

			t = team;
		}

		if (t == null)
			if (isTeamFull())
				return null;
			else {
				t = new Team(gamer, getUuid());
			}
		else {
			t.addTeam(gamer);
			gamer.setTeam(t);
		}

		if (t != null)
			if (!teamList.contains(t))
				teamList.add(t);

		return t;
	}

	public void handleLeave(Gamer gamer) {
		if (gamer.getTeam() == null) {
			Iterator<Team> iterator = teamList.iterator();

			while (iterator.hasNext()) {
				Team team = iterator.next();

				if (team.isInTeam(gamer))
					if (team.getMembers() - 1 == 0) {
						for (Gamer g : team.getGamerList())
							g.setTeam(null);

						iterator.remove();
					} else
						team.removeTeam(gamer);
			}
		} else {
			Team team = gamer.getTeam();

			if (team.isInTeam(gamer))
				if (team.getMembers() - 1 == 0) {
					removeTeam(team);
				} else
					team.removeTeam(gamer);
			else
				gamer.setTeam(null);
		}
	}
	
	public boolean hasTeam(UUID uuid) {
		return teamList.stream().filter(team -> team.getUniqueId().equals(uuid)).findFirst().isPresent();
	}

	public UUID getUuid() {
		UUID uuid = UUID.randomUUID();

		while (hasTeam(uuid))
			uuid = UUID.randomUUID();

		return uuid;
	}

	public List<Team> getTeamPlayingList() {
		return teamList.stream().filter(team -> team.isAlive()).collect(Collectors.toList());
	}

	public List<Team> getTeamList() {
		return teamList;
	}

}
