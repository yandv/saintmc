package tk.yallandev.saintmc.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@Getter
@AllArgsConstructor
public enum ServerType {

	LOGIN("Login"),
    LOBBY("Lobby"), 
    
    HUNGERGAMES("HG"),
    FULLIRON("PvP FullIron"),
    SIMULATOR("PvP Simulator"),
    GLADIATOR("Gladiator"),
    
    EVENTO("Evento"),
    
    SW_SOLO("Skywars Solo"),
    SW_TEAM("Skywars Team"),
    SW_SQUAD("Skywars Squad"),
    
    CLANXCLAN("Clan x Clan"),
    PEAK("Peak"),
    
    PRIVATE_SERVER("Private Server"),
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

    public boolean canSendData() {
		return this == PEAK ? false : true;
	}

}
