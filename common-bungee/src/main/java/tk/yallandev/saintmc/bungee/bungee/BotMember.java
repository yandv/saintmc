package tk.yallandev.saintmc.bungee.bungee;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class BotMember {

	private static final int MAX_NAME = 10;

	private String ipAddress;

	private List<String> nameList;
	private long lastNameChange;

	private int nameChange;
	
	private long blockTime;

	public BotMember(String ipAddress) {
		this.ipAddress = ipAddress;
		this.nameList = new ArrayList<>();
	}

	public void addName(String name) {
		if (!this.nameList.contains(name))
			this.nameList.add(name);
	}

	public boolean tooMany() {
		return this.nameList.size() >= MAX_NAME;
	}
	
	public boolean isBlocked() {
		return blockTime > System.currentTimeMillis();
	}
	
	public void block() {
		blockTime = System.currentTimeMillis() + (1000 * 60 * 60 * 30l);
	}
	
}
