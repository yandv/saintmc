package tk.yallandev.saintmc.common.account;

import lombok.Getter;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

@Getter
public enum League {

    SILVER("§7", "★", "Silver I", 1000),
    SILVER_II("§7", "★", "Silver II", 2000),
    SILVER_III("§7", "★", "Silver III", 4000), /* silver */

    GOLD("§6", "◆", "Gold I", 6000),
    GOLD_II("§6", "◆", "Gold II", 8000),
    GOLD_III("§6", "◆", "Gold III", 10000),

    EXPERT("§9", "◈", "Expert I", 14000),
    EXPERT_II("§9", "◈", "Expert II", 20000),
    EXPERT_III("§9", "◈", "Expert III", 26000),

    ELITE("§5", "✣", "Elite I", 31000),
    ELITE_II("§5", "✣", "Elite II", 35000),
    ELITE_III("§5", "✣", "Elite III", 44000),

    MASTER("§6", "✥", "Master I", 55000),
    MASTER_II("§6", "✥", "Master II", 67000),
    MASTER_III("§6", "✥", "Master III", 80000),
    MASTER_IV("§6", "✥", "Master III", 100000),


    LEGENDARY("§4", "✪", "Legendary", Integer.MAX_VALUE);

    private String color;
    private String symbol;
    private String name;
    private int maxXp;

    League(String color, String symbol, String name, int maxXp) {
        this.symbol = symbol;
        this.color = color;
        this.name = name;
        this.maxXp = maxXp;
    }

    public League getNextLeague() {
        return ordinal() + 1 <= values()[values().length - 1].ordinal() ? League.values()[ordinal() + 1]
                                                                        : values()[values().length - 1];
    }

    public League getPreviousLeague() {
        return ordinal() - 1 >= 0 ? values()[ordinal() - 1] : values()[0];
    }
}
