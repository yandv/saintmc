package tk.yallandev.saintmc.update.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.google.gson.JsonObject;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.web.http.ApacheWebImpl;
import tk.yallandev.saintmc.update.Updater;
import tk.yallandev.saintmc.update.updater.Updateable;

@Getter
public class Plugin implements Updateable {

	private PluginInfo pluginInfo;

	public Plugin(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	@Override
	public boolean update(String key) {

		String filePath = pluginInfo.getPluginFile().getAbsolutePath();

		try {
			HttpGet request = new HttpGet(
					Updater.API + "/plugin/download/?pluginName=" + pluginInfo.getPluginName() + "&key=" + key);

			CloseableHttpResponse response = ((ApacheWebImpl) CommonConst.DEFAULT_WEB).getCloseableHttpClient()
					.execute(request);
			HttpEntity entity = response.getEntity();

			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode == 200) {
				System.out.println("[Downloader] Downloading " + pluginInfo.getPluginName() + " version "
						+ pluginInfo.getVersion());
			} else {
				response.close();
				System.out.println("[Downloader] Cant download the " + pluginInfo.getPluginName() + " version "
						+ pluginInfo.getVersion() + " (status: " + responseCode + ")");
				return false;
			}

			InputStream is = entity.getContent();

			File file = new File(filePath);

			if (!file.getParentFile().exists())
				file.getParentFile().mkdir();

			FileOutputStream fos = new FileOutputStream(file);

			int inByte;
			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}

			is.close();
			fos.close();

			response.close();
			System.out.println("[Downloader] Plugin " + pluginInfo.getPluginName() + " (" + pluginInfo.getVersion()
					+ ") has been downloaded in " + pluginInfo.getPluginFile().getAbsolutePath());

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean upload(String key) {
		HttpPost httppost = new HttpPost(Updater.API + "/plugin/?pluginName=" + pluginInfo.getPluginName() + "&version="
				+ pluginInfo.getActualVersion() + "");

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();

		mpEntity.addPart("file-to-upload", new FileBody(pluginInfo.getPluginFile()));
		httppost.setEntity(mpEntity.build());

		try {
			JsonObject jsonObject = (JsonObject) ((ApacheWebImpl) CommonConst.DEFAULT_WEB).doRequest(httppost);

			if (jsonObject.has("success")) {
				if (jsonObject.get("success").getAsBoolean()) {
					Updater.getInstance()
							.debug("[Update] The plugin " + pluginInfo.getPluginName() + " has been sent!");
					return true;
				} else
					Updater.getInstance()
							.debug("[Update] The plugin " + pluginInfo.getPluginName() + " hasn't been sent!");
			} else {
				System.out.println(jsonObject);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
