package tk.yallandev.saintmc.bukkit.listener.register;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerUpdateFieldEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerUpdatedFieldEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.MemberModel;

public class AccountListener extends Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getAddress().getAddress().toString().startsWith("0.0")) {
			event.disallow(Result.KICK_OTHER, "§4§l" + CommonConst.KICK_PREFIX + "\n\n§fEndereço de host inválido!");
			return;
		}

		UUID uniqueId = event.getUniqueId();

		if (Bukkit.getPlayer(uniqueId) != null) {
			event.setLoginResult(Result.KICK_OTHER);
			event.setKickMessage("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fO jogador " + event.getName()
					+ " já está online no servidor!");
			return;
		}

		String playerName = event.getName();

		try {

			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);
			BukkitMember member = null;

			if (memberModel == null) {
				member = new BukkitMember(playerName, uniqueId);
				CommonGeneral.getInstance().getPlayerData().createMember(member);
				member.setCacheOnQuit(true);
			} else {
				member = new BukkitMember(memberModel);
				member.setCacheOnQuit(true);
			}

			member.setJoinData(playerName, event.getAddress().getHostAddress());
			CommonGeneral.getInstance().getMemberManager().loadMember(member);
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
		member.connect(CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType());

		new BukkitRunnable() {

			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().joinPlayer(event.getPlayer().getUniqueId());
			}

		}.runTaskAsynchronously(BukkitMain.getInstance());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(BukkitMain.getInstance(),
				() -> removePlayer(event.getPlayer().getUniqueId()));
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onUpdateField(PlayerUpdateFieldEvent event) {
		BukkitMember player = event.getBukkitMember();
		switch (event.getField()) {
		case "league":
			player.setLeague((League) event.getObject());
			event.setCancelled(true);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onUpdate(PlayerUpdatedFieldEvent event) {
		BukkitMember player = event.getBukkitMember();
		switch (event.getField()) {
		case "group":
		case "ranks": {
			player.loadTags();
			player.setTag(player.getDefaultTag());
			Bukkit.getPluginManager()
					.callEvent(new PlayerChangeGroupEvent(event.getPlayer(), player, player.getServerGroup()));
			break;
		}
		default:
			break;
		}
	}

	private void removePlayer(UUID uniqueId) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

		if (player == null)
			return;

		if (player.isCacheOnQuit())
			CommonGeneral.getInstance().getPlayerData().cacheMember(uniqueId);

		CommonGeneral.getInstance().getServerData().leavePlayer(uniqueId);
		CommonGeneral.getInstance().getMemberManager().unloadMember(uniqueId);
	}

}
