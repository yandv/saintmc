package tk.yallandev.saintmc.discord;

import javax.security.auth.login.LoginException;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.command.CommandFramework;
import tk.yallandev.saintmc.discord.command.DiscordCommandFramework;
import tk.yallandev.saintmc.discord.command.register.DiscordCommand;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;
import tk.yallandev.saintmc.discord.listener.BoosterListener;
import tk.yallandev.saintmc.discord.listener.ReactionListener;
import tk.yallandev.saintmc.discord.manager.GuildManager;

@Getter
public class DiscordMain {

	private static DiscordMain instance;

	private JDA jda;
	private CommandFramework commandFramework;
	
	private GuildManager guildManager;
	
	public static void main(String[] args) {
		DiscordMain discord = new DiscordMain();
		
		GuildConfiguration guild = discord.getGuildManager().getGuild();
		
		if (guild == null) {
			System.out.println("null");
		} else {
			System.out.println(CommonConst.GSON.toJson(guild));
		}
	}
	
	public DiscordMain() {
		instance = this; 
		
		JDABuilder builder = JDABuilder.createDefault("Njk2MDgyNjA2MDg4OTEyOTE2.Xo9hhw.yD-o7jFhvYKb2sdoi4oe_IIyYhs");

		builder.setBulkDeleteSplittingEnabled(false);
		builder.setCompression(Compression.NONE);
		builder.setActivity(Activity.playing("minecraft no saintmc.com.br"));

		try {
			jda = builder.build().awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}

		this.commandFramework = new DiscordCommandFramework(getInstance());
		this.commandFramework.registerCommands(new DiscordCommand());
		
		this.guildManager = new GuildManager();
		
		jda.addEventListener(new BoosterListener());
		jda.addEventListener(new ReactionListener(getInstance()));
	}

	public static DiscordMain getInstance() {
		return instance;
	}

}
