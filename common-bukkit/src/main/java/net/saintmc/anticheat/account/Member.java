package net.saintmc.anticheat.account;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import net.saintmc.anticheat.alert.Alert;
import net.saintmc.anticheat.alert.AlertType;
import net.saintmc.anticheat.storage.DamageStorage;
import net.saintmc.anticheat.storage.InteractStorage;
import net.saintmc.anticheat.storage.InventoryChangeStorage;
import net.saintmc.anticheat.storage.InventoryCloseStorage;
import net.saintmc.anticheat.storage.MoveStorage;
import tk.yallandev.saintmc.bukkit.BukkitMain;

@Getter
public class Member {

	private String playerName;
	private UUID playerId;

	private Player player;
	
	@Setter
	private long lastHit;

	private MoveStorage[] moves = new MoveStorage[16];
	private MoveStorage lastMove;
	@Setter
	private MoveStorage lastMoveOnGround;
	private int moveCount;

	private DamageStorage[] damage = new DamageStorage[16];
	private DamageStorage lastDamage;
	private int damageCount;

	@Setter
	private InventoryChangeStorage lastChangeStorage;
	@Setter
	private InventoryCloseStorage lastInventoryClose;
	@Setter
	private InteractStorage lastInteractStorage;

	private Map<AlertType, Alert> alertList;
	private boolean banTime;

	public Member(Player player) {
		this.playerName = player.getName();
		this.playerId = player.getUniqueId();
		this.player = player;
		this.alertList = new HashMap<>();
	}

	public DamageStorage getDamageAgo(int x) {
		if (this.damageCount - x < 0)
			return null;

		return this.damage[Math.abs(this.damageCount - x) % this.damage.length];
	}

	public MoveStorage getMoveAgo(int x) {
		if (this.moveCount - x < 0)
			return null;

		return this.moves[Math.abs(this.moveCount - x) % this.moves.length];
	}

	public void setLastDamage(DamageStorage lastDamage) {
		this.damage[damageCount % damage.length] = lastDamage;
		this.lastDamage = lastDamage;
		this.damageCount++;
		setLastHit(System.currentTimeMillis());
	}

	public void setLastMove(MoveStorage lastMove) {
		this.moves[moveCount % moves.length] = lastMove;
		this.lastMove = lastMove;
		this.moveCount++;
	}

	public void addAlert(Alert alert) {
		this.alertList.put(alert.getAlertType(), alert);
	}

	public boolean ban(long time) {
		return true;
	}

	public void alert() {
		Alert alert = this.alertList.values().stream().filter(a -> !a.isAlert()).findFirst().orElse(null);

		if (alert == null)
			return;

		alert.alert();
		BukkitMain.getInstance().getAnticheatController().getAlertController().alert(player, alert);
	}

}
