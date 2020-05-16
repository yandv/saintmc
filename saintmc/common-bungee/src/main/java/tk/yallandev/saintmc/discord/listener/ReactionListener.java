package tk.yallandev.saintmc.discord.listener;

import java.util.HashMap;

import lombok.Getter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.reaction.MessageReaction;

@Getter
public class ReactionListener extends ListenerAdapter {

	private static final HashMap<String, MessageReaction> REACTIONS = new HashMap<>();

	private DiscordMain manager;

	public ReactionListener(DiscordMain manager) {
		this.manager = manager;
	}
	
	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		if (!REACTIONS.containsKey(event.getMessageId()))
			return;
		
		REACTIONS.remove(event.getMessageId());
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		try {
		    if (!REACTIONS.containsKey(event.getMessageId()))
	            return;

	        User user = event.getUser();

	        if (user.isBot())
	            return;
	        
	        MessageReaction message = REACTIONS.get(event.getMessageId());
	        ReactionEmote reaction = event.getReactionEmote();
	        
	        if (event.getChannelType() != ChannelType.PRIVATE)
	            if (message.isRemoveReaction())
	                event.getReaction().removeReaction(event.getUser()).complete();

	        if (message.getReactions().containsKey(reaction.getName()))
	            message.getReactions().get(reaction.getName()).onClick(user, event.getGuild(), event.getTextChannel(), reaction);
		} catch (Exception ex) {
		    /*
		     * This error occurs out of nowhere
		     * http://prntscr.com/otpqbu
		     */
		}
	}

	public static HashMap<String, MessageReaction> getMessageReactions() {
		return REACTIONS;
	}

}
