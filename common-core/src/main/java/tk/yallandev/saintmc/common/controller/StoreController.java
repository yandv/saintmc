package tk.yallandev.saintmc.common.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

public class StoreController<K, V> {

	@Getter
	private Map<K, V> storeMap;
	private Function<K, V> defaultFunction;

	@Getter
	private StoreConfig storeConfig;

	public StoreController() {
		this.storeMap = new HashMap<>();
		this.storeConfig = new StoreConfig();
	}

	public StoreController(Function<K, V> defaultFunction) {
		this.storeMap = new HashMap<>();
		this.defaultFunction = defaultFunction;
		this.storeConfig = new StoreConfig();
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

	@Getter
	@Setter
	public class StoreConfig {

		private boolean replace;

	}

}
