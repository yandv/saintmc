package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.account.League;

@Getter
public class PlayerChangeLeagueEvent extends PlayerCancellableEvent {
	
	private BukkitMember bukkitMember;
	private League oldLeague;
	private League newLeague;

	public PlayerChangeLeagueEvent(Player p, BukkitMember player, League oldLeague, League newLeague) {
		super(p);
		this.bukkitMember = player;
		this.oldLeague = oldLeague;
		this.newLeague = newLeague;
	}
}
