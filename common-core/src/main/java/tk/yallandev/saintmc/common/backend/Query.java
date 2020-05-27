package tk.yallandev.saintmc.common.backend;

import java.util.Collection;

import lombok.Getter;
import tk.yallandev.saintmc.common.utils.supertype.Callback;

/**
 * 
 * Query manager will help abstracting all databases methods of query
 * 
 * @author Allan
 *
 */

public interface Query<T> {
	
	/*
	 * Find
	 */
	
	Collection<T> find();
	
	Collection<T> find(String collection);
	
	Collection<T> find(String key, String value);
	
	Collection<T> find(String collection, String key, String value);
	
	T findOne(String key, String value);
	
	T findOne(String collection, String key, String value);
	
	/*
	 * Find
	 */
	
	void find(QueryResponse<Collection<T>> response);
	
	void find(String collection, QueryResponse<Collection<T>> response);
	
	void find(String key, String value, QueryResponse<Collection<T>> response);
	
	void find(String collection,String key, String value, QueryResponse<Collection<T>> response);
	
	void findOne(String key, String value, QueryResponse<T> response);
	
	void findOne(String collection, String key, String value, QueryResponse<T> response);
	
	/*
	 * Create
	 */
	
	void create(String[] jsons);
	
	void create(String collection, String[] jsons);
	
	/*
	 * Delete
	 */
	
	void deleteOne(String key, String value);
	
	void deleteOne(String collection,String key, String value);
	
	/*
	 * Update
	 */
	
	void updateOne(String key, String value, T t);
	
	void updateOne(String collection,String key, String value, T t);
	
	/*
	 * Response
	 */
	
	@Getter
	public class QueryResponse<T> {
		
		private long startTime;
		private long durationTime;
		
		private Callback<T> callback;
		
		public QueryResponse(Callback<T> callback) {
			this.startTime = System.currentTimeMillis();
			this.callback = callback;
		}

		public void callback(T t) {
			callback.callback(t);
		}
		
	}

}
