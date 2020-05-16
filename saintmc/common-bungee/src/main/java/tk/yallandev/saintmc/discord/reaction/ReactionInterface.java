package tk.yallandev.saintmc.discord.reaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * 
 * @author yAllanDev_ - theNameOfDreams in camelCase
 * @since 1.1
 *
 */

public interface ReactionInterface {
	
    void onClick(User user, Guild guild, TextChannel textChannel, ReactionEmote reaction);

}
