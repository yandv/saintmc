package tk.yallandev.saintmc.discord.reaction;

import net.dv8tion.jda.api.entities.Emote;
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

public class Reaction {

    private ReactionInterface handler;
    private Emote emote;

    public Reaction(Emote emote) {
        this.emote = emote;
        this.handler = new ReactionInterface() {

            @Override
            public void onClick(User user, Guild guild, TextChannel textChannel, ReactionEmote reaction) {

            }
        };
    }

    public Reaction(Emote emote, ReactionInterface handler) {
        this.emote = emote;
        this.handler = handler;
    }

    public ReactionInterface getHandler() {
        return handler;
    }

    public Emote getEmote() {
        return emote;
    }

}
