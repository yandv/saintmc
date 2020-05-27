package br.com.saintmc.updater.plugin;

import br.com.saintmc.updater.update.Updateable;

public class Plugin implements Updateable {

	@Override
	public boolean download() {
		return false;
	}

	@Override
	public boolean upload() {
		return false;
	}

}
