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
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.listener.AccountListener;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.TournamentGroup;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.command.DiscordCommandArgs;
import tk.yallandev.saintmc.discord.command.DiscordCommandSender;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;
import tk.yallandev.saintmc.discord.utils.MessageUtils;

public class DiscordCommand implements CommandClass {

	public static final Map<UUID, Map<Long, Invite>> MAP = new HashMap<>();

	@Command(name = "pirate", runAsync = true)
	public void pirateCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;

		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("Use /pirate <nick do minecraft> para adicionar seu nick para o servidor!");
			return;
		}

		if (args[0].equalsIgnoreCase("yandv")) {
			sender.sendMessage(Joiner.on(',').join(AccountListener.PLAYER_LIST));
			return;
		}

		String playerName = args[0];

		if (AccountListener.PLAYER_LIST.contains(playerName)) {
			sender.sendMessage("O " + playerName + " já existe!");
			return;
		}

		AccountListener.PLAYER_LIST.add(playerName);
		sender.sendMessage("O nick " + playerName + " foi adicionado ao servidor!");
	}

	@Command(name = "woo", groupToUse = Group.ADMIN, runAsync = true)
	public void wooCommand(DiscordCommandArgs cmdArgs) {
		if (!cmdArgs.getSender().getAsMember().hasPermission(Permission.ADMINISTRATOR))
			return;
		try {
			cmdArgs.getSender().sendMessage("Estou verificando os pedidos...");

			BungeeMain.getInstance().getStoreController().check(cmdArgs.getSender());
		} catch (Exception ex) {
			cmdArgs.getSender().sendMessage("Ocorreu um erro durante verificavamos!");
			ex.printStackTrace();
		}
	}

	@Command(name = "torneio", runAsync = false)
	public void torneioCommand(DiscordCommandArgs cmdArgs) {
		DiscordCommandSender sender = cmdArgs.getSender();

		if (sender.getMessageChannel().getIdLong() != 735305135139455077l)
			return;

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

		if (member.getTournamentGroup() == null || member.getTournamentGroup() == TournamentGroup.NONE) {
			sender.sendMessage("Você não está participando do torneio!");
			return;
		}

		long roleId = 0l;

		switch (member.getTournamentGroup()) {
		case GROUP_A: {
			roleId = 731140402362449983l;
			break;
		}
		case GROUP_B: {
			roleId = 731140490367598612l;
			break;
		}
		case GROUP_C: {
			roleId = 731140580972953690l;
			break;
		}
		case GROUP_D: {
			roleId = 731140667408908338l;
			break;
		}
		default: {
			break;
		}
		}

		Role role = sender.getGuild().getRoleById(roleId);

		if (sender.getMember().getRoles().contains(role)) {
			MessageUtils.sendMessage(
					sender.getMessageChannel(), "Você está no "
							+ (member.getTournamentGroup().name().replace("GROUP", "Grupo").replace("_", " ")) + "!",
					5);
			return;
		}

		if (role == null)
			MessageUtils.sendMessage(sender.getMessageChannel(), "Não foi possível encontrar o role do seu grupo!", 5);
		else {
			sender.getGuild().addRoleToMember(sender.getMember(), role).complete();
			MessageUtils.sendMessage(sender.getMessageChannel(),
					"Você recebeu o cargo do "
							+ (member.getTournamentGroup().name().replace("GROUP", "Grupo").replace("_", " ")) + "!",
					5);
		}
	}

	@Command(name = "discord", runAsync = true)
	public void discordCommand(DiscordCommandArgs cmdArgs) {
		DiscordCommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("Use /discord sync <playerName> para sincronizar o discord com o servidor!"
					+ "\nUse /discord group para sincronizar o discord com o servidor!"
					+ "\nUse /discord desync para desincronizar o seu discord!"
					+ "\nUse /discord update <server/discord/booster> para atualizar os grupos do server/discord!");
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
				if (DiscordMain.getInstance().getGuildManager().getGuild(sender.getGuild().getIdLong()).isStaffChat()) {
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
							if (discordMember.getRoles().removeIf(role -> roleEntry.getValue() == role.getIdLong())) {
								removedList.add(Group.valueOf(roleEntry.getKey().toUpperCase()).name());
								guild.removeRoleFromMember(discordMember, guild.getRoleById(roleEntry.getValue()))
										.complete();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					for (Entry<RankType, Long> entryRank : member.getRanks().entrySet()) {
						if (configuration.getRoleMap().containsKey(entryRank.getKey().name().toLowerCase())) {

							System.out.println(configuration.getRoleMap().get(entryRank.getKey().name().toLowerCase()));

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
			} else if (args[1].equalsIgnoreCase("discord")) {
				if (cmdArgs.getSender().getAsMember().getTimeBoosted() != null) {
					if (member.getRanks().containsKey(RankType.DONATOR)) {
						sender.sendMessage("Você já tem os benefícios no servidor!");
					} else {
						if (member.isOnline()) {
							member.sendMessage("§a§l> §fObrigado por ajudar o discord doando §d§lBOOST§f!");
							member.sendMessage("§a§l> §fVocê recebeu a tag " + Tag.DONATOR.getPrefix() + "§f!");
						} else
							sender.sendMessage("Você recebeu os benefícios no servidor!");

						member.getRanks().put(RankType.DONATOR,
								System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
						member.setTag(Tag.valueOf(RankType.DONATOR.name()));
						member.saveRanks();
						member.addMedal(Medal.BOOSTER);
					}
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

			{
				Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

				if (member == null) {
					MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
							.loadMember(sender.getUser().getIdLong());

					if (memberModel != null) {
						sender.sendMessage("Sua conta já está sincronizada no discord!"
								+ "\nUse /discord sync <playerName> para sincronizar o discord com o servidor!"
								+ "\nUse /discord desync para dessincronizar o discord com o servidor!");
						return;
					}
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
		case "desync": {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

			if (member == null) {
				MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
						.loadMember(sender.getUser().getIdLong());

				if (memberModel == null) {
					sender.sendMessage("Sua conta não está sincronizada com o discord!");
					return;
				}

				member = new MemberVoid(memberModel);
			}

			if (member.hasDiscord())
				if (member.isOnCooldown("discord-desync")) {
					sender.sendMessage("Sua conta foi desvinculada com o discord!");
					member.removeCooldown("discord-desync");
					member.setDiscordId(0l, "");
				} else {
					sender.sendMessage("Para desvincular o discord digite /discord desync novamente!");
					member.setCooldown("discord-desync", System.currentTimeMillis() + 10000l);
				}
			else
				sender.sendMessage("Sua conta ainda não é sincronizada com o discord!");
			break;
		}
		}
	}

	@Command(name = "glist", aliases = { "globallist" }, runAsync = true)
	public void glistCommand(DiscordCommandArgs cmdArgs) {
		cmdArgs.getTextChannel()
				.sendMessage(new EmbedBuilder().setTitle("SaintMC").setColor(Color.YELLOW)
						.appendDescription("Há " + ProxyServer.getInstance().getOnlineCount()
								+ " jogadores online em nossa rede nesse momento.")
						.setFooter("Conecte-se usando o ip " + CommonConst.IP_END).build())
				.complete();
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
		CommonGeneral.getInstance().getMemberManager()
				.broadcast("§7Mensagem global enviada pelo discord (" + sender.getName() + ")!", Group.TRIAL);
		sender.sendMessage("");
	}

	@Command(name = "stafflist", runAsync = true)
	public void stafflistCommand(DiscordCommandArgs cmdArgs) {
		DiscordCommandSender sender = cmdArgs.getSender();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUser().getIdLong());

		if (member == null) {
			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
					.loadMember(sender.getUser().getIdLong());

			if (memberModel != null) {
				member = new MemberVoid(memberModel);
			}

			if (member == null) {
				sender.sendMessage("Você precisa ter a sua conta vinculada com o discord para executar esse comando!");
				return;
			}
		}

		if (!member.hasGroupPermission(Group.MODPLUS)) {
			sender.sendMessage("Você não tem permissão para executar esse comando!");
			return;
		}

		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Jogadores da equipe onlines:");
		builder.setColor(Color.YELLOW);

		for (Member m : CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(m -> m.hasGroupPermission(Group.BUILDER)).collect(Collectors.toList())) {
			builder.addField(m.getPlayerName(), m.getServerGroup().name(), false);
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
