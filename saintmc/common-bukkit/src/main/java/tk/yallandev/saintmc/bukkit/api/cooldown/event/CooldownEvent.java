package tk.yallandev.saintmc.bukkit.api.cooldown.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;

@RequiredArgsConstructor
public abstract class CooldownEvent extends Event {

    @Getter
    @NonNull
    private Player player;

    @Getter
    @NonNull
    private Cooldown cooldown;
}
