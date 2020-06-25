package tk.yallandev.saintmc.common.server.loadbalancer.server;

import java.util.Set;
import java.util.UUID;

import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.server.ServerType;

public class HungerGamesServer extends MinigameServer {

    public HungerGamesServer(String serverId, ServerType type, Set<UUID> players, Set<Profile> profile, boolean joinEnabled) {
        super(serverId, type, players, profile, 100, joinEnabled);
        setState(MinigameState.WAITING);
    }

    @Override
    public boolean canBeSelected() {
        return super.canBeSelected() && !isInProgress() && ((getState() == MinigameState.PREGAME && getTime() >= 15) || getState() == MinigameState.WAITING);
    }

    @Override
    public boolean isInProgress() {
        return getState() == MinigameState.GAMETIME || getState() == MinigameState.INVINCIBILITY;
    }

}
