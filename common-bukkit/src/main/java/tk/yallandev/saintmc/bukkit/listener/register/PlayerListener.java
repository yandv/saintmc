package tk.yallandev.saintmc.bukkit.listener.register;

import java.io.File;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class PlayerListener extends Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED)
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "Ocorreu um erro!");
			return;
		}

		if (getServerConfig().isWhitelist()) {
			if (player.hasGroupPermission(Group.BUILDER)
					|| getServerConfig().hasWhitelist(new Profile(player.getPlayerName(), player.getUniqueId())))
				event.allow();
			else
				event.disallow(Result.KICK_OTHER, "§cO servidor está disponivel somente para a equipe!");
		} else
			event.allow();

		if (getServerConfig().isBlackedlist(new Profile(player.getPlayerName(), player.getUniqueId())))
			event.disallow(Result.KICK_OTHER,
					"§cVocê está bloqueado de entrar nesse servidor! Expire em " + DateUtils.getTime(getServerConfig()
							.getBlacklistTime(new Profile(player.getPlayerName(), player.getUniqueId()))));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoinMonitor(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		VanishAPI.getInstance().updateVanishToPlayer(player);

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(player.getUniqueId()))
				continue;

			PlayerShowToPlayerEvent eventCall = new PlayerShowToPlayerEvent(player, online);
			Bukkit.getPluginManager().callEvent(eventCall);

			if (eventCall.isCancelled()) {
				if (online.canSee(player))
					online.hidePlayer(player);
			} else if (!online.canSee(player))
				online.showPlayer(player);
		}

		player.awardAchievement(Achievement.OPEN_INVENTORY);

		VanishAPI.getInstance().updateHide(player);
	}

	@EventHandler
	public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
		if (event.getPlayer() != null && event.getNewLeague().ordinal() > event.getOldLeague().ordinal()) {

			if (event.getNewLeague() == League.CHALLENGER) {
				Bukkit.broadcastMessage(" ");
				Bukkit.broadcastMessage(
						"§e" + event.getBukkitMember().getPlayerName() + "§f subiu para o rank §4§lCHALLENGER§f!");
				Bukkit.broadcastMessage(" ");

				Bukkit.getOnlinePlayers()
						.forEach(player -> player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 0.1f));
			}

			event.getPlayer().sendMessage("§a§l> §fVocê subiu para o rank §a" + event.getNewLeague().getColor()
					+ event.getNewLeague().getSymbol() + " " + event.getNewLeague().getName());
		} else if (event.getNewLeague().ordinal() < event.getOldLeague().ordinal()) {
			event.getPlayer().sendMessage("§c§l> §fVocê desceu para o rank §c" + event.getNewLeague().getColor()
					+ event.getNewLeague().getSymbol() + " " + event.getNewLeague().getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		AdminMode.getInstance().removeAdmin(event.getPlayer());
		VanishAPI.getInstance().removeVanish(event.getPlayer());
		CommonGeneral.getInstance().getStatusManager().unloadStatus(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if (getMain().isRemovePlayerDat())
			removePlayerFile(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (getMain().isRemovePlayerDat())
			removePlayerFile(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerQuit(PlayerAchievementAwardedEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		event.setCancelled(true);
	}

	private void removePlayerFile(UUID uuid) {
		World world = Bukkit.getWorlds().get(0);
		File folder = new File(world.getWorldFolder(), "playerdata");

		if (folder.exists() && folder.isDirectory()) {
			File file = new File(folder, uuid.toString() + ".dat");
			Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitMain.getInstance(), () -> {
				if (file.exists() && !file.delete()) {
					removePlayerFile(uuid);
				}
			}, 2L);
		}
	}

}
