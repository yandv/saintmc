package tk.yallandev.saintmc.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
		serverName = name();
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
