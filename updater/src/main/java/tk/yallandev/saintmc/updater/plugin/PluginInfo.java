package tk.yallandev.saintmc.updater.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.UpdateConst;

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
			
			System.out.println(split[split.length-1]);
			
			this.pluginName = path.endsWith(".jar") ? split[split.length-1].replace(".jar", "") : "???";
			this.actualVersion = "0.0";
		}

		try {
			JsonElement json = UpdateConst.WEB.get(UpdateConst.WEBSITE + "/?pluginName=" + pluginName);

			JsonObject jsonObject = (JsonObject) json;

			PluginInfo pluginInfo = CommonConst.GSON.fromJson(jsonObject, PluginInfo.class);

			if (pluginInfo.getPluginName() != null)
				this.pluginName = pluginInfo.pluginName;
				this.version = pluginInfo.version == null ? actualVersion : pluginInfo.version;
			this.downloadUrl = pluginInfo.downloadUrl;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PluginInfo(String pluginName, String path) {
		try {
			JsonElement json = UpdateConst.WEB.get(UpdateConst.WEBSITE + "/?pluginName=" + pluginName);

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

			if (this.pluginFile.exists())
				this.actualVersion = actualVersion();
			else
				this.actualVersion = "0.0";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String actualVersion() throws Exception {
		try {
			JarFile jarFile = new JarFile(pluginFile);

			ZipEntry zip = new ZipEntry("plugin.yml");

			if (zip.getSize() == -1)
				zip = new ZipEntry("bungee.yml");

			InputStream inputStream = jarFile.getInputStream(zip);

			Yaml yaml = new Yaml();

			Map<String, Object> map = (Map<String, Object>) yaml.load(inputStream);

			if (map.containsKey("version")) {
				return String.valueOf(map.get("version"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new Exception("Cannot get version of " + pluginName);
	}

	public String actualName() throws Exception {
		try {
			JarFile jarFile = new JarFile(pluginFile);

			ZipEntry zip = new ZipEntry("plugin.yml");

			if (zip.getSize() == -1)
				zip = new ZipEntry("bungee.yml");

			InputStream inputStream = jarFile.getInputStream(zip);

			Yaml yaml = new Yaml();

			Map<String, Object> map = (Map<String, Object>) yaml.load(inputStream);

			if (map.containsKey("name")) {
				return String.valueOf(map.get("name"));
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	public boolean update() {
		if (getPlugin() == null)
			throw new IllegalStateException();

		return getPlugin().update();
	}

	public Plugin getPlugin() {
		return plugin;
	}

}