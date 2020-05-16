package tk.yallandev.saintmc.common.music;

public enum MusicType {
	
	WON("won"), MVP("mvp"), DEATH("death");
	
	private String music;
	
	private MusicType(String music) {
		this.music = music;
	}
	
	public String getMusic() {
		return music;
	}

}
