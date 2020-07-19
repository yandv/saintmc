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

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.api.tag.Chroma;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
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

		if (!BukkitMain.getInstance().isTagControl()) {
			HandlerList.unregisterAll(this);
			return;
		}

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

			if (player == null)
				continue;

			player.setTag(player.getTag());
		}
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
		if (!main.isTagControl())
			return;

		Player p = e.getPlayer();

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());
		
		if (player == null) {
			p.kickPlayer("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fNão foi possível carregar sua conta!");
			return;
		}

		player.setTag(player.getTag() == null ? player.getDefaultTag() : player.getTag());

		for (Player o : Bukkit.getOnlinePlayers()) {
			if (!o.getUniqueId().equals(p.getUniqueId())) {
				BukkitMember bp = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
						.getMember(o.getUniqueId());

				if (bp == null)
					continue;

				String id = ScoreboardAPI.getTeamName(bp.getTag(), bp.getLeague(),
						bp.getTag().isChroma() || bp.isChroma(), isClanTag(bp), bp.getClan());

				String tag = BukkitMain.getInstance().isOldTag() ? ChatColor.getLastColors(bp.getTag().getPrefix())
						: bp.getTag().getPrefix();
				String suffix = getSuffix(bp, isClanTag(bp));

				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(p, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), suffix), o);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangeTag(PlayerChangeTagEvent event) {
		if (!main.isTagControl())
			return;

		Player p = event.getPlayer();
		BukkitMember player = (BukkitMember) event.getMember();

		if (player == null)
			return;

		String id = ScoreboardAPI.getTeamName(event.getNewTag(),
				player.isUsingFake() ? League.UNRANKED : player.getLeague(),
				event.getNewTag().isChroma() || player.isChroma(), event.isClanTag(), player.getClan());
		String oldId = ScoreboardAPI.getTeamName(event.getOldTag(),
				player.isUsingFake() ? League.UNRANKED : player.getLeague(),
				event.getNewTag().isChroma() || player.isChroma(), event.isClanTag(), player.getClan());

		String tag = BukkitMain.getInstance().isOldTag() ? ChatColor.getLastColors(event.getNewTag().getPrefix())
				: event.getNewTag().getPrefix();
		String suffix = getSuffix(player, event.isClanTag());

		if (event.getOldTag().isChroma() || player.isChroma())
			if (listener.getChromaList().contains(new Chroma(id, tag, suffix)))
				listener.getChromaList().remove(new Chroma(id, tag, suffix));

		if (event.getNewTag().isChroma() || player.isChroma())
			if (!listener.getChromaList().contains(new Chroma(id, tag, suffix)))
				listener.getChromaList().add(new Chroma(id, tag, suffix));

		/**
		 * TICKs++
		 */

		if (listener.getChromaList().isEmpty())
			listener.unregisterListener();
		else
			listener.registerListener();

		for (Player o : Bukkit.getOnlinePlayers()) {
			try {
				ScoreboardAPI.leaveTeamToPlayer(o, oldId, p);
				ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, id,
						tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : ""), suffix), p);
			} catch (Exception ex) {
			}
		}
	}

	private String getSuffix(BukkitMember player, boolean clanTag) {
		if (BukkitMain.getInstance().isOldTag())
			return "";

		return (clanTag ? " §7[" + player.getClan().getClanAbbreviation() + "]"
				: player.isUsingFake() ? " §7(" + League.UNRANKED.getColor() + League.UNRANKED.getSymbol() + "§7)"
						: " §7(" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7)");
	}

	public boolean isClanTag(Member member) {
		if (member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.ALL
				|| (CommonGeneral.getInstance().getServerType() == ServerType.LOBBY
						&& member.getAccountConfiguration().getClanDisplayType() == ClanDisplayType.LOBBY))
			return member.getClan() != null;
		return false;
	}

}
