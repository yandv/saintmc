package tk.yallandev.saintmc.discord.command;

import java.util.UUID;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.chat.BaseComponent;
import tk.yallandev.saintmc.common.command.CommandSender;

@Getter
public class DiscordCommandSender implements CommandSender {

	private User user;
	private MessageChannel messageChannel;
	private Guild guild;

	public DiscordCommandSender(User user, MessageChannel messageChannel, Guild guild) {
		this.user = user;
		this.messageChannel = messageChannel;
		this.guild = guild;
	}

	public boolean isPlayer() {
		return !user.isBot();
	}

	public Member getAsMember(Guild guild) {
		return guild.getMember(getUser());
	}

	public Member getAsMember() {
		return getGuild().getMember(getUser());
	}

	@Override
	public UUID getUniqueId() {
		return null;
	}
	
	@Override
	public String getName() {
		return user.getName() + "#" + user.getDiscriminator();
	}

	@Override
	public void sendMessage(String str) {
		messageChannel.sendMessage(str).queue();
	}

	@Override
	public void sendMessage(BaseComponent str) {
		messageChannel.sendMessage(str.toLegacyText()).queue();
	}

	@Override
	public void sendMessage(BaseComponent[] fromLegacyText) {
		
	}

}