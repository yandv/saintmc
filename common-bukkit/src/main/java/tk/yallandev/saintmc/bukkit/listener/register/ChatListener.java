package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.BukkitConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.StringURLUtils;

public class ChatListener extends Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (!member.getLoginConfiguration().isLogged()) {
			event.setCancelled(true);

			if (member.getLoginConfiguration().isRegistred())
				member.sendMessage("§4§l> §fLogue-se para ter o acesso ao chat liberado!");
			else
				member.sendMessage("§4§l> §fRegistre-se para ter o acesso ao chat liberado!");
			return;
		}

		String disabledFor = "§aO chat está ativado!";

		switch (getServerConfig().getChatState()) {
		case DISABLED: {
			if (member.getServerGroup().ordinal() < Group.ADMIN.ordinal())
				event.setCancelled(true);
			disabledFor = "§cO chat está ativado apenas para administradores!";
			break;
		}
		case YOUTUBER: {
			if (member.getServerGroup().ordinal() < Group.YOUTUBER.ordinal())
				event.setCancelled(true);
			disabledFor = "§cO chat está ativado apenas para youtubers!";
			break;
		}
		case PAYMENT: {
			if (member.getServerGroup().ordinal() < Group.LIGHT.ordinal())
				event.setCancelled(true);
			disabledFor = "§cO chat está ativado apenas para vips!";
			break;
		}
		case STAFF: {
			if (member.getServerGroup().ordinal() < Group.YOUTUBERPLUS.ordinal())
				event.setCancelled(true);
			disabledFor = "§cO chat está ativado apenas para pessoas da equipe!";
			break;
		}
		default:
			break;
		}

		if (event.isCancelled()) {
			TextComponent text = new TextComponent("§4§l> §fO chat está ");
			TextComponent disabled = new TextComponent("§cdesativado");
			disabled.setHoverEvent(
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(disabledFor)));
			text.addExtra(disabled);
			text.addExtra("§f!");

			member.sendMessage(text);
			return;
		}

		if (member.getLoginConfiguration().getAccountType() != AccountType.ORIGINAL) {
			if (member.getOnlineTime() + member.getSessionTime() <= 1000 * 60 * 10) {
				member.sendMessage("§cVocê precisa ficar online por mais "
						+ DateUtils.getTime(System.currentTimeMillis()
								+ ((1000 * 60 * 10) - (member.getOnlineTime() + member.getSessionTime())))
						+ " para ter o chat liberado!");
				event.setCancelled(true);
				return;
			}
		}

		if (CommonGeneral.getInstance().getServerType() != ServerType.SCREENSHARE) {
			Mute activeMute = member.getPunishmentHistory().getActiveMute();

			if (activeMute == null)
				return;

			member.sendMessage(
					"§4§l> §fVocê está mutado " + (activeMute.isPermanent() ? "permanentemente" : "temporariamente")
							+ " do servidor!" + (activeMute.isPermanent() ? ""
									: "\n §4§l> §fExpira em §e" + DateUtils.getTime(activeMute.getMuteExpire())));
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onSwearWord(AsyncPlayerChatEvent event) {
		if (CommonGeneral.getInstance().getServerType() == ServerType.SCREENSHARE)
			return;

		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (!member.hasGroupPermission(Group.MODPLUS)) {
			for (String string : event.getMessage().split(" ")) {
				if (BukkitConst.SWEAR_WORDS.contains(string.toLowerCase())) {
					StringBuilder stringBuilder = new StringBuilder();

					for (int x = 0; x < string.length(); x++)
						stringBuilder.append('*');

					event.setMessage(event.getMessage().replace(string, stringBuilder.toString().trim()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (!player.hasGroupPermission(Group.HELPER)) {
			if (player.isOnCooldown("chat-delay")) {
				event.setCancelled(true);
				player.sendMessage("§4§l> §fAguarde §e" + DateUtils.getTime(player.getCooldown("chat-delay"))
						+ "§f para falar no chat novamente!");
				return;
			}

			player.setCooldown("chat-delay",
					System.currentTimeMillis() + (player.hasGroupPermission(Group.YOUTUBER) ? 1000l : 3000l));
		}

		if (player.hasGroupPermission(Group.LIGHT))
			event.setMessage(event.getMessage().replace("&", "§"));

		TextComponent textComponent = player.getMedal() != null
				? new MessageBuilder("" + player.getMedal().getChatColor() + player.getMedal().getMedalIcon() + " ")
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								TextComponent.fromLegacyText(player.getMedal().getMedalDescription())))
						.create()
				: new TextComponent("");

		textComponent.addExtra(player.isUsingFake()
				? new MessageBuilder(ChatColor.GRAY + "(" + League.UNRANKED.getSymbol() + ChatColor.GRAY + ") ")
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								TextComponent.fromLegacyText(ChatColor.BOLD + League.UNRANKED.getSymbol() + " "
										+ ChatColor.BOLD + League.UNRANKED.name())))
						.create()
				: new MessageBuilder(ChatColor.GRAY + "(" + player.getLeague().getColor()
						+ player.getLeague().getSymbol() + ChatColor.GRAY + ") ")
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										TextComponent.fromLegacyText(ChatColor.BOLD + player.getLeague().getColor()
												+ player.getLeague().getSymbol() + " " + ChatColor.BOLD
												+ player.getLeague().name())))
								.create());

		String tag = player.getTag().getPrefix();

		textComponent.addExtra(new MessageBuilder(
				tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "") + event.getPlayer().getName())
						.setClickEvent(
								new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/account " + player.getPlayerName()))
						.setHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										TextComponent.fromLegacyText("§aInformações do player \n\n§fXp: §a"
												+ player.getXp() + "\n§fReputação: §a" + player.getReputation())))
						.create());

		textComponent.addExtra(new TextComponent(" §7»§f"));

		TextComponent[] textTo = new TextComponent[event.getMessage().split(" ").length];

		int i = 0;
		for (String msg : event.getMessage().split(" ")) {
			msg = " " + msg;

			TextComponent txt = new TextComponent(
					(i == 0 ? "§f" : ChatColor.getLastColors(textTo[i - 1].getText())) + msg);
			List<String> url = StringURLUtils.extractUrls(msg);

			if (player.hasGroupPermission(Group.SAINT)) {
				if (url.size() > 0) {
					txt.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.get(0)));

					if (player.hasGroupPermission(Group.SAINT))
						txt.setHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(url.get(0))));
					else
						txt.setHoverEvent(null);
				} else
					txt.setHoverEvent(null);
			} else
				txt.setHoverEvent(null);

			textTo[i] = txt;
			++i;
		}

		for (TextComponent txt : textTo)
			textComponent.addExtra(txt);

		for (Player r : event.getRecipients()) {
			try {
				BukkitMember receiver = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(r.getUniqueId());

				if (receiver == null) {
					new BukkitRunnable() {

						@Override
						public void run() {
							Player p = Bukkit.getPlayer(r.getUniqueId());

							if (p == null)
								return;

							if (!p.isOnline())
								return;

							p.kickPlayer("ERROR");
						}

					}.runTask(getMain());
					continue;
				}

				r.spigot().sendMessage(textComponent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("<" + player.getPlayerName() + "> " + event.getMessage());
	}

}
