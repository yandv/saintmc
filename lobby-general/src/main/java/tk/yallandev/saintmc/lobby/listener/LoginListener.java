package tk.yallandev.saintmc.lobby.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.tag.TagWrapper;

public class LoginListener implements Listener {

	public static final Tag LOGGING_TAG = TagWrapper.create("§8§lLOGANDO§8", null);

	private static final int MAX_PLAYERS = 8;

	private Map<BukkitMember, Long> playerList;

	public LoginListener() {
		playerList = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member == null)
			return;

		if (member.getLoginConfiguration().isLogged())
			if (playerList.size() >= MAX_PLAYERS)
				if (!member.hasGroupPermission(Group.VIP)) {
					event.disallow(Result.KICK_OTHER,
							"§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§cO servidor de login está cheio!");
				}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.getLoginConfiguration().getAccountType() == AccountType.CRACKED) {
			if (!member.getLoginConfiguration().isLogged()) {
				member.setTag(LOGGING_TAG, true);

				member.sendMessage(member.getLoginConfiguration().isRegistred() ? "§aUse /login <senha> para se logar!"
						: "§aUse /register <senha> <senha> para se registrar!");

				Title.send(player, member.getLoginConfiguration().isRegistred() ? "§a§lLOGIN" : "§a§lREGISTER",
						member.getLoginConfiguration().isRegistred() ? "§aUse /login <senha> para se logar!"
								: "§aUse /register <senha> para se registrar!",
						SimpleTitle.class);

				playerList.put(member, System.currentTimeMillis() + 30000);
			}
		} else
			player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveUpdateEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (playerList.containsKey(member))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerQuitEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (playerList.containsKey(member))
			playerList.remove(member);
	}

	@EventHandler
	public void onLoginListener(PlayerChangeLoginStatusEvent event) {
		if (event.isLogged() || event.getMember().getLoginConfiguration().getAccountType() == AccountType.ORIGINAL) {
			playerList.remove(event.getMember());
			event.getMember().setTag(((BukkitMember) event.getMember()).getDefaultTag());
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		Iterator<Entry<BukkitMember, Long>> iterator = playerList.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<BukkitMember, Long> entry = iterator.next();
			BukkitMember member = entry.getKey();

			if (entry.getValue() > System.currentTimeMillis()) {
				if (member.getLoginConfiguration().isLogged()) {
					playerList.remove(member);
					continue;
				}

				if (((entry.getValue() - System.currentTimeMillis()) / 1000) % 10 == 0) {
					member.sendMessage(
							member.getLoginConfiguration().isRegistred() ? "§aUse /login <senha> para se logar!"
									: "§aUse /register <senha> <repita a senha> para se registrar!");
				}

				member = null;
			} else {
				member.getPlayer()
						.kickPlayer("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fVocê demorou muito para se §alogar§f!");
				playerList.remove(member);
			}
		}
	}

}
