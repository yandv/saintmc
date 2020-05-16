package tk.yallandev.saintmc;

import java.util.UUID;

public interface CommonPlatform {
	
	UUID getUuid(String playerName);
	
	<T> T getPlayerByName(String playerName, Class<T> clazz);
	
	<T> T getExactPlayerByName(String playerName, Class<T> clazz);
	
	<T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz);
	
	void runAsync(Runnable runnable);

}
