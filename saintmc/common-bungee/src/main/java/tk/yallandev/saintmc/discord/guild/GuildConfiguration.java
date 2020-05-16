package tk.yallandev.saintmc.discord.guild;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;

@Getter
public class GuildConfiguration {
	
	private long guildId;
	private boolean staffChat;
	
	private Map<String, Long> chatMap;
	private Map<String, Long> roleMap;
	
	public GuildConfiguration(long guildId) {
		try {
			HttpGet httpGet = new HttpGet(CommonConst.DISCORD_URL + "?discordId=" + guildId);
			
			httpGet.addHeader("Content-type", "application/json");
			String json = EntityUtils.toString(CommonConst.HTTPCLIENT.execute(httpGet).getEntity());
			
			if (JsonParser.parseString(json) instanceof JsonNull) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GuildConfiguration(694671881961209857l);
	}

}
