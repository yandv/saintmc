package tk.yallandev.saintmc.skwyars.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.game.chest.Chest;

@Getter
public class LocationController {
	
	private Gson gson = new GsonBuilder().create();

	private Map<String, Location> locationMap;
	private List<Chest> chestList;

	public LocationController() {
		locationMap = new HashMap<>();
		chestList = new ArrayList<>();

//		JsonArray jsonArray = JsonParser.parseJson()(GameMain.getInstance().getConfig().getString("chests", "[]"))
//				.getAsJsonArray();
//
//		for (int x = 0; x < jsonArray.size(); x++) {
//			Chest chest = gson.fromJson(jsonArray.get(x), Chest.class);
//			chestList.add(chest);
//		}
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
