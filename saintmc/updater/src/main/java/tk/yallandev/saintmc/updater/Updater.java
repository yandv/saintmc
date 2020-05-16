package tk.yallandev.saintmc.updater;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.updater.plugin.Plugin;
import tk.yallandev.saintmc.updater.plugin.PluginInfo;
import tk.yallandev.saintmc.updater.updater.UpdateType;

@Getter
public class Updater {

	@Getter
	private static Updater instance;
	
	private CommonGeneral general;

	private UpdateType updateType;

	private List<PluginInfo> pluginList;

	public Updater() {
		instance = this;
		general = new CommonGeneral(Logger.getLogger("updater"));

		updateType = UpdateType.UPDATE;
		pluginList = new ArrayList<>();

		JsonObject json = null;

		try {
			json = (JsonObject) JsonParser.parseReader(new JsonReader(new FileReader(
					getClass().getClassLoader().getResource("config.json").getFile().replace("%20", " "))));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		JsonArray jsonArray = json.get("pluginList").getAsJsonArray();

		for (int x = 0; x < jsonArray.size(); x++) {

			JsonObject jsonObject = (JsonObject) jsonArray.get(x);

			if (jsonObject.has("path")) {
				PluginInfo pluginInfo = new PluginInfo(jsonObject.get("path").getAsString());

				debug(" ");
				debug("[Plugin] The plugin " + pluginInfo.getPluginName() + " version " + pluginInfo.getVersion()
						+ " has been loaded!");
				debug("[Plugin] The actual version is " + pluginInfo.getActualVersion() + " ("
						+ (pluginInfo.needUpdate() ? "need update" : "last version") + ")!");
				
				pluginInfo.setPlugin(new Plugin(pluginInfo));

				pluginList.add(pluginInfo);
			}
		}
		
		switch (updateType) {
		case DOWNLOAD: {
			runCheck();
			break;
		}
		case UPDATE: {
			updateCheck();
			break;
		}
		case NONE: {
			debug("None status!");
			break;
		}
		}
	}

	public void runCheck() {
		debug(" ");

		for (PluginInfo plugin : pluginList) {
			if (plugin.needUpdate()) {
				debug("[Checker] The plugin " + plugin.getPluginName() + " need update!");

				plugin.update();
			}
		}
	}

	public void updateCheck() {
		debug(" ");

		for (PluginInfo plugin : pluginList) {
			plugin.getPlugin().upload();
		}
	}

	public void debug(String message) {
		general.debug(message);
	}

	public static void main(String[] args) {
		new Updater();
	}

}
