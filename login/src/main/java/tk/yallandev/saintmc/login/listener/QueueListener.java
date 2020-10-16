package tk.yallandev.saintmc.login.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.login.event.MemberQueueEvent;
import tk.yallandev.saintmc.login.event.MemberQueueLeaveEvent;

public class QueueListener implements Listener {

	private Map<Player, Boolean> queueList;
	private long lastTeleport = System.currentTimeMillis();

	public QueueListener() {
		queueList = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMemberQueue(MemberQueueEvent event) {
		handleQueue(event.getPlayer(), event.getMember().hasGroupPermission(Group.LIGHT));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMemberQueueLeave(MemberQueueLeaveEvent event) {
		if (queueList.containsKey(event.getPlayer()))
			queueList.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		/**
		 * If the player is Original and send his to lobby
		 */

		if (member.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			if (!member.hasGroupPermission(Group.MOD)) {
				event.getPlayer().sendMessage("§aVocê foi autenticado como jogador original!");
				event.getPlayer().sendMessage("§aVocê será movido diretamente ao lobby!");
				handleQueue(event.getPlayer(), true);
			}
		} else {
			if (member.getLoginConfiguration().hasSession(event.getPlayer().getAddress().getHostString())) {
				event.getPlayer().sendMessage("§aVocê foi autenticado automaticamente!");
				handleQueue(event.getPlayer(), true);
			}
		}
	}

	@EventHandler
	public void onPlayerChangeLoginStatus(PlayerChangeLoginStatusEvent event) {
		/**
		 * If the player is the login status change
		 */

		if (event.getMember().getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			if (event.getMember().getLoginConfiguration().isLogged()) {
				if (event.getMember().hasGroupPermission(Group.MOD)) {
					event.getPlayer().sendMessage("§aDigite /lobby para ir ao lobby!");
					return;
				}
			}

			handleQueue(event.getPlayer(),
					event.getMember().getLoginConfiguration().getAccountType() == AccountType.ORIGINAL ? true
							: event.getMember().hasGroupPermission(Group.LIGHT));
		} else if (event.isLogged())
			handleQueue(event.getPlayer(), event.getMember().hasGroupPermission(Group.LIGHT));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Bukkit.getPluginManager().callEvent(new MemberQueueLeaveEvent(event.getPlayer(), (BukkitMember) CommonGeneral
				.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId())));
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			if (queueList.isEmpty())
				return;

			if (lastTeleport > System.currentTimeMillis())
				return;

			Entry<Player, Boolean> entry = queueList.entrySet().stream().findFirst().orElse(null);

			if (entry.getValue()) {
				handleTeleport(entry.getKey(), false);
			} else {
				handleTeleport(entry.getKey(), true);
				entry.setValue(true);
			}

			lastTeleport = System.currentTimeMillis() + 2000l;
		}
	}

	public void handleQueue(Player player, boolean priority) {
		if (queueList.containsKey(player))
			return;

		if (player == null)
			queueList.remove(player);

		if (priority) {
			queueList.put(player, true);
			handleTeleport(player, false);
			player.sendMessage("§aVocê tem prioridade na fila!");
			System.out.println("5");
		} else {
			queueList.put(player, false);
			System.out.println("6");
			player.sendMessage("§aVocê foi colocado na fila para se conectar ao lobby! §e(" + queueList.size() + "°)");
		}
	}

	public void handleTeleport(Player player, boolean clear) {
		if (clear)
			for (int x = 0; x < 25; x++)
				player.sendMessage(" ");

		BukkitMain.getInstance().sendPlayerToLobby(player);
	}

}
