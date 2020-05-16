package tk.yallandev.saintmc.kitpvp.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.Tag;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.challenge.FightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowSearchingStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowSearchingStopEvent;
import tk.yallandev.saintmc.kitpvp.event.kit.PlayerSelectKitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.kit.Kit;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;

public class ScoreboardListener implements Listener {

	public static final Scoreboard DEFAULT_SCOREBOARD;
	public static final Scoreboard SHADOW_SCOREBOARD;
	public static final Scoreboard SEARCHING_SCOREBOARD;

	static {
		DEFAULT_SCOREBOARD = new SimpleScoreboard("§b§lSAINTMC");

		DEFAULT_SCOREBOARD.blankLine(12);
		DEFAULT_SCOREBOARD.setScore(11, new Score("§fRanking: §7(§f-§7)", "rank"));
		DEFAULT_SCOREBOARD.setScore(10, new Score("§fXp: §a0", "xp"));
		DEFAULT_SCOREBOARD.blankLine(9);
		DEFAULT_SCOREBOARD.setScore(8, new Score("§fKills: §70", "kills"));
		DEFAULT_SCOREBOARD.setScore(7, new Score("§fDeaths: §70", "deaths"));
		DEFAULT_SCOREBOARD.setScore(6, new Score("§fKillstreak: §70", "killstreak"));
		DEFAULT_SCOREBOARD.blankLine(5);
		DEFAULT_SCOREBOARD.setScore(4, new Score("§fMoney: §60", "coins"));
		DEFAULT_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		DEFAULT_SCOREBOARD.blankLine(2);
		DEFAULT_SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
		
		SHADOW_SCOREBOARD = new SimpleScoreboard("§b§lSAINTMC");
		
		SHADOW_SCOREBOARD.blankLine(11);
		SHADOW_SCOREBOARD.setScore(10, new Score("§fRanking: §a-/-", "rank"));
        SHADOW_SCOREBOARD.setScore(9, new Score("§fXp: §a0", "xp"));
		SHADOW_SCOREBOARD.blankLine(8);
		SHADOW_SCOREBOARD.setScore(7, new Score("§fKills: §70", "kills"));
		SHADOW_SCOREBOARD.setScore(6, new Score("§fDeaths: §70", "deaths"));
		SHADOW_SCOREBOARD.setScore(5, new Score("§fWinstreak: §70", "winstreak"));
		SHADOW_SCOREBOARD.blankLine(4);
		SHADOW_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		SHADOW_SCOREBOARD.blankLine(2);
        SHADOW_SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
        
        SEARCHING_SCOREBOARD = new SimpleScoreboard("§b§lSEARCHING");
		
        SEARCHING_SCOREBOARD.blankLine(10);
        SEARCHING_SCOREBOARD.setScore(9, new Score("§fRanking: §a-/-", "rank"));
		SEARCHING_SCOREBOARD.setScore(8, new Score("§fXp: §a0", "xp"));
        SEARCHING_SCOREBOARD.blankLine(7);
        SEARCHING_SCOREBOARD.setScore(6, new Score("§fProcurando: §7", "searching"));
        SEARCHING_SCOREBOARD.setScore(5, new Score("§fTempo: §a0s", "time"));
        SEARCHING_SCOREBOARD.blankLine(4);
        SEARCHING_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
        SEARCHING_SCOREBOARD.blankLine(2);
        SEARCHING_SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
	}
	
	private List<FightPingUpdate> observersList;

	public ScoreboardListener() {
		observersList = new ArrayList<>();
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			observersList.forEach(FightPingUpdate::check);
	}

	@EventHandler
	public void onFightStart(FightStartEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		Scoreboard scoreboard = createScoreboard(player, target, event instanceof ShadowFightStartEvent);
		Scoreboard scoreboardTarget = createScoreboard(target, player, event instanceof ShadowFightStartEvent);

		scoreboard.createScoreboard(player);
		scoreboardTarget.createScoreboard(target);

		observersList.add(new FightPingUpdate(player, target) {

			@Override
			public FightPingUpdate onUpdate(Player p, int ping) {
				scoreboard.updateScore(player,
						new Score("§9" + player.getName() + ": §7" + ((CraftPlayer) player).getHandle().ping + "ms",
								"playerPing"));
				scoreboard.updateScore(player,
						new Score("§c" + target.getName() + ": §7" + ((CraftPlayer) target).getHandle().ping + "ms",
								"targetPing"));

				scoreboardTarget.updateScore(getTarget(),
						new Score("§9" + target.getName() + ": §7" + ((CraftPlayer) target).getHandle().ping + "ms",
								"playerPing"));
				scoreboardTarget.updateScore(getTarget(),
						new Score("§c" + player.getName() + ": §7" + ((CraftPlayer) player).getHandle().ping + "ms",
								"targetPing"));
				return this;
			}

		}.onUpdate(player, ((CraftPlayer) player).getHandle().ping).onUpdate(target,
				((CraftPlayer) target).getHandle().ping));
	}

