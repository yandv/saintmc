package tk.yallandev.saintmc.login.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.configuration.LoginConfiguration.AccountType;
import tk.yallandev.saintmc.login.event.captcha.CaptchaSuccessEvent;

public class LoginListener implements Listener {

	private Map<BukkitMember, Long> playerMap;

	public LoginListener() {
		playerMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.getLoginConfiguration().isPassCaptcha())
			handleLogin(event.getPlayer());
	}

	@EventHandler
	public void onPlayerJoin(CaptchaSuccessEvent event) {
		handleLogin(event.getPlayer());
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<BukkitMember, Long>> iterator = playerMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<BukkitMember, Long> entry = iterator.next();
				BukkitMember member = entry.getKey();

				if (!member.getPlayer().isOnline()) {
					iterator.remove();
					continue;
				}

				if (member.getLoginConfiguration().isLogged()) {
					iterator.remove();
					return;
				}

				if (System.currentTimeMillis() > entry.getValue()) {
					long timeRemeaning = System.currentTimeMillis() - entry.getValue();

					if (timeRemeaning > 30000) {
						iterator.remove();
						member.getPlayer().kickPlayer("§cVocê excedeu o limite de tempo para se autenticar!");
					} else {
						ActionBarAPI.send(member.getPlayer(),
								"§cVocê possui " + ((30000 - timeRemeaning) / 1000l) + " para se logar!");
					}
				}
			}
		}
	}

	void handleLogin(Player player) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (member.getLoginConfiguration().getAccountType() == AccountType.CRACKED) {
			if (!member.getLoginConfiguration().isLogged()) {
				member.sendMessage(
						member.getLoginConfiguration().isRegistred() ? "§aUtilize o comando /login <senha> para logar."
								: "§aUtilize o comando /register <senha> <senha> para se registrar.");

				new SimpleTitle("§2§lAUTENTICAÇÃO",
						member.getLoginConfiguration().isRegistred() ? "§fUse /login <senha> para se logar!"
								: "§fUse /register <senha> para se registrar!",
						10, 20 * 99, 10).send(player);

				playerMap.put(member, System.currentTimeMillis() + 1500l);
			}
		}
	}

}
