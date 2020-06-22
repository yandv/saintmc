package tk.yallandev.saintmc.discord.command.register;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.DiscordType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.command.DiscordCommandArgs;
import tk.yallandev.saintmc.discord.command.DiscordCommandSender;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;

public class DiscordCommand implements CommandClass {

	public static final Map<UUID, Map<Long, Invite>> MAP = new HashMap<>();

	@Command(name = "discord", runAsync = true)
	public void discordCommand(DiscordCommandArgs cmdArgs) {
		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("Use /discord sync <playerName> para sincronizar o discord com o servidor!"
					+ "\nUse /discord group para sincronizar o discord com o servidor!\nUse /discord update <server/discord/booster> para atualizar os grupos do server/discord!");
			return;
		}

		switch (args[0].toLowerCase()) {
		case "update": {
			if (args.length < 2) {
				sender.sendMessage(
						"Use /discord update <server/discord/booster> para atualizar os grupos do server/discord!");
				return;
			}

			Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

			if (member == null) {
				MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
						.loadMember(sender.getUser().getIdLong());

				if (memberModel == null) {
					sender.sendMessage("Você precisa sincronizar sua conta antes de executar essa ação!"
							+ "\nUse /discord sync <playerName> para sincronizar o discord com o servidor!");
					return;
				}

				member = new MemberVoid(memberModel);
			}

			if (args[1].equalsIgnoreCase("discord")) {
				if (member.getDiscordType() == DiscordType.ROLE_SYNCRONIZED) {
					if (DiscordMain.getInstance().getGuildManager().getGuild(sender.getGuild().getIdLong())
							.isStaffChat()) {
						sender.sendMessage("Esse comando não é permitido no staffchat!");
					} else {
						Guild guild = sender.getGuild();
						net.dv8tion.jda.api.entities.Member discordMember = sender.getAsMember();
						GuildConfiguration configuration = DiscordMain.getInstance().getGuildManager()
								.getGuild(sender.getGuild().getIdLong());

						List<String> addedList = new ArrayList<>();
						List<String> removedList = new ArrayList<>();

						for (Entry<String, Long> roleEntry : configuration.getRoleMap().entrySet()) {
							try {
								if (discordMember.getRoles()
										.removeIf(role -> roleEntry.getValue() == role.getIdLong())) {
									removedList.add(Group.valueOf(roleEntry.getKey().toUpperCase()).name());
									guild.removeRoleFromMember(discordMember, guild.getRoleById(roleEntry.getValue()))
											.complete();
								}
							} catch (Exception ex) {
								ex.printStackTrace();
//							CommonGeneral.getInstance()
//									.debug("The server hasn't found the Group " + roleEntry.getKey().toUpperCase());
							}
						}

						for (Entry<RankType, Long> entryRank : member.getRanks().entrySet()) {
							if (configuration.getRoleMap().containsKey(entryRank.getKey().name().toLowerCase())) {

								System.out.println(
										configuration.getRoleMap().get(entryRank.getKey().name().toLowerCase()));

								Role role = guild.getRoleById(
										configuration.getRoleMap().get(entryRank.getKey().name().toLowerCase()));

								if (role == null) {
									CommonGeneral.getInstance().debug("The server hasn't found the role "
											+ entryRank.getKey().name().toLowerCase() + " in discord");
								} else {
									guild.addRoleToMember(discordMember, role).complete();

									addedList.add(Group.valueOf(entryRank.getKey().name()).name());
								}

							} else {
								CommonGeneral.getInstance().debug("The server hasn't found the role "
										+ entryRank.getKey().name().toLowerCase() + " in map");
							}
						}

						sender.sendMessage("Sua conta foi sincronizada!");
					}
				} else {
					sender.sendMessage(
							"A sua conta não pode ser sincronizada com o discord, peça para um administrador setar seu cargo manualmente!");
				}
			} else {
				sender.sendMessage("A sincronização do discord com o servidor está desativada!");
			}
			break;
		}
		case "sync": {
			if (args.length == 1) {
				sender.sendMessage("Use /discord sync <playerName> para sincronizar o discord com o servidor!");
				return;
			}

			for (Map<Long, Invite> value : MAP.values()) {
				if (value.containsKey(sender.getUser().getIdLong())) {

					if (value.get(sender.getUser().getIdLong()).getExpireTime() > System.currentTimeMillis()) {
						sender.sendMessage("Você ainda está em um processo de sincronização!");
						return;
					}

					value.remove(sender.getUser().getIdLong());
					break;
				}
			}

			Member member = CommonGeneral.getInstance().getMemberManager().getMember(args[1]);

			if (member == null) {
				sender.sendMessage("Você precisa estar no servidor para executar essa ação!");
				return;
			}

			if (member.hasDiscord()) {
				sender.sendMessage("A conta " + member.getPlayerName() + "(" + member.getUniqueId().toString()
						+ ") já possui uma conta do discord cadastrada!");
				return;
			}

			sender.sendMessage("Uma mensagem foi enviada **IN-GAME**!");

			TextComponent accept = new TextComponent("§a§lAQUI ");

			accept.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord accept " + sender.getUser().getIdLong()));
			accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText("§aClique aqui para aceitar")));

