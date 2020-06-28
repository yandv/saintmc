package br.com.saintmc.hungergames.event.team;

import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.game.Team;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class TeamPlayerEvent extends PlayerCancellableEvent {

    private Gamer gamer;

    private Team team;

    public TeamPlayerEvent(Team team, Gamer gamer) {
        super(gamer.getPlayer());
        this.gamer = gamer;
        this.team = team;
    }
}
