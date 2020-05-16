package tk.yallandev.saintmc.discord.reaction;

import java.util.HashMap;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.listener.ReactionListener;

/**
 * @author yAllanDev_ - theNameOfDreams in camelCase
 * @since 1.1
 */

public class MessageReaction {

    private Message message;
    private boolean removeReaction;
    private HashMap<String, ReactionInterface> reactions;

    @Getter
    private boolean removed = false;

    public MessageReaction(Message message, boolean removeReaction) {
        this.message = message;
        this.removeReaction = removeReaction;
        this.reactions = new HashMap<>();
        ReactionListener.getMessageReactions().put(message.getId(), this);
    }

    public MessageReaction(String message, boolean removeReaction) {
        this(new MessageBuilder().append(message).build(), removeReaction);
    }

    public MessageReaction(EmbedBuilder embedBuilder, boolean removeReaction) {
        this(new MessageBuilder().setEmbed(embedBuilder.build()).build(), removeReaction);
    }

    public MessageReaction addReaction(String reactionEmote, ReactionInterface reactionInterface) {
        if (removed) {
            throw new IllegalArgumentException("You cant make this when MessageReaction is closed");
        }

        message.addReaction(reactionEmote).complete();
        this.reactions.put(reactionEmote, reactionInterface);
        return this;
    }

    public MessageReaction addReaction(String reactionEmote) {
        if (removed) {
            throw new IllegalArgumentException("You cant make this when MessageReaction is closed");
        }

        message.addReaction(reactionEmote).complete();
        this.reactions.put(reactionEmote, new ReactionInterface() {

            @Override
            public void onClick(User user, Guild guild, TextChannel textChannel, ReactionEmote reaction) {

            }

        });

        return this;
    }

    public MessageReaction addReaction(Emote emote, ReactionInterface reactionInterface) {
        if (removed) {
            throw new IllegalArgumentException("You cant make this when MessageReaction is closed");
        }

        message.addReaction(emote).complete();
        this.reactions.put(emote.getName(), reactionInterface);
        return this;
    }

    public MessageReaction addReaction(ReactionEnum reactionEnum, ReactionInterface reactionInterface) {
        return reactionEnum.isCustomEmote() ? addReaction(DiscordMain.getInstance().getJda().getEmoteById(reactionEnum.getEmoteId()), reactionInterface) : addReaction(reactionEnum.getEmote(), reactionInterface);
    }

    public MessageReaction addReaction(Emote emote) {
        return addReaction(emote, new ReactionInterface() {

            @Override
            public void onClick(User user, Guild guild, TextChannel textChannel, ReactionEmote reaction) {
            }

        });
    }

    public Message getMessage() {
        return message;
    }

    public boolean isRemoveReaction() {
        return removeReaction;
    }

    public void setRemoveReaction(boolean removeReaction) {
        this.removeReaction = removeReaction;
    }

    public HashMap<String, ReactionInterface> getReactions() {
        return reactions;
    }

    public void remove() {
        ReactionListener.getMessageReactions().remove(message.getId());
        this.removed = true;
    }

}
