package tk.yallandev.saintmc.update;

import java.io.File;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.update.plugin.Plugin;
import tk.yallandev.saintmc.update.plugin.PluginInfo;

public class UpdatePlugin {

	public static boolean update(File file, String pluginName, Shutdown shutdown) {
		PluginInfo pluginInfo = new PluginInfo(pluginName, file.getAbsolutePath().replace("%20", " "));

		CommonGeneral.getInstance().debug(" ");
		CommonGeneral.getInstance().debug("[Common] The plugin " + pluginInfo.getPluginName() + " version "
				+ pluginInfo.getVersion() + " has been loaded!");
		CommonGeneral.getInstance().debug("[Common] The actual version is " + pluginInfo.getActualVersion() + " ("
				+ (pluginInfo.needUpdate() ? "need update" : "last version") + ")!");

		pluginInfo.setPlugin(new Plugin(pluginInfo));

		if (pluginInfo.needUpdate()) {
			CommonGeneral.getInstance().debug("[Common] The plugin " + pluginInfo.getPluginName() + " need update!");

			if (pluginInfo.update()) {
				shutdown.stop();
				return true;
			}
		}

		return false;
	}

	public static interface Shutdown {

		void stop();

	}

}
