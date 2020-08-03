package tk.yallandev.saintmc.update.updater;

public interface Updateable {

	boolean update(String api, String key);

	boolean upload(String api, String key);

}
