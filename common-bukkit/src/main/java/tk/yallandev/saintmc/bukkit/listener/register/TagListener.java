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
import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.api.tag.Chroma;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeMedalEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeTagEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.enums.ClanDisplayType;
import tk.yallandev.saintmc.common.server.ServerType;

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
	public void onPluginDisable(PluginDisableEvent event) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(p);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
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

				String id = ScoreboardAPI.getTeamName(bp.getTag(), bp.getLeague(), player.getMedal(),
						bp.getTag().isChroma() || bp.isChroma(), isClanTag(bp), bp.getClan());

				String tag = bp.getTag().getPrefix();
				String suffix = getPrefix(bp);

				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(p, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), suffix), o);
			}
		}
	}

	@EventHandler
	public void onPlayerChangeTagListener(PlayerChangeTagEvent e) {
		Player p = e.getPlayer();

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (player == null)
			return;

		String id = ScoreboardAPI.getTeamName(e.getNewTag(), player.getLeague(), player.getMedal(),
				e.getNewTag().isChroma() || player.isChroma(), isClanTag(player), player.getClan());
		String oldId = ScoreboardAPI.getTeamName(e.getOldTag(), player.getLeague(), player.getMedal(),
				e.getNewTag().isChroma() || player.isChroma(), isClanTag(player), player.getClan());

		String tag = e.getNewTag().getPrefix();
		String suffix = getPrefix(player);

		if (e.getOldTag().isChroma() || player.isChroma()) {
			if (listener.getChromaList().contains(new Chroma(id, tag, suffix))) {
				listener.getChromaList().remove(new Chroma(id, tag, suffix));

			}
		}

		if (e.getNewTag().isChroma() || player.isChroma())
			if (!listener.getChromaList().contains(new Chroma(id, tag, suffix))) {
				listener.getChromaList().add(new Chroma(id, tag, suffix));
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
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), suffix), p);
			} catch (Exception ex) {
			}
		}
	}

	@EventHandler
	public void onPlayerChangeMedal(PlayerChangeMedalEvent e) {
		Player p = e.getPlayer();

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (player == null)
			return;

		String suffix = getPrefix(player);

		String oldId = ScoreboardAPI.getTeamName(player.getTag(), player.getLeague(), e.getOldMedal(),
				player.getTag().isChroma() || player.isChroma(), isClanTag(player), player.getClan());
		String id = ScoreboardAPI.getTeamName(player.getTag(), player.getLeague(), e.getMedal(),
				player.getTag().isChroma() || player.isChroma(), isClanTag(player), player.getClan());

		String tag = player.getTag().getPrefix();

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
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), suffix), p);
			} catch (Exception ex) {
			}
		}
	}

	private String getPrefix(BukkitMember player) {
		return (isClanTag(player) ? " §7[" + player.getClan().getClanAbbreviation() + "]"
				: player.isUsingFake() ? " §7(" + League.UNRANKED.getColor() + League.UNRANKED.getSymbol() + "§7)"
						: " §7(" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7)")
				+ (player.getMedal() == null ? ""
						: " " + player.getMedal().getChatColor() + player.getMedal().getMedalIcon());
	}

	boolean isClanTag(Member member) {
		if (member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.ALL
				|| (CommonGeneral.getInstance().getServerType() == ServerType.LOBBY
						&& member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.LOBBY))
			return member.getClan() != null;
		return false;
	}

}
