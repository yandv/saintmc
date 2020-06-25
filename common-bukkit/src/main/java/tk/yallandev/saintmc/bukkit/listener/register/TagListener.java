package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.api.tag.Chroma;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeTagEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;

public class TagListener implements Listener {

	private BukkitMain main;
	private ChromaListener listener;

	public TagListener() {
		main = BukkitMain.getInstance();
		listener = new ChromaListener();

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

			if (player == null)
				continue;

			player.setTag(player.getTag());
		}

		if (!BukkitMain.getInstance().isTagControl())
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onQuit(PluginDisableEvent event) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(p);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (!main.isTagControl())
			return;

		player.setTag(player.getTag() == null ? player.getDefaultTag() : player.getTag());
		
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (!o.getUniqueId().equals(p.getUniqueId())) {
				BukkitMember bp = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(o.getUniqueId());

				if (bp == null)
					continue;

				String id = ScoreboardAPI.getTeamName(bp.getTag(), bp.getLeague(),
						bp.getTag().isChroma() || bp.isChroma());

				String tag = bp.getTag().getPrefix();
				String league = bp.isUsingFake()
						? " §7(" + League.UNRANKED.getColor() + League.UNRANKED.getSymbol() + "§7)"
						: " §7(" + bp.getLeague().getColor() + bp.getLeague().getSymbol() + "§7)";

				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(p, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), league), o);
			}
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler
	public void onPlayerChangeTagListener(PlayerChangeTagEvent e) {
		Player p = e.getPlayer();

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (player == null)
			return;
		
		String id = ScoreboardAPI.getTeamName(e.getNewTag(), player.getLeague(),
				e.getNewTag().isChroma() || player.isChroma());
		String oldId = ScoreboardAPI.getTeamName(e.getOldTag(), player.getLeague(),
				e.getNewTag().isChroma() || player.isChroma());

		String tag = e.getNewTag().getPrefix();
		String league = player.isUsingFake() ? " §7(" + League.UNRANKED.getColor() + League.UNRANKED.getSymbol() + "§7)"
				: " §7(" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7)";

		if (e.getOldTag().isChroma() || player.isChroma()) {
			if (listener.getChromaList().contains(e.getOldTag())) {
				listener.getChromaList().remove(new Chroma(id, tag, league));

			}
		}

		if (e.getNewTag().isChroma() || player.isChroma())
			if (!listener.getChromaList().contains(e.getNewTag())) {
				listener.getChromaList().add(new Chroma(id, tag, league));
			}

		/**
		 * TICKs++
		 */

		if (listener.getChromaList().isEmpty())
			listener.unregisterListener();
		else
			listener.registerListener();

		for (Player o : Bukkit.getOnlinePlayers()) {
			try {
				BukkitMember bp = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(o.getUniqueId());

				if (bp == null)
					continue;

				ScoreboardAPI.leaveTeamToPlayer(o, oldId, p);
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), league), p);
			} catch (Exception ex) {
			}
		}
	}

}
