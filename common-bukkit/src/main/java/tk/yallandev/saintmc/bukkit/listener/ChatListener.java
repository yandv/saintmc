package tk.yallandev.saintmc.bukkit.listener;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.chat.ChatAPI;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.StringURLUtils;

public class ChatListener implements Listener {

	private HashMap<UUID, Long> chatCooldown;

	public ChatListener() {
		chatCooldown = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		
		if (!member.getLoginConfiguration().isLogged()) {
			event.setCancelled(true);
			if (member.getLoginConfiguration().isRegistred()) {
				member.sendMessage("§4§l> §fLogue-se para ter o acesso ao chat liberado!");
			} else {
				member.sendMessage("§4§l> §fRegistre-se para ter o acesso ao chat liberado!");
			}
			return;
		}

		if (!member.hasGroupPermission(Group.MOD)) {
			if (chatCooldown.containsKey(member.getUniqueId())
					&& chatCooldown.get(member.getUniqueId()) > System.currentTimeMillis()) {
				event.setCancelled(true);
				member.sendMessage("§4§l> §fAguarde §e" + DateUtils.getTime(chatCooldown.get(member.getUniqueId()))
						+ "§f para falar no chat novamente!");
				return;
			}

			chatCooldown.put(member.getUniqueId(),
					System.currentTimeMillis() + (member.hasGroupPermission(Group.YOUTUBER) ? 2000l : 5000l));
		}

		String disabledFor = "§aO chat está ativado!";

		switch (ChatAPI.getInstance().getChatState()) {
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
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onAsyncPlayer(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		
		Mute activeMute = member.getPunishmentHistory().getActiveMute();

		if (activeMute == null)
			return;

		member.sendMessage(
				"§4§l> §fVocê está mutado " + (activeMute.isPermanent() ? "permanentemente" : "temporariamente")
						+ " do servidor!" + (activeMute.isPermanent() ? ""
								: "\n §4§l> §fExpira em §e" + DateUtils.getTime(activeMute.getMuteExpire())));
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (player == null) {
			event.setCancelled(true);
			return;
		}

		if (player.hasGroupPermission(Group.LIGHT))
			event.setMessage(event.getMessage().replace("&", "§"));

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

					}.runTask(BukkitMain.getInstance());
					continue;
				}

				TextComponent league = null;
				int text = 2;

				if (player.isUsingFake()) {
					league = new TextComponent(
							ChatColor.GRAY + "(" + League.UNRANKED.getSymbol() + ChatColor.GRAY + ") ");
					league.setHoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.BOLD
									+ League.UNRANKED.getSymbol() + " " + ChatColor.BOLD + League.UNRANKED.name())));
					text += 1;
				} else {
					league = new TextComponent(
							ChatColor.GRAY + "(" + player.getLeague().getColor() + player.getLeague().getSymbol() + ChatColor.GRAY + ") ");
					league.setHoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.BOLD
									+ player.getLeague().getColor() + player.getLeague().getSymbol() + " " + ChatColor.BOLD + player.getLeague().name())));
					text += 1;
				}

				TextComponent[] textTo = new TextComponent[text + event.getMessage().split(" ").length];
				String tag = player.getTag().getPrefix();
				TextComponent account = new TextComponent(
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "") + event.getPlayer().getName());
				account.setClickEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/account " + player.getPlayerName()));
				account.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						TextComponent.fromLegacyText("§aInformações do player \n\n§fXp: §a" + player.getXp() + "\n\n§fReputação: §a" + player.getReputation())));
				int i = 0;

				if (league != null) {
					textTo[i] = league;
					++i;
				}

				textTo[i] = account;
				++i;

				textTo[i] = new TextComponent(" §7»§f");
				++i;

				for (String msg : event.getMessage().split(" ")) {
					msg = " " + msg;
					TextComponent text2 = new TextComponent(ChatColor.getLastColors(textTo[i - 1].getText()) + msg);
					List<String> url = StringURLUtils.extractUrls(msg);

					if (player.hasGroupPermission(Group.SAINT)) {
						if (url.size() > 0) {
							text2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.get(0)));
						}
					}

					textTo[i] = text2;
					++i;
				}

				r.spigot().sendMessage(textTo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("<" + player.getPlayerName() + "> " + event.getMessage());
	}

}
