package tk.yallandev.saintmc.discord.guild;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

@Getter
public class GuildConfiguration {
	
	private long guildId;
	private boolean staffChat;
	
	private Map<String, Long> chatMap;
	private Map<String, Long> roleMap;
	
	public GuildConfiguration(long guildId) {
		try {
			JsonElement json = CommonConst.DEFAULT_WEB.doRequest(CommonConst.DISCORD_URL + "?discordId=" + guildId, Method.GET);
			
			if (json instanceof JsonNull) {
				this.guildId = guildId;
				this.staffChat = false;
				this.chatMap = new HashMap<>();
				this.roleMap = new HashMap<>();
				
				System.out.println("Guild " + guildId + " created!");
				return;
			}
			
			GuildConfiguration guild = CommonConst.GSON.fromJson(json, GuildConfiguration.class);
			
			System.out.println("Guild " + guildId + " loaded!");
			
			this.guildId = guild.getGuildId();
			this.chatMap = guild.getChatMap();
			this.roleMap = guild.getRoleMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GuildConfiguration(694671881961209857l);
	}

}
