package tk.yallandev.saintmc.common.utils.supertype;

public interface FutureCallback<T> {

	void result(T result, Throwable error);

}