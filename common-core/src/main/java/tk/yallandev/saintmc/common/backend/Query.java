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

	/**
	 * 
	 * Returns all elements in MongoCollection
	 * 
	 * @return T
	 */

	Collection<T> find();

	/**
	 * 
	 * Returns all elements in MongoCollection collection
	 * 
	 * @return T
	 */

	Collection<T> find(String collection);

	/**
	 * 
	 * Returns all elements in MongoCollection filtering the key with
	 * value
	 * 
	 * @return T
	 */

	<GenericType> Collection<T> find(String key, GenericType value);
	
	/**
	 * 
	 * Returns all elements in MongoCollection collection filtering the key with
	 * value
	 * 
	 * @return T
	 */

	<GenericType> Collection<T> find(String collection, String key, GenericType value);

	<GenericType> T findOne(String key, GenericType value);

	<GenericType> T findOne(String collection, String key, GenericType value);

	/*
	 * Create
	 */

	void create(String[] jsons);

	void create(String collection, String[] jsons);

	/*
	 * Delete
	 */

	<GenericType> void deleteOne(String key, GenericType value);

	<GenericType> void deleteOne(String collection, String key, GenericType value);

	/*
	 * Update
	 */

	<GenericType> void updateOne(String key, GenericType value, T t);

	<GenericType> void updateOne(String collection, String key, GenericType value, T t);

	/*
	 * Ranking
	 */
	
	<GenericType> Collection<T> ranking(String key, GenericType value, int limit);
	
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
