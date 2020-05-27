package tk.yallandev.saintmc.common.backend;

public interface Database {
	
	/*
	 * Connection Manager
	 */
	
	void connect() throws Exception;
	
	boolean isConnected();
	
	void close();
	
}
