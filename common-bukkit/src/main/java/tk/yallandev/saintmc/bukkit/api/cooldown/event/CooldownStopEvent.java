package tk.yallandev.saintmc.bukkit.api.cooldown.event;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;

public class CooldownStopEvent extends CooldownEvent {

    public CooldownStopEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }

}