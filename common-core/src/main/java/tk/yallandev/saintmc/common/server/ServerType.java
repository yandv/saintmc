package tk.yallandev.saintmc.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@Getter
@AllArgsConstructor
public enum ServerType {

    HUNGERGAMES("HG"),
    GLADIATOR("Gladiator"),
    FULLIRON("PvP FullIron"),
    SIMULATOR("PvP Simulator"),
    LOBBY("Lobby"), 
    LOGIN("Login"),
    SCREENSHARE("Screenshare"),
    NETWORK(),
    NONE;
	
	private String serverName;
	
	ServerType() {
		serverName = NameUtils.formatString(name());
	}

    public boolean isLobby() {
        return this == LOBBY;
    }

    public ServerType getServerLobby() {
        switch (this) {
            default:
                return LOBBY;
        }
	}

}
