package tk.yallandev.saintmc.bukkit.listener;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import tk.yallandev.saintmc.bukkit.event.player.PlayerCommandEvent;

public class CommandListener implements Listener {
	
    private HashMap<String, Command> knownCommands;
    
    public CommandListener() {
		try {
			Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f1.setAccessible(true);

			CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
			Field f2 = commandMap.getClass().getDeclaredField("knownCommands");

			f2.setAccessible(true);
	        knownCommands = (HashMap<String, Command>) f2.get(commandMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!event.getMessage().startsWith("/"))
			return;
		
		String command = event.getMessage().split(" ")[0];
		String commandLabel = command.substring(1, command.length());
		
		PlayerCommandEvent playerCommandEvent = new PlayerCommandEvent(event.getPlayer(), commandLabel);
		Bukkit.getPluginManager().callEvent(playerCommandEvent);
		event.setCancelled(playerCommandEvent.isCancelled());
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandEvent event) {
		if (event.getCommandLabel().split(" ")[0].contains(":")) {
			event.getPlayer().sendMessage(" §c* §fNão é permitido usar comandos com \":\"!");
			event.setCancelled(true);
			return;
		}
		
		if (event.getCommandLabel().startsWith("//"))
			return;

    	String command = event.getCommandLabel().split(" ")[0].replace("/", "");
    	
    	if (!knownCommands.containsKey(command.toLowerCase())) {
			event.getPlayer().sendMessage(" §c* §fO comando não existe!");
			event.setCancelled(true);
    	}
	}
	
}
