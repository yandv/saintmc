package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.api.tag.Chroma;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class ChromaListener extends ManualRegisterableListener {
	
	@Getter
	private List<Chroma> chromaList;

	private char[] chatColors = { 'a', 'b', 'e', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private char lastColor;
	
	public ChromaListener() {
		chromaList = new ArrayList<>();
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			if (!chromaList.isEmpty()) {
				char color = 'e';

				do {
					color = chatColors[CommonConst.RANDOM.nextInt(chatColors.length)];
				} while (lastColor == color);

				lastColor = color;

				for (Chroma chroma : chromaList) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						ScoreboardAPI.setTeamPrefix(ScoreboardAPI.createTeamIfNotExistsToPlayer(player,
								chroma.getTeamId(), chroma.getPrefix(color), chroma.getSuffix()),
								chroma.getPrefix(color));
					}
				}
			}
		}
	}

}
