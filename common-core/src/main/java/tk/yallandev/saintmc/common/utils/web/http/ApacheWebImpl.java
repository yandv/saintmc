package tk.yallandev.saintmc.common.utils.web.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper;

@Getter
public class ApacheWebImpl implements WebHelper {

	private CloseableHttpClient closeableHttpClient;
	private CloseableHttpAsyncClient closeableHttpAsyncClient;

	public ApacheWebImpl(CloseableHttpClient closeableHttpClient, CloseableHttpAsyncClient closeableHttpAsyncClient) {
		this.closeableHttpClient = closeableHttpClient;
		this.closeableHttpAsyncClient = closeableHttpAsyncClient;
	}

	public ApacheWebImpl() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000)
				.setSocketTimeout(5000).setMaxRedirects(3).build();

		this.closeableHttpClient = HttpClientBuilder.create().setConnectionTimeToLive(10, TimeUnit.SECONDS)
				.setDefaultRequestConfig(requestConfig).build();

		this.closeableHttpAsyncClient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build();
		this.closeableHttpAsyncClient.start();
	}

	public static void main(String[] args) {
		ApacheWebImpl apache = new ApacheWebImpl();

		try {
			apache.doAsyncRequest("http://localhost:3333/mojang/session/?ip=186.221.185.74", Method.GET,
					new FutureCallback<JsonElement>() {

						@Override
						public void result(JsonElement result, Throwable error) {
							System.out.println(result);
						}

					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JsonElement doRequest(String url, Method method) throws Exception {
		return doRequest(url, method, null);
	}

	@Override
	public JsonElement doRequest(String url, Method method, String jsonEntity) throws Exception {
		HttpRequestBase requestBase = createRequestBase(url, method, jsonEntity);

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
			CommonGeneral.getInstance().getLogger().warning(json);
		}

		response.close();

		return jsonElement;
	}

	@Override
	public void doAsyncRequest(String url, Method method, FutureCallback<JsonElement> callback) {
		doAsyncRequest(url, method, null, callback);
	}

	@Override
	public void doAsyncRequest(String url, Method method, String jsonEntity, FutureCallback<JsonElement> callback) {
		try {
			closeableHttpAsyncClient.execute(createRequestBase(url, method, jsonEntity),
					new org.apache.http.concurrent.FutureCallback<HttpResponse>() {
				
						public void completed(HttpResponse response) {

							HttpEntity entity = response.getEntity();
							String json;

							try {
								json = EntityUtils.toString(entity);
							} catch (ParseException | IOException e) {
								failed(e);
								return;
							}

							if (json == null) {
								failed(new Exception("Received empty response from your server, check connections."));
								return;
							}

							JsonElement jsonElement = new JsonObject();

							try {
								jsonElement = JsonParser.parseString(json);
							} catch (Exception ex) {
								callback.result(jsonElement, ex);
								return;
							}

							callback.result(jsonElement, null);
						}

						public void failed(Exception ex) {
							callback.result(new JsonObject(), ex);
						}

						public void cancelled() {
							callback.result(new JsonObject(), new Exception("The request has been cancelled!"));
						}

					});
		} catch (Exception ex) {
			callback.result(null, ex);
		}
	}

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
			CommonGeneral.getInstance().getLogger().warning(json);
		}

		response.close();

		return jsonElement;
	}

	private HttpRequestBase createRequestBase(String url, Method method, String jsonEntity) {
		HttpRequestBase requestBase = null;

		switch (method) {
		case POST: {
			HttpPost request = new HttpPost(url);

			if (jsonEntity != null) {
				try {
					request.setEntity(new StringEntity(jsonEntity));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			requestBase = request;
			break;
		}
		case GET: {
			requestBase = new HttpGet(url);
			break;
		}
		case DELETE: {
			requestBase = new HttpDelete(url);
			break;
		}
		case PUT: {
			requestBase = new HttpPut(url);
			break;
		}
		}

		requestBase.setHeader("Accept", "application/json");
		requestBase.setHeader("Content-type", "application/json");

		return requestBase;
	}

}
