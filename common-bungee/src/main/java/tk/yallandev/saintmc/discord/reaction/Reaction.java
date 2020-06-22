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

    private ReactionHandler handler;
    private Emote emote;

    public Reaction(Emote emote) {
        this.emote = emote;
        this.handler = new ReactionHandler() {

            @Override
            public void onClick(User user, Guild guild, TextChannel textChannel, ReactionEmote reaction, ReactionAction action) {

            }
        };
    }

    public Reaction(Emote emote, ReactionHandler handler) {
        this.emote = emote;
        this.handler = handler;
    }

    public ReactionHandler getHandler() {
        return handler;
    }

    public Emote getEmote() {
        return emote;
    }

}
