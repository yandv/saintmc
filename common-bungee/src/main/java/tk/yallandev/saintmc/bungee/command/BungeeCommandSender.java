package tk.yallandev.saintmc.bungee.command;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.common.command.CommandSender;

@AllArgsConstructor
@Getter
public class BungeeCommandSender implements CommandSender {

    private net.md_5.bungee.api.CommandSender sender;

    @Override
    public UUID getUniqueId() {
        if (sender instanceof ProxiedPlayer)
            return ((ProxiedPlayer) sender).getUniqueId();
        return UUID.randomUUID();
    }
    
	@Override
	public String getName() {
		if (sender instanceof ProxiedPlayer)
            return ((ProxiedPlayer) sender).getName();
        return "CONSOLE";
	}

    @Override
    public void sendMessage(String str) {
        sender.sendMessage(TextComponent.fromLegacyText(str));
    }

    @Override
    public void sendMessage(BaseComponent str) {
        sender.sendMessage(str);
    }

    @Override
    public void sendMessage(BaseComponent[] fromLegacyText) {
        sender.sendMessage(fromLegacyText);
    }

	@Override
	public boolean isPlayer() {
		return sender instanceof ProxiedPlayer;
	}

}
