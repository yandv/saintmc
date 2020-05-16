package tk.yallandev.saintmc.common.command;

import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;

/*
 * https://github.com/mcardy/CommandFramework
 * Took from https://github.com/Battlebits
 * 
 */

public interface CommandSender {

    UUID getUniqueId();
    
    String getName();

    void sendMessage(String str);
	
    void sendMessage(BaseComponent str);

    void sendMessage(BaseComponent[] fromLegacyText);
}
