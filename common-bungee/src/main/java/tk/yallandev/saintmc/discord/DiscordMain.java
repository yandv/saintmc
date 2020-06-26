package tk.yallandev.saintmc.discord;

import java.util.UUID;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.CommonPlatform;
import tk.yallandev.saintmc.common.command.CommandFramework;
import tk.yallandev.saintmc.discord.command.DiscordCommandFramework;
import tk.yallandev.saintmc.discord.command.register.DiscordCommand;
import tk.yallandev.saintmc.discord.command.register.SayCommand;
import tk.yallandev.saintmc.discord.listener.BoosterListener;
import tk.yallandev.saintmc.discord.listener.MemberListener;
import tk.yallandev.saintmc.discord.listener.ReactionListener;
import tk.yallandev.saintmc.discord.manager.GuildManager;

@Getter
public class DiscordMain {

	private static DiscordMain instance;

	private JDA jda;
	private CommandFramework commandFramework;
	
	private GuildManager guildManager;
	
	public static void main(String[] args) {
		new DiscordMain();
	}
	
	public DiscordMain() {
		instance = this; 
		
		JDABuilder builder = JDABuilder.createDefault("NzIxNDUxNjQ3MjkyOTMyMjE2.XuUuWA.ywS34PZ6E9dG-U19W8nvbW4wMyY");

		builder.setActivity(Activity.playing("minecraft no saintmc.net"));

		try {
			jda = builder.build().awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		
		CommonGeneral general = new CommonGeneral(Logger.getLogger("OI"));
		general.setCommonPlatform(new CommonPlatform() {
			
			@Override
			public void runAsync(Runnable runnable) {
				runnable.run();
			}
			
			@Override
			public UUID getUuid(String playerName) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T getPlayerByUuid(UUID uniqueId, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T getPlayerByName(String playerName, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public <T> T getExactPlayerByName(String playerName, Class<T> clazz) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		commandFramework = new DiscordCommandFramework(getInstance());
		commandFramework.registerCommands(new DiscordCommand());
		commandFramework.registerCommands(new SayCommand());
		
		guildManager = new GuildManager();
		
		jda.addEventListener(new MemberListener());
		jda.addEventListener(new BoosterListener());
		jda.addEventListener(new ReactionListener(getInstance()));
	}

	public static DiscordMain getInstance() {
		return instance;
	}

}
