package tk.yallandev.saintmc.update;

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
import tk.yallandev.saintmc.update.plugin.Plugin;
import tk.yallandev.saintmc.update.plugin.PluginInfo;
import tk.yallandev.saintmc.update.updater.UpdateType;

@Getter
public class Updater {

	public static final String API = "http://apidata.saintmc.net";

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
		
		System.exit(0);
	}

	public void runCheck() {
		debug(" ");

		for (PluginInfo plugin : pluginList) {
			if (plugin.needUpdate()) {
				debug("[Checker] The plugin " + plugin.getPluginName() + " need update!");

				plugin.update("kangaroo123");
			}
		}
	}

	public void updateCheck() {
		debug(" ");

		for (PluginInfo plugin : pluginList) {
			plugin.getPlugin().upload("kangaroo123");
		}
	}

	public void debug(String message) {
		System.out.println(message);
	}

	public static void main(String[] args) {
		new Updater();
	}

}
