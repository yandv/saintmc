package net.saintmc.anticheat.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	private Map<AlertType, List<Alert>> alertList;

	private Alert banAlert;
	private long banTime;

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
		if (alertList.containsKey(alert.getAlertType()))
			if (alertList.get(alert.getAlertType()).size() >= alert.getAlertType().getMaxAlerts()) {
				if (!isBan())
					ban(alert, 60000l);
				return;
			}

		this.alertList.computeIfAbsent(alert.getAlertType(), v -> new ArrayList<>()).add(alert);
	}

	public boolean ban(Alert alert, long time) {
		this.banAlert = alert;
		this.banTime = System.currentTimeMillis() + time;
		return true;
	}

	public boolean isBan() {
		return this.banAlert != null;
	}

	public void alert() {
		List<Alert> alertList = this.alertList.values().stream().filter(a -> !a.isEmpty()).findFirst().orElse(null);

		if (alertList == null)
			return;

		Alert alert = alertList.stream().filter(a -> !a.isAlert()).findFirst().orElse(null);

		if (alert == null)
			return;

		alert.alert();
		BukkitMain.getInstance().getAnticheatController().getAlertController().alert(player, alert,
				(int) alertList.stream().filter(a -> a.isAlert()).count());
	}

}
