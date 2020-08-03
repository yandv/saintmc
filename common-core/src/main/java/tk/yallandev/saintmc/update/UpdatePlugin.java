package tk.yallandev.saintmc.update;

import java.io.File;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.update.plugin.Plugin;
import tk.yallandev.saintmc.update.plugin.PluginInfo;

public class UpdatePlugin {

	public static boolean update(File file, String pluginName, String key, Shutdown shutdown) {
		try {
			PluginInfo pluginInfo = new PluginInfo(pluginName, file.getAbsolutePath().replace("%20", " "));

			CommonGeneral.getInstance().debug(" ");
			CommonGeneral.getInstance().debug("[Common] The plugin " + pluginInfo.getPluginName() + " version "
					+ pluginInfo.getVersion() + " has been loaded!");
			CommonGeneral.getInstance().debug("[Common] The actual version is " + pluginInfo.getActualVersion() + " ("
					+ (pluginInfo.needUpdate() ? "need update" : "last version") + ")!");

			pluginInfo.setPlugin(new Plugin(pluginInfo));

			if (pluginInfo.needUpdate()) {
				CommonGeneral.getInstance()
						.debug("[Common] The plugin " + pluginInfo.getPluginName() + " need update!");

				if (pluginInfo.update(Updater.API, key)) {
					System.exit(0);
					return true;
				}
			}
		} catch (Exception ex) {
			try {
				PluginInfo pluginInfo = new PluginInfo(pluginName, file.getAbsolutePath().replace("%20", " "));

				CommonGeneral.getInstance().debug(" ");
				CommonGeneral.getInstance().debug("[Common] The plugin " + pluginInfo.getPluginName() + " version "
						+ pluginInfo.getVersion() + " has been loaded!");
				CommonGeneral.getInstance().debug("[Common] The actual version is " + pluginInfo.getActualVersion()
						+ " (" + (pluginInfo.needUpdate() ? "need update" : "last version") + ")!");

				pluginInfo.setPlugin(new Plugin(pluginInfo));

				if (pluginInfo.needUpdate()) {
					CommonGeneral.getInstance()
							.debug("[Common] The plugin " + pluginInfo.getPluginName() + " need update!");

					if (pluginInfo.update("http://localhost:3333", key)) {
						System.exit(0);
						return true;
					}
				}
			} catch (Exception e) {
				CommonGeneral.getInstance().debug("Couldn't connect to http://apidata.saintmc.net/!");
			}
		}

		return false;
	}

	public static interface Shutdown {

		void stop();

	}

}
