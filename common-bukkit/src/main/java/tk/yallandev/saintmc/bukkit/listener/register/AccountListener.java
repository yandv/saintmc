package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitClan;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerUpdateFieldEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerUpdatedFieldEvent;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreInitEvent;
import tk.yallandev.saintmc.bukkit.event.restore.RestoreStopEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;
import tk.yallandev.saintmc.common.clan.enums.ClanHierarchy;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.profile.Profile;

public class AccountListener extends Listener {

	private List<Profile> restoreProfile = new ArrayList<>();

	@EventHandler(priority = EventPriority.LOW)
	public synchronized void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
			return;

		if (BukkitMain.getInstance().getServerConfig().isRestoreMode()
				&& !restoreProfile.contains(new Profile(event.getName(), event.getUniqueId()))) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
					"§cO servidor está em modo restauração, somente jogadores que já estavam no servidor podem entrar!");
			return;
		}
		if (event.getAddress().getAddress().toString().startsWith("0.0")) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
					"§4§l" + CommonConst.KICK_PREFIX + "\n\n§fEndereço de host inválido!");
			return;
		}

		UUID uniqueId = event.getUniqueId();

		if (Bukkit.getPlayer(uniqueId) != null) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fO jogador " + event.getName()
					+ " já está online no servidor!");
			return;
		}

		String playerName = event.getName();

		try {
			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);

			if (memberModel == null) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cSua conta não foi carregada!");
				return;
			}

			BukkitMember member = new BukkitMember(memberModel);

			if (CommonGeneral.getInstance().getPlayerData().checkCache(uniqueId))
				member.setCacheOnQuit(true);

			member.setJoinData(playerName, event.getAddress().getHostAddress());
			CommonGeneral.getInstance().getMemberManager().loadMember(member);

			if (member.getClanUniqueId() != null) {
				Clan clan = CommonGeneral.getInstance().getClanManager().getClan(member.getClanUniqueId());

				if (clan == null) {
					ClanModel clanModel = CommonGeneral.getInstance().getClanData().loadClan(member.getClanUniqueId());

					if (clanModel == null) {
						member.setClanUniqueId(null);
					} else {

						clan = new BukkitClan(clanModel);
						CommonGeneral.getInstance().getClanManager().load(member.getClanUniqueId(), clan);
						CommonGeneral.getInstance().debug("Clan " + clan.getClanName() + " has been loaded!");
					}
				}
			}
		} catch (Exception ex) {
			event.setKickMessage("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fNão foi possível carregar sua conta!");
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayer(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			CommonGeneral.getInstance().getMemberManager().unloadMember(event.getUniqueId());
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		if (CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()) == null) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
					"§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fNão foi possível carregar sua conta!");
			return;
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		member.setPlayer(event.getPlayer());

		if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
			if (member.hasGroupPermission(Group.VIP))
				event.setResult(PlayerLoginEvent.Result.ALLOWED);
			else {
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO servidor está cheio!");
				return;
			}
		}

		if (!member.hasGroupPermission(Group.TRIAL)) {
			if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers() + 20) {
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO servidor está lotado!");
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLoginMonitor(PlayerLoginEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (member == null) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
					"§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fNão foi possível carregar sua conta!");
			return;
		}

		if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
			member.connect(CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType());
		} else
			CommonGeneral.getInstance().getMemberManager().unloadMember(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().joinPlayer(event.getPlayer().getUniqueId());
			}

		}.runTaskAsynchronously(BukkitMain.getInstance());
		CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()).checkRanks();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		removePlayer(event.getPlayer().getUniqueId());
		event.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRestoreInit(RestoreInitEvent event) {
		restoreProfile = event.getProfileList();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRestoreStop(RestoreStopEvent event) {
		restoreProfile.clear();
	}

	@EventHandler
	public void onUpdateField(PlayerUpdateFieldEvent event) {
		BukkitMember player = event.getBukkitMember();
		switch (event.getField()) {
		case "league":
			player.setLeague((League) event.getObject());
			event.setCancelled(true);
			break;
		}
	}

	@EventHandler
	public void onUpdate(PlayerUpdatedFieldEvent event) {
		BukkitMember player = event.getBukkitMember();
		switch (event.getField()) {
		case "group":
		case "ranks":
			player.loadTags();
			player.setTag(player.getDefaultTag());
			Bukkit.getPluginManager()
					.callEvent(new PlayerChangeGroupEvent(event.getPlayer(), player, player.getServerGroup()));
			break;
		case "clanUniqueId":
			if (event.getObject() == null) {
				UUID uuid = (UUID) event.getOldObject();

				if (uuid == null) {
					return;
				}

				Clan clan = CommonGeneral.getInstance().getClanManager().getClan(uuid);

				if (clan == null)
					return;

				if (clan.isGroup(player.getUniqueId(), ClanHierarchy.OWNER)) {
					CommonGeneral.getInstance().getClanManager().unload(clan.getUniqueId());
				} else {
					clan.removeMember(player);
					handleUnload(clan);
				}
			} else {
				Clan clan = CommonGeneral.getInstance().getClanManager().getClan((UUID) event.getObject());

				if (clan == null) {
					ClanModel clanModel = CommonGeneral.getInstance().getClanData().loadClan((UUID) event.getObject());

					if (clanModel == null) {
						player.sendMessage("§cNão foi possível carregar seu clan!");
						return;
					}

					clan = new BukkitClan(clanModel);
				}
			}
			break;
		}
	}

	private void removePlayer(UUID uniqueId) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

		if (player == null)
			return;

		CommonGeneral.getInstance().getMemberManager().unloadMember(uniqueId);

		Clan clan = CommonGeneral.getInstance().getClanManager().getClan(player.getClanUniqueId());

		if (clan != null)
			handleUnload(clan);

		CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

			@Override
			public void run() {
				if (player.isCacheOnQuit())
					CommonGeneral.getInstance().getPlayerData().cacheMember(uniqueId);

				CommonGeneral.getInstance().getServerData().leavePlayer(uniqueId);
			}
		});
	}

	private void handleUnload(Clan clan) {
		if (clan.getOnlineMembers().size() == 0) {
			CommonGeneral.getInstance().debug("Clan " + clan.getClanName() + " has been unloaded!");
			CommonGeneral.getInstance().getClanManager().unload(clan.getUniqueId());
		}
	}

}
