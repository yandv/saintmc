package tk.yallandev.saintmc.common.utils.web;

import org.apache.http.client.methods.HttpRequestBase;

import com.google.gson.JsonElement;

public interface WebHelper {
	
	/**
	 * used to do generic request using apache http client api
	 * 
	 * @param request
	 * @return jsonObject not null
	 * @throws Exception 
	 */
	
	JsonElement doRequest(HttpRequestBase requestBase) throws Exception;
	
	/**
	 * used to make get request using apache http client api
	 * 
	 * @param url and body json
	 * @return jsonObject not null
	 */
	
	JsonElement get(String url) throws Exception;
	
	/**
	 * used to make post request using apache http client api
	 * 
	 * @param url and body json
	 * @return jsonObject not null
	 */
	
	JsonElement post(String url, String jsonEntity) throws Exception;

}