	@EventHandler
	public void onFightFinish(FightFinishEvent event) {
		observersList
				.removeIf(update -> update.getTarget() == event.getPlayer() || update.getTarget() == event.getTarget());

		Warp warp = GameMain.getInstance().getWarpManager()
				.getWarpByName(event instanceof ShadowFightFinishEvent ? "1v1" : "sumo");

		if (warp == null) {
			System.out.println("Not found warp " + (event instanceof ShadowFightFinishEvent ? "1v1" : "sumo") + "!");
			return;
		}

		loadScoreboard(event.getTarget(), warp);
		loadScoreboard(event.getPlayer(), warp);
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		loadScoreboard(event.getPlayer(), event.getWarp());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		Player player = event.getPlayer();
		Kit kit = event.getKit();

		DEFAULT_SCOREBOARD.updateScore(player, new Score("§fKit: §6" + kit.getKitName(), "coins"));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!event.getPlayer().isOnline())
					return;
				
				Player player = event.getPlayer();
				BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
				
				Scoreboard scoreboard = member.getScoreboard();

				if (scoreboard != null) {
					scoreboard.updateScore(new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
					scoreboard.updateScore(player,
							new Score("§fGrupo: §7" + (member.getGroup() == Group.MEMBRO ? "Membro"
									: Tag.valueOf(member.getGroup().name()).getPrefix()), "group"));
				}
			}
		}.runTaskLater(GameMain.getInstance(), 20l);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!event.getPlayer().isOnline())
					return;
				
				BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
				Scoreboard scoreboard = member.getScoreboard();

				if (scoreboard != null)
					scoreboard.updateScore(
							new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size() - 1), "players"));
			}
		}.runTaskLater(GameMain.getInstance(), 20l);
	}

	@EventHandler
	public void onPlayerChangeTag(PlayerChangeGroupEvent event) {
		Player player = event.getPlayer();
		Scoreboard scoreboard = event.getBukkitMember().getScoreboard();

		if (scoreboard == null)
			return;
		
		Group group = event.getGroup();

		scoreboard.updateScore(player, new Score("§fGrupo: §7"
				+ (group == Group.MEMBRO ? "Membro" : Tag.valueOf(group.name()).getPrefix()),
				"group"));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShadowSearchingStart(ShadowSearchingStartEvent event) {
		Player player = event.getPlayer();
		Warp warp = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp();
		Scoreboard scoreboard = SEARCHING_SCOREBOARD;
		
		scoreboard.createScoreboard(player);
		scoreboard.updateScore(player, new Score("§fProcurando: §7" + (warp instanceof ShadowWarp ? "1v1 rápido" : "Sumo rápido"), "searching"));
		scoreboard.updateScore(player, new Score("§fTempo: §a0s", "time"));
		updateScore(player, scoreboard);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShadowSearchingStop(ShadowSearchingStopEvent event) {
		Player player = event.getPlayer();
		loadScoreboard(player, GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp());
	}

	public void loadScoreboard(Player player, Warp warp) {
		Scoreboard scoreboard = warp.getScoreboard();

		scoreboard.createScoreboard(player);
		updateScore(player, scoreboard);
	}
	
	public void updateScore(Player player, Scoreboard scoreboard) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		scoreboard.updateScore(player, new Score(
				"§fRanking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
		scoreboard.updateScore(player, new Score("§fGrupo: §7"
				+ (member.getGroup() == Group.MEMBRO ? "Membro" : Tag.valueOf(member.getGroup().name()).getPrefix()),
				"group"));
		scoreboard.updateScore(player, new Score("§fKills: §70", "kills"));
		scoreboard.updateScore(player, new Score("§fDeaths: §70", "deaths"));
		scoreboard.updateScore(player, new Score("§fKillstreak: §70", "killstreak"));
		scoreboard.updateScore(player, new Score("§fMoney: §60", "coins"));
		scoreboard.updateScore(player, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
	}

	public Scoreboard createScoreboard(Player player, Player target, boolean shadow) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		Scoreboard scoreboard = new SimpleScoreboard("§b§lSAINTMC");

		scoreboard.blankLine(11);
		scoreboard.setScore(10, new Score("§fGrupo: §7"
				+ (member.getGroup() == Group.MEMBRO ? "Membro" : Tag.valueOf(member.getGroup().name()).getPrefix()),
				"group"));
		scoreboard.setScore(4, new Score(
				"§fRanking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
		scoreboard.blankLine(9);
		scoreboard.setScore(8, new Score("§9" + player.getName() + ": §70ms", "playerPing"));
		scoreboard.setScore(7, new Score("§c" + target.getName() + ": §70ms", "targetPing"));
		scoreboard.blankLine(5);
		scoreboard.setScore(4, new Score("§fWarp: §a" + (shadow ? "1v1" : "Sumo"), "warp"));
		scoreboard.setScore(3, new Score("§fWinstreak: §70", "winstreak"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§awww.saint-mc.com.br", "site"));

		return scoreboard;
	}

	@AllArgsConstructor
	@Getter
	public abstract class FightPingUpdate {

		private Player player;
		private Player target;

		public void check() {
			if (player.isOnline())
				onUpdate(player, ((CraftPlayer) player).getHandle().ping);
			else {
				observersList.remove(this);
				return;
			}

			if (target.isOnline())
				onUpdate(target, ((CraftPlayer) target).getHandle().ping);
			else
				observersList.remove(this);
		}

		public abstract FightPingUpdate onUpdate(Player player, int ping);

	}

}
