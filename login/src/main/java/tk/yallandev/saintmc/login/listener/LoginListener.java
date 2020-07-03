package tk.yallandev.saintmc.login.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;

public class LoginListener implements Listener {

	private Map<BukkitMember, Long> playerMap;

	public LoginListener() {
		playerMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.getLoginConfiguration().getAccountType() == AccountType.CRACKED) {
			if (!member.getLoginConfiguration().isLogged()) {
				member.sendMessage(
						member.getLoginConfiguration().isRegistred() ? "§a§l> §fUse §a/login <senha>§f para se logar!"
								: "§a§l> §fUse §a/register <senha> <senha>§f para se registrar!");

				Title.send(event.getPlayer(), member.getLoginConfiguration().isRegistred() ? "§a§lLOGIN" : "§a§lREGISTER",
						member.getLoginConfiguration().isRegistred() ? "§aUse /login <senha> para se logar!"
								: "§aUse /register <senha> para se registrar!",
						SimpleTitle.class);

				playerMap.put(member, System.currentTimeMillis() + 30000);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (playerMap.containsKey(member))
			playerMap.remove(member);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		Iterator<Entry<BukkitMember, Long>> iterator = playerMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<BukkitMember, Long> entry = iterator.next();
			BukkitMember member = entry.getKey();

			if (entry.getValue() > System.currentTimeMillis()) {
				if (member.getLoginConfiguration().isLogged()) {
					iterator.remove();
					continue;
				}

				if (((entry.getValue() - System.currentTimeMillis()) / 1000) % 10 == 0) {
					member.sendMessage(member.getLoginConfiguration().isRegistred()
							? "§a§l> §fUse §a/login <senha>§f para se logar!"
							: "§a§l> §fUse §a/register <senha> <repita a senha>§f para se registrar!");
				}
			} else {
				member.getPlayer()
						.kickPlayer("§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§fVocê demorou muito para se §alogar§f!");
				iterator.remove();
			}
		}
	}

}
