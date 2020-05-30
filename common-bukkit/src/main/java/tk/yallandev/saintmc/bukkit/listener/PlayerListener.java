package tk.yallandev.saintmc.bukkit.listener;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;

public class PlayerListener implements Listener {

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "Ocorreu um erro!");
			return;
		}

		if (event.getResult() == Result.KICK_WHITELIST) {
			if (player.hasGroupPermission(Group.BUILDER)) {
				event.allow();
			} else {
				event.disallow(Result.KICK_OTHER, "§4§l" + CommonConst.KICK_PREFIX
						+ "\n\n§cO servidor está em manutenção!\n\n§6Entre em nosso discord para mais informações!\n§b"
						+ CommonConst.DISCORD);
			}
		}

		if (event.getAddress().getAddress().toString().startsWith("0.0")) {
			event.disallow(Result.KICK_OTHER, "§4§l" + CommonConst.KICK_PREFIX + "\n\n§fEndereço de host inválido!");
		}
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

		VanishAPI.getInstance().playerJoin(player);

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		PermissionAttachment permissionAttachment = player.addAttachment(BukkitMain.getInstance());

		for (String permission : member.getPermissions().keySet())
			permissionAttachment.setPermission(permission, true);

		for (String permission : member.getServerGroup().getGroup().getPermissions())
			permissionAttachment.setPermission(permission, true);

		player.recalculatePermissions();

		new BukkitRunnable() {

			@Override
			public void run() {
				if (CommonGeneral.getInstance().getServerType() != ServerType.SCREENSHARE)
					if (member.getAccountConfiguration().isAdminOnJoin())
						if (member.hasGroupPermission(Group.TRIAL))
							if (!AdminMode.getInstance().isAdmin(player)) 
								AdminMode.getInstance().setAdmin(player, member);
			}
		}.runTaskLater(BukkitMain.getInstance(), 10);
	}

    @EventHandler
    public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
        if (event.getPlayer() != null && event.getNewLeague().ordinal() > event.getOldLeague().ordinal()) {
        	
        	if (event.getNewLeague() == League.CHALLENGER) {
        		Bukkit.broadcastMessage(" ");
        		Bukkit.broadcastMessage("§e" + event.getBukkitMember().getPlayerName() + "§f subiu para o rank §4§lCHALLENGER§f!");
        		Bukkit.broadcastMessage(" ");
        		
        		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 0.1f));
        	}
        	
            event.getPlayer().sendMessage("§a§l> §fVocê subiu para o rank §a" + event.getNewLeague().getColor() + event.getNewLeague().getSymbol() + " " + event.getNewLeague().getName());
        } else if (event.getNewLeague().ordinal() < event.getOldLeague().ordinal()) {
            event.getPlayer().sendMessage("§c§l> §fVocê desceu para o rank §c" + event.getNewLeague().getColor() + event.getNewLeague().getSymbol() + " " + event.getNewLeague().getName());
        }
    }
    
    @EventHandler
    public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
    	Title.send(event.getPlayer(), "§a§l" + event.getGroup().name(), "§fSeu grupo foi atualizado!", SimpleTitle.class);
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		AdminMode.getInstance().removeAdmin(event.getPlayer());
		VanishAPI.getInstance().removeVanish(event.getPlayer());
		CommonGeneral.getInstance().getStatusManager().unloadStatus(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if (BukkitMain.getInstance().isRemovePlayerDat())
			removePlayerFile(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (BukkitMain.getInstance().isRemovePlayerDat())
			removePlayerFile(event.getPlayer().getUniqueId());
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
