package tk.yallandev.saintmc.bukkit.api.server.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatState {
	
	ENABLED(true, "*"), STAFF("todos da equipe"), YOUTUBER("YOUTUBER e superiores"), PAYMENT("PRO e superiores"), DISABLED("ADMIN e superiores");
	
	private boolean enabled;
	private String availableTo;
	
	private ChatState(String availableTo) {
		this.enabled = false;
		this.availableTo = availableTo;
	}
	
}
