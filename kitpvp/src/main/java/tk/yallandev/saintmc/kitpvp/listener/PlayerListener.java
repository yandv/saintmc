package tk.yallandev.saintmc.kitpvp.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.bukkit.api.cooldown.event.CooldownFinishEvent;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		Gamer gamer = new Gamer(player);

		GameMain.getInstance().getGamerManager().loadGamer(player.getUniqueId(), gamer);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer == null) {
			player.kickPlayer("§cNão foi possível carregar sua conta!");
		}
	}

	@EventHandler
	public void onCooldownFinish(CooldownFinishEvent event) {
		Kit kit = GameMain.getInstance().getKitManager().getKit(event.getCooldown().getName().replace("Kit ", ""));

		if (kit == null)
			return;

		event.getPlayer().sendMessage("§aVocê agora pode usar o " + event.getCooldown().getName() + "!");
		event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP, 1F, 1F);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameMain.getInstance().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
		event.setQuitMessage(null);
	}

}
