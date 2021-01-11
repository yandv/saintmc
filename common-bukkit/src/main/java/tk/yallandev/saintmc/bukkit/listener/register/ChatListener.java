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
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.string.StringURLUtils;

public class ChatListener extends Listener {

	private static final long ONLINE_TIME_TO_CHAT = 1000l * 60l * 5l;

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member == null) {
			new BukkitRunnable() {

				@Override
				public void run() {
					player.kickPlayer("§cSua conta não foi carregada!");
				}

			}.runTask(getMain());
			return;
		}

		if (!member.getLoginConfiguration().isLogged()) {
			event.setCancelled(true);

			if (member.getLoginConfiguration().isRegistred())
				member.sendMessage("§cLogue-se para ter o acesso ao chat liberado!");
			else
				member.sendMessage("§cRegistre-se para ter o acesso ao chat liberado!");
			return;
		}

		if (member.getGroup() == Group.MEMBRO)
			if (member.getLoginConfiguration().getAccountType() != AccountType.ORIGINAL) {
				if (member.getOnlineTime() + member.getSessionTime() <= ONLINE_TIME_TO_CHAT) {
					member.sendMessage("§cVocê precisa ficar online por mais "
							+ DateUtils.getTime(System.currentTimeMillis()
									+ (ONLINE_TIME_TO_CHAT - (member.getOnlineTime() + member.getSessionTime())))
							+ " para ter o chat liberado!");
					event.setCancelled(true);
					return;
				}
			}

		String disabledFor;

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
			if (member.getServerGroup().ordinal() < Group.PRO.ordinal())
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
		default: {
			disabledFor = "§aO chat está ativado!";
			break;
		}
		}

		if (event.isCancelled()) {
			member.sendMessage(new MessageBuilder("§cO chat está desativado!")
					.setHoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(disabledFor)))
					.create());
			return;
		}

		if (CommonGeneral.getInstance().getServerType() != ServerType.SCREENSHARE) {
			Mute activeMute = member.getPunishmentHistory().getActiveMute();

			if (activeMute == null)
				return;

			member.sendMessage("§cVocê está mutado "
					+ (activeMute.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor por "
					+ activeMute.getReason().toLowerCase() + "!" + (activeMute.isPermanent() ? ""
							: "\n§cExpira em " + DateUtils.getTime(activeMute.getMuteExpire())));
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

		if (!player.hasGroupPermission(Group.ELITE)) {
			if (player.isOnCooldown("chat-delay")) {
				event.setCancelled(true);
				player.sendMessage(
						"§cAguarde " + DateUtils.getTime(player.getCooldown("chat-delay")) + " para falar novamente!");
				return;
			}

			player.setCooldown("chat-delay",
					System.currentTimeMillis() + (player.hasGroupPermission(Group.PRO) ? 1000l : 3000l));
		}

		if (player.hasGroupPermission(Group.PRO))
			event.setMessage(event.getMessage().replace("&", "§"));

		TextComponent textComponent = new TextComponent("");

		if (player.getMedal() != null && player.getMedal() != Medal.NONE)
			textComponent.addExtra(
					new MessageBuilder("" + player.getMedal().getChatColor() + player.getMedal().getMedalIcon() + " ")
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									TextComponent.fromLegacyText("§fMedalha " + player.getMedal().getMedalName() + "\n"
											+ player.getMedal().getMedalRarity().getName() + "\n\n§f"
											+ player.getMedal().getMedalDescription())))
							.create());

		textComponent.addExtra(player.isUsingFake()
				? new MessageBuilder(ChatColor.GRAY + "(§f" + League.values()[0].getSymbol() + ChatColor.GRAY + ") ")
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								TextComponent.fromLegacyText(ChatColor.BOLD + League.values()[0].getSymbol() + " "
										+ ChatColor.BOLD + League.values()[0].name())))
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

		String[] split = event.getMessage().split(" ");
		TextComponent[] txt = new TextComponent[split.length];

		for (int x = 0; x < split.length; x++) {
			String msg = " " + (x == 0 ? "" : ChatColor.getLastColors(txt[x - 1].getText())) + split[x];

			List<String> url = StringURLUtils.extractUrls(msg);

			TextComponent t = url.isEmpty() ? new MessageBuilder(msg).setHoverable(true).create()
					: new MessageBuilder(msg).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.get(0)))
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									TextComponent.fromLegacyText(url.get(0))))
							.create();

			txt[x] = t;
		}

		for (TextComponent t : txt)
			textComponent.addExtra(t);

		for (Player r : event.getRecipients()) {
			try {
				BukkitMember receiver = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(r.getUniqueId());

				if (receiver == null) {
					new BukkitRunnable() {

						@Override
						public void run() {
							Player p = Bukkit.getPlayer(r.getUniqueId());

							if (p == null || !p.isOnline())
								return;

							p.kickPlayer("§cSua conta não foi carregada!");
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
