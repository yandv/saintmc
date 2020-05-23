package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent.TeleportResult;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerHideToPlayerEvent;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.event.player.PlayerSpectateEvent;

public class SpectatorListener extends GameListener {

	public SpectatorListener(GameMain main) {
		super(main);
	}

	@EventHandler
	public void onSpectate(PlayerSpectateEvent event) {
		Player p = event.getPlayer();
		
		if (p == null)
			return;
		
		for (Player online : Bukkit.getOnlinePlayers())
			if (online != null)
				if (online.isOnline())
					if (online.getUniqueId() != p.getUniqueId())
						if (online.canSee(p))
							online.hidePlayer(p);
		
		VanishAPI.getInstance().setPlayerVanishToGroup(p, Group.YOUTUBER);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.sendMessage("§a§l> §fVocê está no espectador!");
//		Bukkit.getOnlinePlayers().forEach(player -> BukkitMain.getInstance().getScoreboardManager().updateScoreboard(player));
	}

	@EventHandler
	public void onVisible(PlayerShowToPlayerEvent event) {
		if (!Gamer.getGamer(event.getPlayer()).isSpectator())
			return;
		
		Gamer toGamer = Gamer.getGamer(event.getToPlayer());
		
		if (!toGamer.isNotPlaying()) {
			if (!toGamer.isSpectatorsEnabled())
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerShow(PlayerShowToPlayerEvent event) {
		Gamer toGamer = Gamer.getGamer(event.getToPlayer());
		
		if (AdminMode.getInstance().isAdmin(event.getPlayer()))
			if (!toGamer.isSpectatorsEnabled() && Member.getGroup(event.getPlayer().getUniqueId()).ordinal() - 1 > Member.getGroup(event.getToPlayer().getUniqueId()).ordinal()) {
				event.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onPlayerHide(PlayerHideToPlayerEvent event) {
		Gamer toGamer = Gamer.getGamer(event.getToPlayer());
		
		if (AdminMode.getInstance().isAdmin(event.getPlayer()))
			if (toGamer.isSpectatorsEnabled() && Member.getGroup(event.getPlayer().getUniqueId()).ordinal() - 1 <= Member.getGroup(event.getToPlayer().getUniqueId()).ordinal())  {
				event.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportCommandEvent event) {
		if (event.getResult() != TeleportResult.NO_PERMISSION)
			return;
		
		if (Gamer.getGamer(event.getPlayer()).isSpectator())
			event.setResult(TeleportResult.ONLY_PLAYER_TELEPORT);
	}

}
