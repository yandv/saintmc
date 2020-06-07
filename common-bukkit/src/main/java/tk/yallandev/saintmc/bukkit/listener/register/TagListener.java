package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeTagEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.tag.Tag;

public class TagListener implements Listener {

	public TagListener() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

			if (player == null)
				continue;

			player.setTag(player.getTag());
		}
	}

	@EventHandler
	public void onQuit(PluginDisableEvent event) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			removePlayerTag(p);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		removePlayerTag(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinListener(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player == null)
			return;

		if (!BukkitMain.getInstance().isTagControl())
			return;

		player.setTag(player.getTag() == Tag.LOGANDO ? player.getDefaultTag() : player.getTag());

		for (Player o : Bukkit.getOnlinePlayers()) {
			if (!o.getUniqueId().equals(p.getUniqueId())) {
				BukkitMember bp = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(o.getUniqueId());

				if (bp == null)
					continue;

				String id = getTeamName(bp.getTag(), bp.getLeague());

				String tag = bp.getTag().getPrefix();
				String league = bp.isUsingFake()
						? " §7(" + League.UNRANKED.getColor() + League.UNRANKED.getSymbol() + "§7)"
						: " §7(" + bp.getLeague().getColor() + bp.getLeague().getSymbol() + "§7)";

				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(p, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), league), o);
			}
		}
	}

	@EventHandler
	public void onPlayerChangeTagListener(PlayerChangeTagEvent e) {
		Player p = e.getPlayer();

		if (!BukkitMain.getInstance().isTagControl())
			return;

		if (p == null)
			return;

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (player == null)
			return;

		String id = getTeamName(e.getNewTag(), player.getLeague());
		String oldId = getTeamName(e.getOldTag(), player.getLeague());

		for (final Player o : Bukkit.getOnlinePlayers()) {
			try {
				BukkitMember bp = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(o.getUniqueId());

				if (bp == null)
					continue;

				String tag = e.getNewTag().getPrefix();

				String league = player.isUsingFake()
						? " §7(" + League.UNRANKED.getColor() + League.UNRANKED.getSymbol() + "§7)"
						: " §7(" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7)";

				ScoreboardAPI.leaveTeamToPlayer(o, oldId, p);
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), league), p);
			} catch (Exception e2) {
			}
		}
	}

	public void removePlayerTag(Player p) {
		ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(p);
	}

	private static char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static String getTeamName(Tag tag, League liga) {
		return chars[tag.ordinal()] + "-" + chars[League.values().length - liga.ordinal()];
	}

}
