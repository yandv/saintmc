package tk.yallandev.saintmc.update.updater;

public interface Updateable {

	boolean update(String key);

	boolean upload(String key);

}
