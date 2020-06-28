package tk.yallandev.saintmc.skwyars.gamer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.skwyars.event.kit.PlayerSelectKitEvent;
import tk.yallandev.saintmc.skwyars.event.kit.PlayerSelectedKitEvent;
import tk.yallandev.saintmc.skwyars.event.player.PlayerSpectateEvent;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;
import tk.yallandev.saintmc.skwyars.game.team.Team;

@Getter
public class Gamer {

	private UUID uniqueId;

	/*
	 * Match
	 */

	@Setter
	private Team team;
	private Kit kit;
	private int matchKills;

	/*
	 * Settings
	 */

	private boolean spectator;
	@Setter
	private boolean gamemaker;

	private transient Player player;

	public Gamer(Player player) {
		this.uniqueId = player.getUniqueId();
		this.player = player;
	}

	public boolean hasTeam() {
		return team != null;
	}

	public boolean isPlaying() {
		if (spectator)
			return false;

		if (gamemaker)
			return false;

		return team != null;
	}

	public void selectKit(Kit kit) {
		PlayerSelectKitEvent playerSelectKitEvent = new PlayerSelectKitEvent(player, this, kit);

		Bukkit.getPluginManager().callEvent(playerSelectKitEvent);

		if (!playerSelectKitEvent.isCancelled()) {
			PlayerSelectedKitEvent playerSelectedKitEvent = new PlayerSelectedKitEvent(player, kit);
			this.kit = kit;
			Bukkit.getPluginManager().callEvent(playerSelectedKitEvent);
		}
	}

	public void setSpectator(boolean spectator) {
		this.spectator = spectator;

		if (spectator) {
			PlayerSpectateEvent event = new PlayerSpectateEvent(getPlayer());
			Bukkit.getPluginManager().callEvent(event);
		}
	}

	public boolean isSpectatorsEnabled() {
		return isSpectator();
	}

	public boolean isNotPlaying() {
		return !isPlaying();
	}

	public String getPlayerName() {
		return player.getName();
	}

	public boolean hasKit(String name) {
		return CommonGeneral.getInstance().getMemberManager().getMember(getUniqueId())
				.hasPermission(CommonGeneral.getInstance().getServerType().name().toLowerCase().replace("_", "-")
						+ ".kit." + name.toLowerCase());
	}

	public boolean hasKit() {
		return kit != null;
	}

	public void addMatchKill() {
		matchKills++;
	}

}
