package tk.yallandev.saintmc.discord;

import javax.security.auth.login.LoginException;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tk.yallandev.saintmc.common.command.CommandFramework;
import tk.yallandev.saintmc.discord.command.DiscordCommandFramework;
import tk.yallandev.saintmc.discord.command.register.SayCommand;
import tk.yallandev.saintmc.discord.listener.MemberListener;
import tk.yallandev.saintmc.discord.listener.ReactionListener;
import tk.yallandev.saintmc.discord.manager.GuildManager;

@Getter
public class DiscordMain {

	private static DiscordMain instance;

	private JDA jda;
	private CommandFramework commandFramework;

	private GuildManager guildManager;

	public DiscordMain() {
		instance = this;

		JDABuilder builder = JDABuilder.createDefault("NzIxNDUxNjQ3MjkyOTMyMjE2.XuUuWA.ywS34PZ6E9dG-U19W8nvbW4wMyY");

		builder.setActivity(Activity.playing("minecraft no clouth-network.com.br"));

		try {
			jda = builder.build().awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}

		commandFramework = new DiscordCommandFramework(getInstance());
		commandFramework.registerCommands(new SayCommand());

		guildManager = new GuildManager();

		jda.addEventListener(new MemberListener());
		jda.addEventListener(new ReactionListener(getInstance()));
	}

	public static DiscordMain getInstance() {
		return instance;
	}

}
