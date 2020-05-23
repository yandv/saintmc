package tk.yallandev.saintmc.common.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;

public class StoreController<K, V> {

	@Getter
	private Map<K, V> storeMap;
	private Function<K, V> defaultFunction;

	public StoreController() {
		this.storeMap = new HashMap<>();
	}

	public StoreController(Function<K, V> defaultFunction) {
		this.storeMap = new HashMap<>();
		this.defaultFunction = defaultFunction;
	}

	public void load(K key, V value) {
		if (!storeMap.containsKey(key))
			storeMap.put(key, value);
	}

	public void unload(K key) {
		if (storeMap.containsKey(key))
			storeMap.remove(key);
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

}
