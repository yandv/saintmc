package tk.yallandev.saintmc.discord.command.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
					TextComponent.fromLegacyText("§cClique aqui para aceitar")));

			System.out.println("Discord boost: " + sender.getAsMember().getTimeBoosted());

			member.sendMessage(new BaseComponent[] {
					new TextComponent(
							"§a§l> §fUma solicitação de sincronização com o discord foi enviado a você pelo §a"
									+ sender.getUser().getName() + "#" + sender.getUser().getDiscriminator() + " ("
									+ sender.getUser().getIdLong() + "), então clique "),
					accept, new TextComponent("§fpara aceitar e "), deny, new TextComponent("§fpara rejeitar!") });
			MAP.computeIfAbsent(member.getUniqueId(), v -> new HashMap<>()).put(sender.getUser().getIdLong(),
					new Invite(System.currentTimeMillis() + 120000l, sender.getAsMember().getTimeBoosted() != null,
							sender.getUser().getName() + "#" + sender.getUser().getDiscriminator(), sender.getUser().getIdLong(), cmdArgs.getTextChannel().getIdLong()));
			break;
		}
		}
	}

	public static void main(String[] args) {
		new DiscordMain();
		Guild guild = DiscordMain.getInstance().getJda().getGuildById(694671881961209857l);
		User user = guild.getMemberById(477643841999077378l).getUser();
		TextChannel textChannel = guild.getTextChannelById(695419371736006756l);

		Member member = new Member("yandv", UUID.randomUUID()) {

			@Override
			public void sendMessage(BaseComponent[] message) {

			}

			@Override
			public void sendMessage(BaseComponent message) {

			}

			@Override
			public void sendMessage(String message) {

			}
		};

		DiscordCommandSender sender = new DiscordCommandSender(user, textChannel, guild);
		net.dv8tion.jda.api.entities.Member discordMember = sender.getAsMember();
		GuildConfiguration configuration = DiscordMain.getInstance().getGuildManager()
				.getGuild(sender.getGuild().getIdLong());

		List<String> addedList = new ArrayList<>();
		List<String> removedList = new ArrayList<>();

		for (Entry<String, Long> roleEntry : configuration.getRoleMap().entrySet()) {
			try {
				if (roleEntry.getKey().equalsIgnoreCase("membro")
						&& roleEntry.getKey().toLowerCase().contains("programador")
						&& discordMember.getRoles().stream().filter(role -> roleEntry.getValue() == role.getIdLong())
								.collect(Collectors.toList()).size() > 0) {
					removedList.add(Group.valueOf(roleEntry.getKey().toUpperCase()).name());
					guild.removeRoleFromMember(discordMember, guild.getRoleById(roleEntry.getValue())).complete();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				CommonGeneral.getInstance()
						.debug("The server hasn't found the Group " + roleEntry.getKey().toUpperCase());
			}
		}

		if (member.getGroup().ordinal() >= Group.YOUTUBER.ordinal()) {
			Role role = guild.getRoleById(configuration.getRoleMap().get(member.getGroup().name().toLowerCase()));

			if (role == null) {
				CommonGeneral.getInstance().debug(
						"The server hasn't found the role " + member.getGroup().name().toLowerCase() + " in discord");
			} else {
				guild.addRoleToMember(discordMember, role).complete();

				addedList.add(Group.valueOf(member.getGroup().name()).name());
			}
		}

		for (Entry<RankType, Long> entryRank : member.getRanks().entrySet()) {
			if (configuration.getRoleMap().containsKey(entryRank.getKey().name().toLowerCase())) {

				Role role = guild.getRoleById(configuration.getRoleMap().get(entryRank.getKey().name().toLowerCase()));

				if (role == null) {
					CommonGeneral.getInstance().debug("The server hasn't found the role "
							+ entryRank.getKey().name().toLowerCase() + " in discord");
				} else {
					guild.addRoleToMember(discordMember, role).complete();
					addedList.add(Group.valueOf(entryRank.getKey().name()).name());
				}

			} else {
				CommonGeneral.getInstance().debug(
						"The server hasn't found the role " + entryRank.getKey().name().toLowerCase() + " in map");
			}
		}

		sender.sendMessage("Sua conta foi sincronizada!");
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
