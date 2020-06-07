package tk.yallandev.saintmc.bukkit.permission.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.permission.PermissionManager;
import tk.yallandev.saintmc.common.permission.Group;

public class PermissionListener implements Listener {

	private final Map<UUID, PermissionAttachment> attachments;
	private PermissionManager manager;

	public PermissionListener(PermissionManager manager) {
		this.attachments = new HashMap<>();
		this.manager = manager;

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : manager.getServer().getOnlinePlayers()) {
					updateAttachment(player, getServerGroup(player));
				}
			}
		}.runTaskLater(manager.getPlugin(), 10);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		updateAttachment(player, getServerGroup(player));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMonitorLogin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED) {
			removeAttachment(event.getPlayer());
		}
	}

	@EventHandler
	public void onChangeGroupEvent(PlayerChangeGroupEvent event) {
		updateAttachment(event.getPlayer(), event.getGroup());
	}

	protected void updateAttachment(Player player, Group group) {
		PermissionAttachment attach = (PermissionAttachment) attachments.get(player.getUniqueId());
		Permission playerPerm = getCreateWrapper(player, player.getUniqueId().toString());
		
		if (attach == null) {
			attach = player.addAttachment(manager.getPlugin());
			attachments.put(player.getUniqueId(), attach);
			attach.setPermission(playerPerm, true);
		}
		
		playerPerm.getChildren().clear();
		
		for (String perm : group.getGroup().getPermissions()) {
			if (!playerPerm.getChildren().containsKey(perm)) {
				playerPerm.getChildren().put(perm, true);
			}
		}
		
		for (String permission : getPermissions(player))
			if (!playerPerm.getChildren().containsKey(permission)) {
				playerPerm.getChildren().put(permission, true);
			}

		player.recalculatePermissions();
		
		player.recalculatePermissions();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		removeAttachment(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PlayerKickEvent event) {
		removeAttachment(event.getPlayer());
	}

	protected void removeAttachment(Player player) {
		PermissionAttachment attach = (PermissionAttachment) this.attachments.remove(player.getUniqueId());
		if (attach != null) {
			attach.remove();
		}
		manager.getServer().getPluginManager().removePermission(player.getUniqueId().toString());
	}

	public void onDisable() {
		for (PermissionAttachment attach : attachments.values()) {
			attach.remove();
		}
		attachments.clear();
	}
	
	private Permission getCreateWrapper(Player player, String name) {
		Permission perm = manager.getServer().getPluginManager().getPermission(name);
		if (perm == null) {
			perm = new Permission(name, "Interal Permission", PermissionDefault.FALSE);
			manager.getServer().getPluginManager().addPermission(perm);
		}
		return perm;
	}
	
	private Group getServerGroup(Player player) {
		if (CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()) == null)
			return Group.MEMBRO;

		return CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).getServerGroup();
	}

	private Set<String> getPermissions(Player player) {
		if (CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()) == null)
			return new HashSet<>();

		return CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()).getPermissions().keySet();
	}
}