			TextComponent deny = new TextComponent("§c§lAQUI ");

			deny.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord deny " + sender.getUser().getIdLong()));
			deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText("§cClique aqui para rejeitar")));

			member.sendMessage(new BaseComponent[] {
					new TextComponent(
							"§a§l> §fUma solicitação de sincronização com o discord foi enviado a você pelo §a"
									+ sender.getUser().getName() + "#" + sender.getUser().getDiscriminator() + " ("
									+ sender.getUser().getIdLong() + "), então clique "),
					accept, new TextComponent("§fpara aceitar e "), deny, new TextComponent("§fpara rejeitar!") });
			MAP.computeIfAbsent(member.getUniqueId(), v -> new HashMap<>()).put(sender.getUser().getIdLong(),
					new Invite(System.currentTimeMillis() + 120000l, sender.getAsMember().getTimeBoosted() != null,
							sender.getUser().getName() + "#" + sender.getUser().getDiscriminator(),
							sender.getUser().getIdLong(), cmdArgs.getTextChannel().getIdLong()));
			break;
		}
		}
	}

	@Command(name = "glist", aliases = { "globallist" }, runAsync = true)
	public void glistCommand(DiscordCommandArgs cmdArgs) {
		EmbedBuilder builder = new EmbedBuilder().setTitle("Temos " + ProxyServer.getInstance().getPlayers().size())
				.setColor(Color.YELLOW).appendDescription(Joiner.on(", ").join(ProxyServer.getInstance().getPlayers()
						.stream().map(ProxiedPlayer::getName).collect(Collectors.toList())));

		cmdArgs.getTextChannel().sendMessage(builder.build()).complete();
	}
	
	@Command(name = "broadcast", aliases = { "bc" }, runAsync = true)
	public void broadcastCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;
		
		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("Use /broadcast <mensagem> para enviar broadcast no servidor!");
			return;
		}
		
		String msg = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");
		msg = sb.toString();
		
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§c§lAVISO §f" + msg.replace("&", "§")));
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(" "));
		sender.sendMessage("");
	}

	@Command(name = "stafflist", runAsync = true)
	public void stafflistCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;
		
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Jogadores da equipe onlines:");
		builder.setColor(Color.YELLOW);

		for (Member member : CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(member -> member.hasGroupPermission(Group.BUILDER)).collect(Collectors.toList())) {
			builder.addField(member.getPlayerName(), member.getServerGroup().name(), false);
		}

		cmdArgs.getTextChannel().sendMessage(builder.build()).complete();
	}

	@AllArgsConstructor
	@Getter
	public class Invite {

		private long expireTime;
		private boolean booster;
		private String discordName;

		private long userId;
		private long chatId;

	}

}
