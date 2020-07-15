package tk.yallandev.saintmc.common.server.loadbalancer.server;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.server.ServerType;

@Getter
@Setter
public abstract class MinigameServer extends ProxiedServer {

    private int time;
    private String map = "-/-";
    private MinigameState state;

    public MinigameServer(String serverId, ServerType type, Set<UUID> players, Set<Profile> profile, int maxPlayers, boolean joinEnabled) {
        super(serverId, type, players, profile, 100, joinEnabled);
        this.state = MinigameState.WAITING;
    }

    @Override
    public int getActualNumber() {
        return super.getActualNumber();
    }

    public abstract boolean isInProgress();

}
