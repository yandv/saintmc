package tk.yallandev.saintmc.update.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;
import tk.yallandev.saintmc.update.Updater;

@Getter
@SuppressWarnings("resource")
public class PluginInfo {

	@Setter
	private Plugin plugin;

	private String pluginName;
	private String version;

	private String downloadUrl;
	private String actualVersion;

	private File pluginFile;

	public PluginInfo(String path) {

		if (path.endsWith(".jar")) {
			this.pluginFile = new File(path);
		} else {
			File file = new File(path);

			if (file.isDirectory()) {
				if (!path.endsWith("plugins")) {
					path = path + "\\plugins";
					file = new File(path);

					if (!file.exists())
						file.mkdirs();
				}
			}

			this.pluginFile = new File(path + "\\" + pluginName + ".jar");
		}

		if (pluginFile.exists()) {
			try {
				this.pluginName = actualName();
				this.actualVersion = actualVersion();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		} else {
			String[] split = path.split("\\\\");

			System.out.println(split[split.length - 1]);

			this.pluginName = path.endsWith(".jar") ? split[split.length - 1].replace(".jar", "") : "???";
			this.actualVersion = "0.0";
		}

		try {
			JsonElement json = CommonConst.DEFAULT_WEB.doRequest(Updater.API + "/plugin/?pluginName=" + pluginName,
					Method.GET);

			JsonObject jsonObject = (JsonObject) json;

			PluginInfo pluginInfo = CommonConst.GSON.fromJson(jsonObject, PluginInfo.class);

			if (pluginInfo.getPluginName() != null)
				this.pluginName = pluginInfo.pluginName;
			this.version = pluginInfo.version == null ? actualVersion : pluginInfo.version;
			this.downloadUrl = pluginInfo.downloadUrl;
		} catch (Exception e) {

			try {
				JsonElement json = CommonConst.DEFAULT_WEB
						.doRequest("http://localhost/plugin/?pluginName=" + pluginName, Method.GET);

				JsonObject jsonObject = (JsonObject) json;

				PluginInfo pluginInfo = CommonConst.GSON.fromJson(jsonObject, PluginInfo.class);

				if (pluginInfo.getPluginName() != null)
					this.pluginName = pluginInfo.pluginName;
				this.version = pluginInfo.version == null ? actualVersion : pluginInfo.version;
				this.downloadUrl = pluginInfo.downloadUrl;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public PluginInfo(String pluginName, String path) {
		try {
			JsonElement json = CommonConst.DEFAULT_WEB.doRequest(Updater.API + "/plugin/?pluginName=" + pluginName,
					Method.GET);

			JsonObject jsonObject = (JsonObject) json;

			PluginInfo pluginInfo = CommonConst.GSON.fromJson(jsonObject, PluginInfo.class);

			this.pluginName = pluginInfo.pluginName == null ? pluginName : pluginInfo.getPluginName();
			this.version = pluginInfo.version;
			this.downloadUrl = pluginInfo.downloadUrl;

			if (path.endsWith(".jar")) {
				this.pluginFile = new File(path);
			} else {
				File file = new File(path);

				if (file.isDirectory()) {
					if (!path.endsWith("plugins")) {
						path = path + "\\plugins";
						file = new File(path);

						if (!file.exists())
							file.mkdirs();
					}
				}

				this.pluginFile = new File(path + "\\" + pluginName + ".jar");
			}

			if (this.pluginFile.exists()) {
				this.actualVersion = actualVersion();
				System.out.println("Found!");
			} else {
				this.actualVersion = "0.0";
				System.out.println("Not found!");
			}
		} catch (Exception e) {
			try {
				JsonElement json = CommonConst.DEFAULT_WEB
						.doRequest("http://localhost/plugin/?pluginName=" + pluginName, Method.GET);

				JsonObject jsonObject = (JsonObject) json;

				PluginInfo pluginInfo = CommonConst.GSON.fromJson(jsonObject, PluginInfo.class);

				this.pluginName = pluginInfo.pluginName == null ? pluginName : pluginInfo.getPluginName();
				this.version = pluginInfo.version;
				this.downloadUrl = pluginInfo.downloadUrl;

				if (path.endsWith(".jar")) {
					this.pluginFile = new File(path);
				} else {
					File file = new File(path);

					if (file.isDirectory()) {
						if (!path.endsWith("plugins")) {
							path = path + "\\plugins";
							file = new File(path);

							if (!file.exists())
								file.mkdirs();
						}
					}

					this.pluginFile = new File(path + "\\" + pluginName + ".jar");
				}

				if (this.pluginFile.exists()) {
					this.actualVersion = actualVersion();
					System.out.println("Found!");
				} else {
					this.actualVersion = "0.0";
					System.out.println("Not found!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public String actualVersion() throws Exception {
		JarFile jarFile = new JarFile(pluginFile);

		try {
			ZipEntry zip = new ZipEntry("plugin.yml");

			InputStream inputStream = jarFile.getInputStream(zip);

			Yaml yaml = new Yaml();

			Map<String, Object> map = (Map<String, Object>) yaml.load(inputStream);

			if (map.containsKey("version")) {
				return String.valueOf(map.get("version"));
			}
		} catch (YAMLException e) {
			ZipEntry zip = new ZipEntry("bungee.yml");
			InputStream inputStream = jarFile.getInputStream(zip);

			Yaml yaml = new Yaml();

			Map<String, Object> map = (Map<String, Object>) yaml.load(inputStream);

			if (map.containsKey("version")) {
				return String.valueOf(map.get("version"));
			}
		}

		throw new Exception("Cannot get version of " + pluginName);
	}

	public String actualName() throws Exception {
		JarFile jarFile = new JarFile(pluginFile);

		try {
			ZipEntry zip = new ZipEntry("plugin.yml");

			InputStream inputStream = jarFile.getInputStream(zip);

			Yaml yaml = new Yaml();

			Map<String, Object> map = (Map<String, Object>) yaml.load(inputStream);

			if (map.containsKey("name")) {
				return String.valueOf(map.get("name"));
			}
		} catch (YAMLException e) {
			ZipEntry zip = new ZipEntry("bungee.yml");
			InputStream inputStream = jarFile.getInputStream(zip);

			Yaml yaml = new Yaml();

			Map<String, Object> map = (Map<String, Object>) yaml.load(inputStream);

			if (map.containsKey("name")) {
				return String.valueOf(map.get("name"));
			}
		}

		throw new Exception("Cannot get version of " + pluginName);
	}

	public boolean needUpdate() {
		if (this.version == null || this.actualVersion == null)
			return true;

		if (this.actualVersion.equals("0.0"))
			return true;

		int major = 0;
		int minor = 0;
		int build = 0;

		if (this.version.contains(".")) {
			major = Integer.valueOf(this.version.split("\\.")[0]);
			minor = Integer.valueOf(this.version.split("\\.")[1]);

			if (this.version.split("\\.").length >= 3)
				build = Integer.valueOf(this.version.split("\\.")[2]);
		}

		int actualMajor = 0;
		int actualMinor = 0;
		int actualBuild = 0;

		if (this.actualVersion.contains(".")) {
			actualMajor = Integer.valueOf(this.actualVersion.split("\\.")[0]);
			actualMinor = Integer.valueOf(this.actualVersion.split("\\.")[1]);

			if (this.actualVersion.split("\\.").length >= 3)
				actualBuild = Integer.valueOf(this.actualVersion.split("\\.")[2]);
		}

		return major > actualMajor || major <= actualMajor && minor > actualMinor
				|| major <= actualMajor && minor <= actualMinor && build > actualBuild;
	}

	public boolean update(String api, String key) {
		if (getPlugin() == null)
			throw new IllegalStateException();

		return getPlugin().update(api, key);
	}

	public Plugin getPlugin() {
		return plugin;
	}

}
