package tk.yallandev.saintmc.bukkit.api.hologram.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;
import tk.yallandev.saintmc.bukkit.api.hologram.TouchHandler;
import tk.yallandev.saintmc.bukkit.api.hologram.ViewHandler;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.permission.Group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TopRanking<T> {

    private Hologram mainHologram;

    private Map<UUID, Integer> playerPageMap;

    private int firstLineIndex;
    private UpdateHandler<T> updateHandler;

    private List<T> list;

    public TopRanking(Hologram mainHologram, UpdateHandler<T> updateHandler, FormatString<T> format) {
        this.mainHologram = mainHologram;
        this.playerPageMap = new HashMap<>();

        this.firstLineIndex = mainHologram.getLinesBelow().size() - 1;
        this.updateHandler = updateHandler;

        this.list = this.updateHandler.update();
        this.mainHologram.setTouchHandler((hologram, player, touchType) -> {
            int currentPage = this.playerPageMap.getOrDefault(player.getUniqueId(), 1);

            if (touchType == TouchHandler.TouchType.RIGHT) {
                currentPage--;

                if (currentPage < 1) {
                    currentPage = 10;
                }
            } else {
                currentPage++;

                if (currentPage > 10) {
                    currentPage = 1;
                }
            }

            this.playerPageMap.put(player.getUniqueId(), currentPage);

            for (Hologram page : mainHologram.getLinesBelow())
                page.updateTitle(player);
        });

        ViewHandler viewHandler = new ViewHandler() {

            @Override
            public String onView(Hologram hologram, Player player, String text) {
                int page = playerPageMap.getOrDefault(player.getUniqueId(), 1);
                int index = Integer.parseInt(text);
                int realIndex = (page - 1) * 10 + index;

                if (realIndex >= list.size()) {
                    return format.format(null, realIndex + 1);
                }

                return format.format(list.get(realIndex), realIndex + 1);
            }
        };

        for (int i = 0; i < 10; i++) {
            mainHologram.addLineBelow(Integer.toString(i)).setViewHandler(viewHandler);
        }

        BukkitMain.getInstance().getHologramController().loadHologram(mainHologram);
    }

    @AllArgsConstructor
    @Getter
    public static class RankingModel<T extends Status> {

        private T status;

        private String playerName;
        private Group group;


    }

    public void update() {
        this.list = this.updateHandler.update();
    }

    public interface UpdateHandler<T> {

        List<T> update();
    }

    public interface FormatString<T> {

        String format(T toFormat, int position);
    }
}
