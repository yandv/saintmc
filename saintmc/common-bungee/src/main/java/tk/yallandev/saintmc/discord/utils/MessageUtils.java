package tk.yallandev.saintmc.discord.utils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ProxyServer;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.discord.DiscordMain;

public class MessageUtils {
	
	public static void sendMessage(MessageChannel messageChannel, String message, int time) {
		Message msg = messageChannel.sendMessage(message).complete();
		
		ProxyServer.getInstance().getScheduler().schedule(BungeeMain.getInstance(), () -> msg.delete().complete(), time, TimeUnit.SECONDS);
	}
	
	public static void sendMessage(MessageChannel messageChannel, String message) {
		messageChannel.sendMessage(message).complete();
	}
	
	public static void sendMessage(MessageChannel messageChannel, MessageEmbed messageEmbed, String content) {
		messageChannel.sendMessage(messageEmbed).content(content).complete();
	}
	
	public static void sendMessage(String messageChannel, Message message, boolean staffChat) {
		List<TextChannel> textChannel = DiscordMain.getInstance().getJda().getTextChannels();
		
		if (staffChat)
			textChannel = textChannel.stream().filter(channel -> DiscordMain.getInstance().getGuildManager().getGuild(channel.getGuild().getIdLong()).isStaffChat()).collect(Collectors.toList());
		
		textChannel.forEach(channel -> channel.sendMessage(message).queue());
	}
	
	public static void sendMessage(String messageChannel, MessageEmbed messageEmbed, boolean staffChat) {
		List<TextChannel> textChannel = DiscordMain.getInstance().getJda().getTextChannels();
		
		if (staffChat)
			textChannel = textChannel.stream().filter(channel -> DiscordMain.getInstance().getGuildManager().getGuild(channel.getGuild().getIdLong()).isStaffChat()).collect(Collectors.toList());
		
		textChannel.forEach(channel -> channel.sendMessage(messageEmbed).queue());
	}

}