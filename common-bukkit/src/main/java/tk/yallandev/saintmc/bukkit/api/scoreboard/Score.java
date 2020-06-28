package tk.yallandev.saintmc.bukkit.api.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public class Score {

    private String teamName;
    private String scoreName;

    private String prefix;
    private String suffix;

    public Score(String text, String teamName) {
        String part1 = text;
        String part2 = "";

        /*
         *
         */

        if (text.length() > 12) {
            int a = 12;

            while (text.substring(0, a).endsWith("§")) {
                --a;
            }

            part1 = text.substring(0, a);
            part2 = text.substring(a);

            if (!part2.startsWith("§")) {
                for (int i = part1.length(); i > 0; i--) {
                    if (part1.charAt(i - 1) == '§') {
                        part2 = part1.substring(i - 1, i + 1) + part2;
                        break;
                    }
                }
            }

            if (!part2.startsWith("§")) {
                part2 = "§f" + part2;
            }
        }

        String id = "";

        for (int i = 0; i < (teamName + "").length(); i++)
            if (id.length() < 16) {
                id += "§" + (teamName + "").charAt(i);
            } else {
                break;
            }

        this.prefix = part1;
        this.suffix = ChatColor.getLastColors(prefix).equals("§f") ? part2 : ChatColor.getLastColors(prefix) + part2;
        this.teamName = teamName;
        this.scoreName = id;

        if (this.teamName.length() > 16) {
            this.teamName = this.teamName.substring(0, 16);
        }

        if (this.scoreName.length() > 16) {
            this.scoreName = this.scoreName.substring(0, 16);
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
