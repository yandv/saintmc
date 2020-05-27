package tk.yallandev.saintmc.discord.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;

public class GuildManager {
	
	private Map<Long, GuildConfiguration> guildMap;
	
	public GuildManager() {
		guildMap = new HashMap<>();
		
		try {
			JsonArray jsonArray = (JsonArray) CommonConst.DEFAULT_WEB.doRequest(CommonConst.DISCORD_URL, Method.GET);
			
			for (int x = 0; x < jsonArray.size(); x++) {
				GuildConfiguration guild = CommonConst.GSON.fromJson(jsonArray.get(x), GuildConfiguration.class);
				
				guildMap.put(guild.getGuildId(), guild);
				System.out.println(guild.getGuildId());
				
				for (Entry<String, Long> entry : guild.getChatMap().entrySet()) {
					System.out.println(entry.getKey());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadGuild(long guildId, GuildConfiguration guildConfiguration) {
		guildMap.put(guildId, guildConfiguration);
	}
	
	public GuildConfiguration getGuildStaff() {
		for (Entry<Long, GuildConfiguration> entry : guildMap.entrySet()) {
			if (entry.getValue().isStaffChat()) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public GuildConfiguration getGuild() {
		for (Entry<Long, GuildConfiguration> entry : guildMap.entrySet()) {
			if (!entry.getValue().isStaffChat()) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public GuildConfiguration getGuild(long guildId) {
		return guildMap.computeIfAbsent(guildId, v -> new GuildConfiguration(guildId));
	}
	
	public static void main(String[] args) {
		new GuildManager();
	}

}
