package tk.yallandev.saintmc.bungee.command.register;

import java.awt.Color;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.command.BungeeCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.command.register.DiscordCommand.Invite;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;
import tk.yallandev.saintmc.discord.reaction.MessageReaction;
import tk.yallandev.saintmc.discord.reaction.ReactionEnum;
import tk.yallandev.saintmc.discord.reaction.ReactionInterface;
import tk.yallandev.saintmc.discord.utils.MessageUtils;

public class DiscordCommand implements CommandClass {

	@Command(name = "discord")
	public void discordCommand(BungeeCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			return;
		}

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/discord sync <discordId>§f para sincronizar o discord com o servidor!");
			sender.sendMessage(
					" §e* §fUse §a/discord accept <discordId>§f para aceitar a sincronização com o discord!");
			sender.sendMessage(" §e* §fUse §a/discord desync <discordId>§f para sincronizar o discord com o servidor!");
			sender.sendMessage(
					" §e* §fUse §a/discord update <server/discord/booster>§f para sincronizar o discord com o servidor!");

			if (member.hasDiscord()) {
				sender.sendMessage(
						" §e* §fUse §a/discord info§f para ver as informações sobre a sincronização com o discord!");
			}

			return;
		}

		switch (args[0].toLowerCase()) {
		case "sync": {
			if (member.hasDiscord()) {
				member.sendMessage(" §c* §fO seu discord já está sincronizado!");
				member.sendMessage(
						" §e* §a/discord desync <discordId>§f para dessincronizar o discord com o servidor!");
			} else {

				if (member.isOnCooldown("discord-sync-delay")) {
					member.sendMessage(" §c* §fVocê precisa esperar §e"
							+ DateUtils.getTime(member.getCooldown("discord-sync-delay"))
							+ "§f para executar esse comando novamente!");
					return;
				}

				GuildConfiguration config = DiscordMain.getInstance().getGuildManager().getGuild();

				if (config == null) {
					member.sendMessage(" §c* §fO servidor não conseguiu carregar o discord!");
					return;
				}

				Guild guild = DiscordMain.getInstance().getJda().getGuildById(config.getGuildId());

				if (guild == null) {
					member.sendMessage(" §c* §fO servidor não possui discord!");
					return;
				}

				TextChannel textChannel;

				if (config.getChatMap().containsKey("command")) {
					textChannel = guild.getTextChannelById(config.getChatMap().get("command"));
				} else {
					textChannel = guild.getTextChannels().stream().findFirst().orElse(null);
				}

				if (textChannel == null) {
					member.sendMessage(" §c* §fO servidor não possui chat!");
					return;
				}

				net.dv8tion.jda.api.entities.Member dMember = guild
						.getMembersByEffectiveName(args[1].replace("#", ""), true).stream().findFirst().orElse(null);

				if (dMember == null) {
					try {
						dMember = guild.getMemberById(args[1]);
					} catch (NumberFormatException ex) {
						member.sendMessage(" §c* §fNão foi possível achar o seu discord!");
						return;
					}

					if (dMember == null) {
						member.sendMessage(" §c* §fNão foi possível achar o seu discord!");
						return;
					}
				}

				net.dv8tion.jda.api.entities.Member discordMember = dMember;

				Message message = textChannel
						.sendMessage(new EmbedBuilder()
								.setAuthor("Vincular com discord - " + member.getPlayerName(),
										CommonConst.WEBSITE + "perfil?nick=" + member.getPlayerName(),
										"https://minotar.net/avatar/" + member.getPlayerName())
								.appendDescription(ReactionEnum.TAG_SPACE.getEmote() + "\nClique em "
										+ ReactionEnum.COOKIE.getEmote()
										+ " para aceitar a solicitação para vincular o discord!")
								.setTimestamp(Instant.now())
								.setThumbnail("https://minotar.net/avatar/" + member.getPlayerName())
								.setColor(Color.YELLOW).setFooter("Enviado pela conta " + member.getPlayerName())
								.build())
						.content("<@" + discordMember.getUser().getIdLong() + ">").complete();

				MessageReaction messageReaction = new MessageReaction(message, true);

				ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(BungeeMain.getInstance(),
						new Runnable() {

							@Override
							public void run() {
								if (!messageReaction.isRemoved())
									try {
										message.delete().complete();
									} catch (Exception ex) {
									}
							}
						}, 2, TimeUnit.MINUTES);

				messageReaction.addReaction(ReactionEnum.COOKIE.getEmote(), new ReactionInterface() {

					@Override
					public void onClick(User user, Guild guild, TextChannel textChannel, ReactionEmote reaction) {
						if (user.getIdLong() == discordMember.getIdLong()) {

							member.setDiscordId(user.getIdLong(), user.getName() + "#" + user.getDiscriminator());
							member.sendMessage(" §a* §fVocê sincronizou o discord com o servidor!");

							if (discordMember.getTimeBoosted() != null) {
								member.sendMessage("§a§l> §fObrigado por ajudar o discord doando §d§lBOOST§f!");
								member.sendMessage("§a§l> §fVocê recebeu a tag " + Tag.DONATOR.getPrefix() + "§f!");

								member.getRanks().put(RankType.DONATOR,
										System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
								member.setTag(Tag.valueOf(RankType.DONATOR.name()));
								member.saveRanks();
							}

							task.cancel();
							message.delete().complete();
						}
					}
				});

				member.setCooldown("discord-sync-delay", System.currentTimeMillis() + (1000 * 60 * 30));
			}
			break;
		}
		case "desync": {
			if (member.hasDiscord()) {
				if (member.isOnCooldown("discord-desync")) {
					member.sendMessage(" §c* §fSua conta foi desvinculada com o discord!");
					member.removeCooldown("discord-desync");
					member.setDiscordId(0l, "");
				} else {
					member.sendMessage(" §c* §fPara desvincular o discord digite §c/discord desync§f novamente!");
				}
			} else {
				member.sendMessage(" §c* §fSua conta ainda não é sincronizada com o discord!");
				member.sendMessage(" §e* §a/discord sync <discordId>§f para sincronizar o discord com o servidor!");
				member.sendMessage(
						" §a* §fEntre no nosso discord §a" + CommonConst.DISCORD + "§f e digite §a/discord sync "
								+ member.getPlayerName() + "§f para sincronizar com o servidor!");
			}
			break;
		}
		case "update": {
			if (!member.hasDiscord()) {
				member.sendMessage(" §c* §fVocê precisa sincronizar sua conta antes de executar essa ação!");
				member.sendMessage(" §e* §a/discord sync <discordId>§f para sincronizar o discord com o servidor!");
				return;
			}

			if (args.length < 2) {
				sender.sendMessage(
						" §e* §fUse §a/discord update <server/discord/booster>§f para sincronizar o discord com o servidor!");
				return;
			}

			if (args[1].equalsIgnoreCase("servidor")) {
				net.dv8tion.jda.api.entities.Member discordMember = DiscordMain.getInstance().getJda()
						.getGuildById(694671881961209857l).getMemberById(member.getDiscordId());

				if (discordMember.getTimeBoosted() != null) {
					if (!member.getRanks().containsKey(RankType.DONATOR)) {
						member.sendMessage("§a§l> §fObrigado por ajudar o discord doando §d§lBOOST§f!");
						member.sendMessage("§a§l> §fVocê recebeu a tag " + Tag.DONATOR.getPrefix() + "§f!");

						member.getRanks().put(RankType.DONATOR,
								System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
						member.setTag(Tag.valueOf(RankType.DONATOR.name()));
						member.saveRanks();
					}
				}
			} else {
				member.sendMessage(" §e* §fEsse comando está disponível no §3discord§f!");
			}
			break;
		}
		case "accept": {
			if (member.hasDiscord()) {

				member.sendMessage(" §c* §fO seu discord já está sincronizado!");
				member.sendMessage(
						" §e* §a/discord desync <discordId>§f para dessincronizar o discord com o servidor!");

				return;
			}

			Long discordId = null;

			try {
				discordId = Long.valueOf(args[1]);
			} catch (NumberFormatException ex) {
				return;
			}

			if (tk.yallandev.saintmc.discord.command.register.DiscordCommand.MAP.containsKey(member.getUniqueId())) {
				Map<Long, Invite> map = tk.yallandev.saintmc.discord.command.register.DiscordCommand.MAP
						.get(member.getUniqueId());

				if (map.containsKey(discordId)) {
					Invite invite = map.get(discordId);

					if (invite.getExpireTime() > System.currentTimeMillis()) {

						member.setDiscordId(discordId, invite.getDiscordName());
						member.sendMessage(" §a* §fVocê sincronizou o discord com o servidor!");

						if (invite.isBooster()) {
							member.sendMessage("§a§l> §fObrigado por ajudar o discord doando §d§lBOOST§f!");
							member.sendMessage("§a§l> §fVocê recebeu a tag " + Tag.DONATOR.getPrefix() + "§f!");

							member.getRanks().put(RankType.DONATOR,
									System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
							member.setTag(Tag.valueOf(RankType.DONATOR.name()));
							member.saveRanks();
						}

						MessageUtils.sendMessage(
								DiscordMain.getInstance().getJda().getTextChannelById(invite.getChatId()),
								new EmbedBuilder()
										.setAuthor("Vinculação com discord - " + member.getPlayerName(),
												CommonConst.WEBSITE + "perfil?nick=" + member.getPlayerName(),
												"https://minotar.net/avatar/" + member.getPlayerName())
										.appendDescription(ReactionEnum.TAG_SPACE.getEmote()
												+ "\nSua conta foi vinculada com sucesso!")
										.setTimestamp(Instant.now()).setColor(Color.GREEN)
										.setThumbnail("https://minotar.net/avatar/" + member.getPlayerName())
										.setFooter("Enviado pela conta " + member.getPlayerName()).build(),
								"<@" + invite.getUserId() + ">");

						tk.yallandev.saintmc.discord.command.register.DiscordCommand.MAP.remove(member.getUniqueId());
					} else {
						member.sendMessage(" §c* §fO pedido de sincronização expirou!");
					}
				} else {
					member.sendMessage(" §c* §fVocê não recebeu pedidos de sincronização desse discord!");
				}
			} else {
				member.sendMessage(" §c* §fVocê não possui pedidos de sincronização!");
			}

			break;
		}
		}
	}

}
