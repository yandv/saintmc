package tk.yallandev.saintmc.bungee.command.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.bungee.BungeeClan;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanInfo;
import tk.yallandev.saintmc.common.clan.ClanInvite;
import tk.yallandev.saintmc.common.clan.ClanModel;
import tk.yallandev.saintmc.common.clan.enums.ClanHierarchy;
import tk.yallandev.saintmc.common.clan.event.ClanVoid;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class ClanCommand implements CommandClass {

	public static final Pattern ABBREVIATION_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,6}");
	public static final Pattern CLANNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,12}");

	private Map<UUID, Map<UUID, ClanInvite>> inviteMap;

	public ClanCommand() {
		inviteMap = new HashMap<>();
	}

	@Command(name = "clan", runAsync = true)
	public void clanCommand(CommandArgs cmdArgs) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			handleUsage(player, cmdArgs.getLabel());
			return;
		}

		switch (args[0].toLowerCase()) {
		case "criar": {
			if (dontHaveClan(player)) {
				if (args.length >= 3) {
					String clanName = args[1];

					if (CommonGeneral.getInstance().getClanData().loadClan(clanName) != null) {
						player.sendMessage("§cUm clan com o nome " + clanName + " já existe!");
						return;
					}

					if (!CLANNAME_PATTERN.matcher(clanName).matches()) {
						player.sendMessage("§cO nome do seu clan é inválido!");
						player.sendMessage("§cSomente letras, numeros e _, com até 12 caracteres!");
						return;
					}

					String abbreviation = args[2];

					if (CommonGeneral.getInstance().getClanData().loadClanByAbbreviation(abbreviation) != null) {
						player.sendMessage("§cUm clan com a sigla " + abbreviation + " já existe!");
						return;
					}

					if (!ABBREVIATION_PATTERN.matcher(abbreviation).matches()) {
						player.sendMessage("§cA sigla do seu clan é inválida!");
						player.sendMessage("§cSomente letras, numeros e _, com até 6 caracteres!");
						return;
					}

					UUID uuid = UUID.randomUUID();

					while (CommonGeneral.getInstance().getClanData().loadClan(uuid) != null)
						uuid = UUID.randomUUID();

					Clan clan = new BungeeClan(uuid, clanName, abbreviation, player);

					CommonGeneral.getInstance().getClanData().createClan(clan);
					CommonGeneral.getInstance().getClanManager().loadClan(clan.getUniqueId(), clan);

					player.setClanUniqueId(uuid);
					player.sendMessage("§aO clan " + clanName + " (" + abbreviation + ") foi criado!");
				} else {
					handleUsage(player, cmdArgs.getLabel());
				}
			}
			break;
		}
		case "setgroup": {
			if (haveClan(player)) {
				if (args.length >= 3) {
					if (!player.getClan().hasGroup(player.getUniqueId(), ClanHierarchy.ADMIN)) {
						player.sendMessage("§cVocê não tem permissão para fazer isso!");
						return;
					}

					UUID uuid = CommonGeneral.getInstance().getUuid(args[1]);

					if (uuid == null) {
						player.sendMessage(" §c* §fO jogador §a" + args[1] + "§f não existe!");
						return;
					}

					if (!player.getClan().isMember(uuid)) {
						player.sendMessage("§cO jogador não é do seu clan!");
						return;
					}

					ClanHierarchy clanHierarchy = null;

					try {
						clanHierarchy = ClanHierarchy.valueOf(args[2].toUpperCase());
					} catch (Exception ex) {
						player.sendMessage("§cO grupo " + args[2] + " não existe! Tente: "
								+ Joiner.on(", ").join(Arrays.asList(ClanHierarchy.values()).stream()
										.map(cl -> cl.name().toLowerCase()).collect(Collectors.toList())));
						return;
					}

					if (clanHierarchy == ClanHierarchy.ADMIN || clanHierarchy == ClanHierarchy.OWNER)
						if (!player.getClan().isGroup(player.getUniqueId(), ClanHierarchy.OWNER)) {
							player.sendMessage("§cVocê não pode manejar esse grupo!");
							return;
						}

					if (player.getClan().isGroup(uuid, ClanHierarchy.OWNER)) {
						player.sendMessage("§cVocê não pode manejar o grupo desse jogador!");
						return;
					}

					if (player.getClan().isGroup(uuid, clanHierarchy)) {
						player.sendMessage("§cO jogador já está nesse grupo!");
						return;
					}

					Member member = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

					if (player.getClan().setGroup(uuid, clanHierarchy)) {
						if (member != null)
							player.getClan().sendMessage("§fO grupo do " + member.getPlayerName()
									+ " foi alterado para " + clanHierarchy.getTag() + "§f!");
						else
							player.getClan().sendMessage(
									"§fO grupo do " + player.getClan().getMemberMap().get(uuid).getPlayerName()
											+ " foi alterado para " + clanHierarchy.getTag() + "§f!");
					} else {
						if (player.getClan().isMember(uuid))
							player.sendMessage("§cO jogador não é do seu clan!");
						else
							player.sendMessage("§cO jogador não tem clan!");
					}

				} else
					handleUsage(player, cmdArgs.getLabel());
			}

			break;
		}
		case "apagar":
		case "disband":
		case "deletar": {
			if (haveClan(player)) {
				Clan clan = player.getClan();

				if (clan.isGroup(player.getUniqueId(), ClanHierarchy.OWNER))
					if (clan.disband())
						player.sendMessage("§cO seu clan foi deletado com sucesso!");
					else {
						player.sendMessage("§aDigite novamente para deletar o seu clan!");
						player.sendMessage("§4§lNão é possível desfazer essa ação!");
					}
				else {
					player.sendMessage("§cSomente o " + Tag.DONO.getPrefix() + "§f do clan pode fazer isso!");
				}
			}

			break;
		}
		case "leave":
		case "sair": {
			if (haveClan(player)) {
				Clan clan = player.getClan();

				if (clan.isGroup(player.getUniqueId(), ClanHierarchy.OWNER))
					player.sendMessage("§cVocê não pode sair do seu clan!");
				else {
					player.sendMessage("§aVocê saiu do clan!");
					player.setClanUniqueId(null);
					clan.removeMember(player);
				}
			}
			break;
		}
		case "invite":
		case "convidar": {
			if (haveClan(player)) {
				if (args.length >= 2) {
					Member member = CommonGeneral.getInstance().getMemberManager().getMember(args[1]);

					if (member == null) {
						player.sendMessage("§cO jogador não existe!");
						return;
					}

					if (member.hasClan()) {
						player.sendMessage("§cO jogador já está em um clan!");
						return;
					}

					if (player.getClan().getMemberMap().size() + 1 > 12) {
						player.sendMessage("§cO clan está cheio!");
						return;
					}

					Map<UUID, ClanInvite> map = inviteMap.computeIfAbsent(player.getClanUniqueId(),
							v -> new HashMap<>());

					if (map.containsKey(member.getUniqueId())) {
						if (!map.get(member.getUniqueId()).hasExpired()) {
							player.sendMessage("§cVocê já convidou este jogador!");
							return;
						}
					}

					if (map.values().stream().filter(clanInvite -> !clanInvite.hasExpired()).count() >= 12) {
						player.sendMessage(
								"§cVocê enviou muitos convites simultâneos, aguarde para enviar outro convite!");
						return;
					}

					map.put(member.getUniqueId(),
							new ClanInvite(player.getClanUniqueId(), player.getClan().getClanName(),
									player.getUniqueId(), player.getPlayerName(), System.currentTimeMillis() + 60000l));
					player.sendMessage("§aO jogador " + member.getPlayerName() + " foi convidado para o clan!");

					TextComponent textComponent = new MessageBuilder("§a§l> §fVocê foi convidado para o clan "
							+ player.getClan().getClanName() + " (" + player.getClan().getClanAbbreviation() + ") pelo "
							+ player.getPlayerName() + " clique ").create();

					textComponent.addExtra(new MessageBuilder("§a§lAQUI ")
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("§aClique para aceitar o convite!").create()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
									"/clan aceitar " + player.getClan().getClanName()))
							.create());

					textComponent.addExtra(new MessageBuilder("§fpara aceitar ").create());

					textComponent.addExtra(new MessageBuilder("§c§lREJEITAR ")
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder("§cClique para rejeitar o convite!").create()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
									"/clan rejeitar " + player.getClan().getClanName()))
							.create());

					textComponent.addExtra(new MessageBuilder("§fpara rejeitar o convite!").create());

					member.sendMessage(textComponent);
				} else
					handleUsage(player, cmdArgs.getLabel());
			}
			break;
		}
		case "expulsar":
		case "kick": {
			if (haveClan(player)) {
				if (args.length >= 2) {
					if (!player.getClan().hasGroup(player.getUniqueId(), ClanHierarchy.ADMIN)) {
						player.sendMessage("§cVocê não tem permissão para fazer isso!");
						return;
					}

					UUID uuid = CommonGeneral.getInstance().getUuid(args[1]);

					if (uuid == null) {
						player.sendMessage(" §c* §fO jogador §a" + args[1] + "§f não existe!");
						return;
					}

					if (!player.getClan().isMember(uuid)) {
						player.sendMessage("§cO jogador não é do seu clan!");
						return;
					}

					Member member = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

					if (member == null) {
						try {
							MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

							if (loaded == null) {
								player.sendMessage("§cO jogador " + args[1] + " nunca entrou no servidor!");
								return;
							}

							member = new MemberVoid(loaded);
						} catch (Exception e) {
							e.printStackTrace();
							player.sendMessage("§cNão foi possível pegar as informações do jogador " + args[1] + "!");
							return;
						}
					}

					if (player.getClan().kickMember(member, player))
						member.setClanUniqueId(null);
					else {
						if (member.hasClan())
							player.sendMessage("§cO jogador não é do seu clan!");
						else
							player.sendMessage("§cO jogador não tem clan!");
					}
				} else
					handleUsage(player, cmdArgs.getLabel());
			}

			break;
		}
		case "accept":
		case "aceitar": {
			if (dontHaveClan(player)) {
				if (args.length >= 2) {
					Clan clan = CommonGeneral.getInstance().getClanManager().getClan(args[1], true);

					if (clan == null) {
						clan = CommonGeneral.getInstance().getClanManager().getClanByAbbreviation(args[1], true);

						if (clan == null) {
							player.sendMessage("§cO clan " + args[1] + " não existe!");
							return;
						}
					}

					if (clan.getMemberMap().size() + 1 > 12) {
						player.sendMessage("§cO clan está cheio!");
						return;
					}

					if (!inviteMap.containsKey(clan.getUniqueId())) {
						player.sendMessage("§cVocê não tem convite desse clan para aceitar!");
						return;
					}

					ClanInvite clanInvite = inviteMap.get(clan.getUniqueId()).get(player.getUniqueId());

					if (clanInvite == null) {
						player.sendMessage("§cVocê não tem convite desse clan para aceitar!");
						return;
					}

					if (clan.addMember(player)) {
						player.setClanUniqueId(clan.getUniqueId());
						player.sendMessage("§aVocê aceitou o convite do clan!");
						inviteMap.get(clan.getUniqueId()).remove(player.getUniqueId());
					}
				} else
					handleUsage(player, cmdArgs.getLabel());
			}
			break;
		}
		case "deny":
		case "rejeitar": {
			if (dontHaveClan(player)) {
				if (args.length >= 2) {
					Clan clan = CommonGeneral.getInstance().getClanManager().getClan(args[0], true);

					if (clan == null) {
						clan = CommonGeneral.getInstance().getClanManager().getClanByAbbreviation(args[0], true);

						if (clan == null) {
							player.sendMessage("§cO clan " + args[0] + " não existe!");
							return;
						}
					}

					if (!inviteMap.containsKey(clan.getUniqueId())) {
						player.sendMessage("§cVocê não tem convite desse clan para recusar!");
						return;
					}

					ClanInvite clanInvite = inviteMap.get(clan.getUniqueId()).get(player.getUniqueId());

					if (clanInvite == null) {
						player.sendMessage("§cVocê não tem convite desse clan para recusar!");
						return;
					}

					clan.sendMessage("§cO " + player.getPlayerName() + " recusou o convite para entrar no clan!");
					player.sendMessage("§aVocê recusou o convite do clan!");
					inviteMap.get(clan.getUniqueId()).remove(player.getUniqueId());
				} else
					handleUsage(player, cmdArgs.getLabel());
			}
			break;
		}
		case "info":
		case "member":
		case "members":
		case "membro":
		case "membros": {
			Clan clan = player.getClan();

			if (args.length >= 2) {
				clan = CommonGeneral.getInstance().getClanManager().getClan(args[1], true);

				if (clan == null) {
					clan = CommonGeneral.getInstance().getClanManager().getClanByAbbreviation(args[1], true);

					if (clan == null) {
						ClanModel clanModel = CommonGeneral.getInstance().getClanData().loadClan(args[1]);

						if (clanModel == null) {
							clanModel = CommonGeneral.getInstance().getClanData().loadClanByAbbreviation(args[1]);

							if (clanModel == null) {
								player.sendMessage("§cO clan " + args[1] + " não existe!");
								return;
							}

							clan = new ClanVoid(clanModel);
						}
					}
				}
			}

			if (clan == null) {
				handleUsage(player, cmdArgs.getLabel());
				return;
			}

			if (args[0].equalsIgnoreCase("info")) {
				player.sendMessage("§0§m---------------------");
				player.sendMessage("§a§l> §e" + clan.getClanName() + " - Info");
				player.sendMessage(" ");
				player.sendMessage("§7" + clan.getClanAbbreviation());
				player.sendMessage(" ");
				player.sendMessage("§a§l> §4§lDONO§f: " + (clan.getMemberMap().values().stream()
						.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.OWNER)
						.map(clanInfo -> (CommonGeneral.getInstance().getMemberManager()
								.containsKey(clanInfo.getPlayerId()) ? "§a" : "§c") + clanInfo.getPlayerName())
						.findFirst().orElse("§cNinguém")));
				player.sendMessage("§a§l> " + ClanHierarchy.ADMIN.getTag()
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.ADMIN).count() == 1
										? ""
										: "S")
						+ "§f: "
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.ADMIN).findFirst()
								.isPresent()
										? Joiner.on(", ").join(clan.getMemberMap().values().stream()
												.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.ADMIN)
												.map(clanInfo -> (CommonGeneral.getInstance().getMemberManager()
														.containsKey(clanInfo.getPlayerId()) ? "§a" : "§c")
														+ clanInfo.getPlayerName())
												.collect(Collectors.toList()))
										: ""));
				player.sendMessage("§a§l> " + ClanHierarchy.RECRUTER.getTag()
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.RECRUTER).count() == 1
										? ""
										: "S")
						+ "§f: "
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.RECRUTER).findFirst()
								.isPresent()
										? Joiner.on(", ").join(clan.getMemberMap().values().stream().filter(
												clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.RECRUTER)
												.map(clanInfo -> (CommonGeneral.getInstance().getMemberManager()
														.containsKey(clanInfo.getPlayerId()) ? "§a" : "§c")
														+ clanInfo.getPlayerName())
												.collect(Collectors.toList()))
										: ""));
				player.sendMessage("§a§l> " + ClanHierarchy.MEMBER.getTag()
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.MEMBER).count() == 1
										? ""
										: "S")
						+ "§f: "
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.MEMBER).findFirst()
								.isPresent()
										? Joiner.on(", ").join(clan.getMemberMap().values().stream()
												.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.MEMBER)
												.map(clanInfo -> (CommonGeneral.getInstance().getMemberManager()
														.containsKey(clanInfo.getPlayerId()) ? "§a" : "§c")
														+ clanInfo.getPlayerName())
												.collect(Collectors.toList()))
										: ""));
				player.sendMessage(" ");
				player.sendMessage("§6Ranking: ");
				player.sendMessage(" ");
				player.sendMessage("§a§l> §fColocação: §e-/-");
				player.sendMessage("§a§l> §fXP: §e" + clan.getXp());
				player.sendMessage("§a§l> §fMembros: §e" + clan.getMemberMap().size() + "/12");
				player.sendMessage("§0§m---------------------");
			} else {
				player.sendMessage("§a" + clan.getClanName() + " (" + clan.getClanAbbreviation() + ")");
				player.sendMessage(" ");

				player.sendMessage(new MessageBuilder("§4§lDono"
						+ (clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.OWNER).count() == 1
										? ""
										: "s")
						+ ": §f"
						+ Joiner.on(", ").join(clan.getMemberMap().values().stream()
								.filter(clanInfo -> clanInfo.getClanHierarchy() == ClanHierarchy.OWNER)
								.map(clanInfo -> (CommonGeneral.getInstance().getMemberManager()
										.containsKey(clanInfo.getPlayerId()) ? "§a" : "§c") + clanInfo.getPlayerName())
								.collect(Collectors.toList()))).create());

				TextComponent textComponent = new MessageBuilder(
						"§c§lMembro" + (clan.getMemberMap().size() == 1 ? "" : "s") + ": §f").create();

				for (Entry<UUID, ClanInfo> entry : clan.getMemberMap().entrySet()) {
					if (entry.getValue().getClanHierarchy() == ClanHierarchy.OWNER)
						continue;

					Member member = CommonGeneral.getInstance().getMemberManager().getMember(entry.getKey());

					MessageBuilder messageBuilder = new MessageBuilder(
							member == null ? "§c" + entry.getValue().getPlayerName() : "§a" + member.getPlayerName());

					messageBuilder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							TextComponent.fromLegacyText("§aXP que o jogador obteu para a Clan\n\n"
									+ (entry.getValue().getXpEarned() > 0 ? "§a" + entry.getValue().getXpEarned()
											: entry.getValue().getXpEarned() < 0 ? "§c" + entry.getValue().getXpEarned()
													: "§e" + entry.getValue().getXpEarned())
									+ "\n\n§7Grupo: §f" + entry.getValue().getClanHierarchy().getTag())));

					textComponent.addExtra(messageBuilder.create());
					textComponent.addExtra(", ");
				}

				player.sendMessage(textComponent);
			}
			break;
		}
		default: {
			handleUsage(player, cmdArgs.getLabel());
			break;
		}
		}
	}

	@Command(name = "clan.chat", aliases = { "clanchat", "cchat" })
	public void clanchatCommand(CommandArgs cmdArgs) {
		if (!(cmdArgs.isPlayer()))
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (haveClan(player)) {
			StringBuilder stringBuilder = new StringBuilder();

			for (int x = 0; x < cmdArgs.getArgs().length; x++)
				stringBuilder.append(cmdArgs.getArgs()[x] + " ");

			if (stringBuilder.toString().isEmpty()) {
				player.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " chat <mensagem>§f para falar no chat do clan!");
				return;
			}

			player.getClan().chat(player, stringBuilder.toString().trim());
		}
	}

	@Command(name = "clan.top")
	public void clantopCommand(CommandArgs cmdArgs) {
		cmdArgs.getSender().sendMessage("§a§l> §eClan - Top");
		cmdArgs.getSender().sendMessage("");

		List<ClanModel> ranking = new ArrayList<>(CommonGeneral.getInstance().getClanData().ranking("xp"));

		for (int x = 0; x < ranking.size(); x++) {
			ClanModel clanModel = ranking.get(x);
			cmdArgs.getSender()
					.sendMessage((x == 0 ? "§a" : x == 1 ? "§e" : x == 2 ? "§c" : "§7") + (x + 1) + "° §8- §f "
							+ clanModel.getClanName() + " (" + clanModel.getClanAbbreviation() + ") §8- §a"
							+ clanModel.getXp() + " xp");
		}
	}

	public boolean haveClan(Member member) {
		if (member.getClanUniqueId() == null) {
			member.sendMessage("§cVocê não tem um clan!");
			return false;
		}

		return true;
	}

	public boolean dontHaveClan(Member member) {
		if (member.getClanUniqueId() != null) {
			member.sendMessage("§cVocê já tem um clan!");
			return false;
		}

		return true;
	}

	public void handleUsage(CommandSender sender, String label) {
		sender.sendMessage(" §4* §fUse §a/" + label + " <create:criar> <nome> <sigla>§f para criar um clan!");
		sender.sendMessage(" §4* §fUse §a/" + label + " apagar§f para apagar o seu clan!");
		sender.sendMessage(" §e* §fUse §a/"
				+ label + " setgroup <player> <" + (Joiner.on(':').join(Arrays.asList(ClanHierarchy.values()).stream()
						.map(ch -> ch.name().toLowerCase()).collect(Collectors.toList())))
				+ ">§f para mudar o grupo do player no clan!");
		sender.sendMessage(" §e* §fUse §a/" + label + " kick <player>§f para kickar um player do seu clan!");
		sender.sendMessage(" §e* §fUse §a/" + label + " invite <player>§f para convidar um jogador!");
		sender.sendMessage(" §e* §fUse §a/" + label + " cancel <player>§f para cancelar o invite de um jogador!");
		sender.sendMessage(" §e* §fUse §a/" + label + " join <nome>§f para entrar em um clan!");
		sender.sendMessage(" §e* §fUse §a/" + label + " chat <mensagem>§f para falar no chat do clan!");
		sender.sendMessage(" §e* §fUse §a/" + label + " info <nome:sigla>§f para ver as informações de um clan!");
		sender.sendMessage(" §e* §fUse §a/" + label + " leave§f para sair do clan!");
		sender.sendMessage(" §6* §fUse §a/" + label + " top§f para ver o top clans!");
		sender.sendMessage(" §6* §fUse §a/" + label + " members§f para ver os membros do clan!");
	}

	/**
	 * /clan setgroup <nick> <cargo> /clan sigla <sigla> /clan apagar /clan invite
	 * <nick> /clan cancel <nick> /clan kick (nick) /clan blacklist (nick) /clan
	 * create (nome) (sigla) /clan join (nome da clan) /clan chat /clan info /clan
	 * quit/leave /clan top /clan members /clan groups
	 */

}
