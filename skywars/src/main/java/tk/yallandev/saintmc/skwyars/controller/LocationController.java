package tk.yallandev.saintmc.skwyars.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.game.chest.Chest;

@Getter
public class LocationController {

	private Map<String, Location> locationMap;
	private List<Chest> chestList;

	public LocationController() {
		locationMap = new HashMap<>();
		chestList = new ArrayList<>();

		JsonArray jsonArray = JsonParser.parseString(GameMain.getInstance().getConfig().getString("chests", "[]"))
				.getAsJsonArray();

		for (int x = 0; x < jsonArray.size(); x++) {
			Chest chest = CommonConst.GSON.fromJson(jsonArray.get(x), Chest.class);
			chestList.add(chest);
		}
	}

	public void registerLocation(String string, Location location) {
		locationMap.put(string, location);
	}

	public void registerChest(Chest chest) {
		if (!chestList.contains(chest))
			chestList.add(chest);
	}

	public void removeChest(Chest chest) {
		if (chestList.contains(chest))
			chestList.remove(chest);
	}

	public void saveChests() {
		GameMain.getInstance().getConfig().set("chests", new Gson().toJson(chestList));
		GameMain.getInstance().saveConfig();
	}

}
