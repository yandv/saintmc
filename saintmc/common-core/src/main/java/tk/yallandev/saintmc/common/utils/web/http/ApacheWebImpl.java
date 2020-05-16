package tk.yallandev.saintmc.common.utils.web.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.utils.web.WebHelper;

public class ApacheWebImpl implements WebHelper {
	
	@Getter
	private CloseableHttpClient closeableHttpClient;
	
	public ApacheWebImpl(CloseableHttpClient closeableHttpClient) {
		this.closeableHttpClient = closeableHttpClient;
	}
	
	public ApacheWebImpl() {
		this.closeableHttpClient = HttpClientBuilder.create()
				.setMaxConnTotal(9999)
				.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000)
						.setSocketTimeout(3000).setMaxRedirects(3).build())
				.build();
	}
	
	/**
	 * used to do generic request using apache http client api
	 * 
	 * @param url and body json
	 * @return jsonObject not null
	 * @throws Exception 
	 */

	@Override
	public JsonElement doRequest(HttpRequestBase requestBase) throws Exception {
		CloseableHttpResponse response = closeableHttpClient.execute(requestBase);
		
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);
		
		if (json == null) {
			response.close();
			throw new Exception("Received empty response from your server, check connections.");
		}
		
		JsonElement jsonElement = new JsonObject();
		
		try {
			jsonElement = JsonParser.parseString(json);
		} catch (Exception ex) {
//			System.out.println(ex);
			CommonGeneral.getInstance().getLogger().warning(json);
		}
		
		response.close();
		
		return jsonElement;
	}
	
	/**
	 * used to make get request using apache http client api
	 * 
	 * @param url and body json
	 * @return jsonObject not null
	 */
	
	@Override
	public JsonElement get(String url) throws Exception {
		HttpGet request = new HttpGet(url);

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		
		return doRequest(request);
	}
	
	/**
	 * used to make post request using apache http client api
	 * 
	 * @param url and body json
	 * @return jsonObject not null
	 */
	
	@Override
	public JsonElement post(String url, String jsonEntity) throws Exception {
		HttpPost request = new HttpPost(url);

		StringEntity entity = new StringEntity(jsonEntity);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		
		return doRequest(request);
	}
	
	public static void main(String[] args) {
		ApacheWebImpl apache = new ApacheWebImpl();
		
		try {
			System.out.println(apache.get("http://localhost:3333/mojang/session/?ip=186.221.185.74"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
