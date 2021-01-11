package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyPlatform;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

public class GamerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.hasGroupPermission(Group.PRO)) {
			player.setAllowFlight(true);
			player.setFlying(true);

			if (member.getGroup().ordinal() <= Group.YOUTUBER.ordinal()
					&& member.getGroup().ordinal() >= Group.PRO.ordinal())
//			if (member.getGroup().ordinal() >= Group.PRO.ordinal())
				Bukkit.broadcastMessage(Tag.valueOf(member.getGroup().name()).getPrefix() + " " + player.getName()
						+ " §6entrou no lobby!");
		} else {
			for (Gamer gamer : LobbyPlatform.getInstance().getPlayerManager().getGamers())
				if (!gamer.isSeeing())
					gamer.getPlayer().hidePlayer(player);

			player.setFlying(false);
			player.setAllowFlight(false);
		}

		player.teleport(
				member.getLoginConfiguration().isLogged() ? BukkitMain.getInstance().getLocationFromConfig("spawn")
						: BukkitMain.getInstance().getLocationFromConfig("login"));

		Title.clear(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		LobbyPlatform.getInstance().getPlayerManager().removeGamer(e.getPlayer());
	}

}
