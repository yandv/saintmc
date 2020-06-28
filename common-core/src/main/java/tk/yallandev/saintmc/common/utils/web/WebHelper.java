package tk.yallandev.saintmc.common.utils.web;

import com.google.gson.JsonElement;

import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

/**
 * 
 * TODO Tenho que documentar!
 * 
 * @author yandv
 */

public interface WebHelper {

	/*
	 * 
	 */

	JsonElement doRequest(String url, Method method) throws Exception;
	
	JsonElement doRequest(String url, Method method, String jsonEntity) throws Exception;
	
	void doRequest(String url, Method method, FutureCallback<JsonElement> callback);
	
	void doRequest(String url, Method method, String jsonEntity, FutureCallback<JsonElement> callback);

	/*
	 * Callback
	 */
	
	void doAsyncRequest(String url, Method method, FutureCallback<JsonElement> callback);

	void doAsyncRequest(String url, Method method, String jsonEntity, FutureCallback<JsonElement> callback);
	
	/*
	 * Create
	 */
	
	public enum Method {
		
		POST, DELETE, GET, PUT
		
	}

}
