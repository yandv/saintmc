package tk.yallandev.saintmc.common.backend;

public interface Database {
	
	/*
	 * Connection Manager
	 */
	
	void connect();
	
	boolean isConnected();
	
	void close();
	
}
