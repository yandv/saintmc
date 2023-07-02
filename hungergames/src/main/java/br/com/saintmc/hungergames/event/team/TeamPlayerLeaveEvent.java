package br.com.saintmc.hungergames.event.team;


import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.Team;

public class TeamPlayerLeaveEvent extends TeamPlayerEvent {

    public TeamPlayerLeaveEvent(Team team, Gamer gamer) {
        super(team, gamer);
    }
}