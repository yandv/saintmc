package tk.yallandev.saintmc.skwyars.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.Setter;

@Getter
public class StoreController<K, V> {

	@Setter
	private Map<K, V> storeMap;
	private Function<K, V> defaultFunction;

	private StoreConfig storeConfig;

	public StoreController() {
		this.storeMap = new HashMap<>();
		this.storeConfig = new StoreConfig();
	}

	public StoreController(Function<K, V> defaultFunction) {
		this.defaultFunction = defaultFunction;
		this.storeConfig = new StoreConfig();
		this.storeMap = new HashMap<>();
	}

	public StoreController(Function<K, V> defaultFunction, StoreConfig storeConfig) {
		this.defaultFunction = defaultFunction;
		this.storeConfig = storeConfig;

		if (this.storeConfig.linked)
			this.storeMap = new LinkedHashMap<>();
		else
			this.storeMap = new HashMap<>();
	}

	public void load(K key, V value) {
		if (storeConfig.replace) {
			if (!storeMap.containsKey(key))
				storeMap.put(key, value);
		} else {
			storeMap.put(key, value);
		}
	}

	public boolean unload(K key) {
		if (storeMap.containsKey(key)) {
			storeMap.remove(key);
			return true;
		}

		return false;
	}

	public boolean containsKey(K key) {
		return storeMap.containsKey(key);
	}

	public V getValue(K key) {
		if (defaultFunction == null)
			return storeMap.get(key);
		else
			return storeMap.computeIfAbsent(key, defaultFunction);
	}

	public StoreController<K, V> setDefaultFunction(Function<K, V> defaultFunction) {
		this.defaultFunction = defaultFunction;
		return this;
	}

	public long count() {
		return storeMap.size();
	}

	public long count(Predicate<? super V> predicate) {
		return storeMap.values().stream().filter(predicate).count();
	}

	public int getIndexOf(K key) {

		if (!containsKey(key))
			return -1;

		int index = 0;

		for (V value : getStoreMap().values()) {
			if (key == value)
				return index;

			index++;
		}

		return -1;
	}

	@Getter
	@Setter
	public class StoreConfig {

		private boolean replace;
		private boolean linked;

	}

}
