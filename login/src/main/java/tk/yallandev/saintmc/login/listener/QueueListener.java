package tk.yallandev.saintmc.login.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.permission.Group;

public class QueueListener implements Listener {

	private Map<Player, Boolean> queueList;
	private long lastTeleport = System.currentTimeMillis();

	public QueueListener() {
		queueList = new HashMap<>();
	}

	@EventHandler
	public void onPlayerChangeLoginStatus(PlayerChangeLoginStatusEvent event) {
		if (event.isLogged())
			handleQueue(event.getPlayer(), hasPriority(event.getMember()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			if (!member.hasGroupPermission(Group.MOD)) {
				event.getPlayer().sendMessage("§aAutenticado como original!");
				handleQueue(event.getPlayer(), true);
			}

			return;
		}

		if (member.getLoginConfiguration().isPassCaptcha()) {
			if (member.getLoginConfiguration().hasSession(event.getPlayer().getAddress().getHostString())) {
				member.getLoginConfiguration().login(event.getPlayer().getAddress().getHostString());
				event.getPlayer().sendMessage("§aVocê foi autenticado automaticamente!");
				handleQueue(event.getPlayer(), true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		queueList.remove(event.getPlayer());
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			if (queueList.isEmpty())
				return;

			if (lastTeleport > System.currentTimeMillis())
				return;

			Entry<Player, Boolean> entry = queueList.entrySet().stream().findFirst().orElse(null);

			handleTeleport(entry.getKey());

			if (!entry.getValue())
				entry.setValue(true);

			lastTeleport = System.currentTimeMillis() + 2000l;
		}
	}

	public void handleQueue(Player player, boolean priority) {
		if (queueList.containsKey(player))
			return;

		if (player == null) {
			queueList.remove(player);
			return;
		}

		if (priority) {
			queueList.put(player, true);
			handleTeleport(player);
		} else {
			queueList.put(player, false);
			player.sendMessage("§aVocê entrou na fila para entrar no lobby!");
		}
	}

	public void handleTeleport(Player player) {
		BukkitMain.getInstance().sendPlayerToLobby(player);
	}

	public boolean hasPriority(Member member) {
		return member.hasGroupPermission(Group.VIP);
	}

}
