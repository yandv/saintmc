package br.com.saintmc.hungergames.event.team;


import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.Team;

public class TeamPlayerJoinEvent extends TeamPlayerEvent {
    public TeamPlayerJoinEvent(Team team, Gamer gamer) {
        super(team, gamer);
    }
}
