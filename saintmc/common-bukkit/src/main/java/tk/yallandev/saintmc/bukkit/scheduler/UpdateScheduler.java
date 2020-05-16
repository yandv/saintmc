package tk.yallandev.saintmc.bukkit.scheduler;

import org.bukkit.Bukkit;

import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class UpdateScheduler implements Runnable {

    private long currentTick;

    @Override
    public void run() {
        currentTick++;
        Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.TICK, currentTick));

        if (currentTick % 20 == 0) {
            Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.SECOND, currentTick));
        }

        if (currentTick % 1200 == 0) {
            Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.MINUTE, currentTick));
        }
    }
}
