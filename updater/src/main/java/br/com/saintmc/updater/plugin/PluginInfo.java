package br.com.saintmc.updater.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.UpdateConst;
import tk.yallandev.saintmc.common.utils.web.WebHelper.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;
import tk.yallandev.saintmc.updater.plugin.Plugin;

@Getter
public class PluginInfo {
	
	@Setter
	private Plugin plugin;

	private String pluginName;
	private String version;

	private String downloadUrl;

	private String actualVersion;
	
	public boolean needUpdate() {
		if (this.version == null || this.actualVersion == null)
			return true;
		
		if (this.actualVersion.equals("0.0"))
			return true;

		int major = 0;
		int minor = 0;

		if (this.version.contains(".")) {
			major = Integer.valueOf(this.version.split("\\.")[0]);
			minor = Integer.valueOf(this.version.split("\\.")[1]);
		}

		int actualMajor = 0;
		int actualMinor = 0;

		if (this.actualVersion.contains(".")) {
			actualMajor = Integer.valueOf(this.actualVersion.split("\\.")[0]);
			actualMinor = Integer.valueOf(this.actualVersion.split("\\.")[1]);
		}

		return major > actualMajor || major <= actualMajor && minor > actualMinor;
	}
	
	public static void needUpdate(String pluginName, FutureCallback<Boolean> callback) {
		CommonConst.DEFAULT_WEB.doAsyncRequest(UpdateConst.WEBSITE + "/?pluginName=" + pluginName, Method.GET, new FutureCallback<JsonElement>() {
			
			@Override
			public void result(JsonElement result, Throwable error) {
				if (error == null) {
					JsonObject jsonObject = (JsonObject) result;

					PluginInfo pluginInfo = CommonConst.GSON.fromJson(jsonObject, PluginInfo.class);
					
					callback.result(pluginInfo.needUpdate(), error);
				} else {
					callback.result(false, error);
				}
			}
		});
	}
	
	public static void needUpdate(PluginInfo pluginInfo, FutureCallback<Boolean> callback) {
		needUpdate(pluginInfo.getPluginName(), callback);
	}

}
