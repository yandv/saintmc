package tk.yallandev.saintmc.discord.listener;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.reaction.MessageReaction;
import tk.yallandev.saintmc.discord.reaction.ReactionEnum;
import tk.yallandev.saintmc.discord.reaction.ReactionHandler;
import tk.yallandev.saintmc.discord.reaction.ReactionHandler.ReactionAction;

@Getter
public class ReactionListener extends ListenerAdapter {

	private static final HashMap<String, MessageReaction> REACTIONS = new HashMap<>();

	private DiscordMain manager;

	public ReactionListener(DiscordMain manager) {
		this.manager = manager;

		Map<ReactionEnum, ReactionHandler> handlers = new HashMap<>();

		handlers.put(ReactionEnum.PENCIL, (user, guild, textChannel, reaction, action) -> {

			user.openPrivateChannel().complete().sendMessage(new EmbedBuilder().setColor(Color.YELLOW)
					.appendDescription(ReactionEnum.PENCIL.getEmote()
							+ " Para ingressar na equipe é necessário que você demonstre habilidades em moderar um servidor, então seja sempre"
							+ " ativo no servidor e faça o formulario:"
							+ "\nTrial-Moderador: " + CommonConst.TRIAL_FORM
							+ "\nHelper: " + CommonConst.HELPER_FORM)
					.setColor(Color.YELLOW).build()).complete();

		});

		handlers.put(ReactionEnum.PROJECTOR, (user, guild, textChannel, reaction, action) -> {

			user.openPrivateChannel().complete().sendMessage(new EmbedBuilder().setColor(Color.YELLOW)
					.appendDescription(
							ReactionEnum.PROJECTOR.getEmote() + " Preencha o formulário " + CommonConst.YOUTUBER_FORM)
					.setColor(Color.YELLOW).build()).complete();

		});

		handlers.put(ReactionEnum.LINK, (user, guild, textChannel, reaction, action) -> {

			user.openPrivateChannel().complete().sendMessage(new EmbedBuilder().setColor(Color.YELLOW)
					.appendDescription(ReactionEnum.LINK.getEmote()
							+ " Se você foi banido injustamente, preencha o formulário: " + CommonConst.APPEAL_FORM)
					.setColor(Color.YELLOW).build()).complete();

		});

		createMessage(708356198700810271l, new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.YELLOW)
				.appendDescription(ReactionEnum.STAR.getEmote() + " Selecione a opção que você gostaria.\r\n" + "\r\n"
						+ ReactionEnum.PENCIL.getEmote() + " Aplique para equipe\r\n"
						+ ReactionEnum.PROJECTOR.getEmote() + " Aplique para Tag Youtuber\r\n"
						+ ReactionEnum.LINK.getEmote() + " Aplique para APPEAL (banido injustamente)\r\n" + "\r\n"
						+ "⋅ Nosso bot enviará uma mensagem em seu privado com todas as informações necessárias, mas caso tenha alguma duvida contate um membro da equipe.")
				.build()), handlers, true);

		handlers = new HashMap<>();

		handlers.put(ReactionEnum.RIGHT_POINTING_MAGNIFYING_GLASS, (user, guild, textChannel, reaction, action) -> {

			user.openPrivateChannel().complete().sendMessage("Para denunciar um bug, dirija-se ao yandv (Allan#2856)!")
					.complete();

		});

		handlers.put(ReactionEnum.WRENCH, (user, guild, textChannel, reaction, action) -> {

			user.openPrivateChannel().complete().sendMessage("Para denunciar um bug, dirija-se ao yandv (Allan#2856)!")
					.complete();

		});

		createMessage(708356151301111890l, new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.YELLOW)
				.appendDescription(ReactionEnum.STAR.getEmote() + " Selecione a melhor opção pra você\r\n" + "\r\n"
						+ ReactionEnum.WRENCH.getEmote() + " Denuncie um bug\r\n"
						+ ReactionEnum.RIGHT_POINTING_MAGNIFYING_GLASS.getEmote() + " Denuncie um jogador\r\n" + "\r\n"
						+ "⋅ Nosso bot adicionará/removerá o role <@&708384460776931329> para que você seja notificado em nosso discord!")
				.build()), handlers, true);

		handlers = new HashMap<>();

		handlers.put(ReactionEnum.WRENCH, (user, guild, textChannel, reaction, action) -> {
			Member member = guild.retrieveMember(user).complete();

			boolean notify = false;
			Role notifyRole = guild.getRoleById(708384460776931329l);

			for (Role role : member.getRoles()) {
				if (role == notifyRole) {
					notify = true;
					break;
				}
			}

			if (notify)
				guild.removeRoleFromMember(member, notifyRole).complete();
			else
				guild.addRoleToMember(member, notifyRole).complete();
		});

		createMessage(722800140217352223l, new MessageBuilder().setEmbed(new EmbedBuilder()
				.setAuthor("SaintMC", CommonConst.WEBSITE,
						"https://cdn.discordapp.com/attachments/535167734208790539/722800695337812028/logo-128px.png")
				.appendDescription(ReactionEnum.STAR.getEmote() + " Selecione a melhor opção pra você\n" + "\n"
						+ ReactionEnum.WRENCH.getEmote() + " Para ativar/desativar as notificações\n\n"
						+ "⋅ Nosso bot enviará uma mensagem em seu privado com todas as informações necessárias, mas caso tenha alguma duvida contate um membro da equipe.")
				.build()), handlers, false);
	}

	public void createMessage(long channelId, MessageBuilder messageBuilder,
			Map<ReactionEnum, ReactionHandler> handlers, boolean removeReaction) {
		Preconditions.checkArgument(!handlers.isEmpty(), "Handlers Map is empty");

		TextChannel textChannel = DiscordMain.getInstance().getJda().getTextChannelById(channelId);

		MessageHistory history = textChannel.getHistory();
		Message theMessage = null;

		for (Message message : history.retrievePast(100).complete()) {
			if (message.getAuthor().isBot()) {
				theMessage = message;
				continue;
			}

			message.delete().queue();
		}

		if (theMessage == null)
			theMessage = textChannel.sendMessage(messageBuilder.build()).complete();
		else if (removeReaction)
			for (net.dv8tion.jda.api.entities.MessageReaction reaction : theMessage.getReactions())
				reaction.clearReactions().queue();

		MessageReaction messageReaction = new MessageReaction(theMessage, removeReaction);

		for (Entry<ReactionEnum, ReactionHandler> entry : handlers.entrySet())
			messageReaction.addReaction(entry.getKey(), entry.getValue());
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
				message.getReactions().get(reaction.getName()).onClick(user, event.getGuild(), event.getTextChannel(),
						reaction, ReactionAction.ADD);
		} catch (Exception ex) {
			/*
			 * This error occurs out of nowhere http://prntscr.com/otpqbu
			 */
		}
	}

//	@Override
//	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
//		try {
//			if (!REACTIONS.containsKey(event.getMessageId()))
//				return;
//
//			User user = event.getUser();
//			
//
//			MessageReaction message = REACTIONS.get(event.getMessageId());
//			ReactionEmote reaction = event.getReactionEmote();
//
//			if (message.getReactions().containsKey(reaction.getName()))
//				message.getReactions().get(reaction.getName()).onClick(user, event.getGuild(), event.getTextChannel(),
//						reaction, ReactionAction.REMOVE);
//		} catch (Exception ex) {
//			/*
//			 * This error occurs out of nowhere http://prntscr.com/otpqbu
//			 */
//		}
//	}

	public static HashMap<String, MessageReaction> getMessageReactions() {
		return REACTIONS;
	}

}
