package br.com.saintmc.hungergames.controller;

import br.com.saintmc.hungergames.constructor.SimpleKit;
import tk.yallandev.saintmc.common.controller.StoreController;

public class SimplekitController extends StoreController<String, SimpleKit> {
	
	@Override
	public boolean containsKey(String key) {
		return super.containsKey(key.toLowerCase());
	}
	
	@Override
	public void load(String key, SimpleKit value) {
		super.load(key.toLowerCase(), value);
	}
	
	@Override
	public boolean unload(String key) {
		return super.unload(key.toLowerCase());
	}
	
	@Override
	public SimpleKit getValue(String key) {
		return super.getValue(key.toLowerCase());
	}

}
