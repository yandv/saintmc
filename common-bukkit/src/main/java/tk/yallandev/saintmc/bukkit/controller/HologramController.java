package tk.yallandev.saintmc.bukkit.controller;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HologramController {

    private final Map<Integer, Hologram> hologramMap;

    public HologramController() {
        this.hologramMap = new HashMap<>();
    }

    public <T extends Hologram> T loadHologram(T hologram) {
        this.hologramMap.put(hologram.getEntityId(), hologram);
        return hologram;
    }

    public Hologram getHologramById(int entityId) {
        return this.hologramMap.get(entityId);
    }

    public Collection<? extends Hologram> getHolograms() {
        return this.hologramMap.values();
    }
}
