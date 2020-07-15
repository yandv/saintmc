package net.saintmc.anticheat.check.register;

import org.bukkit.Material;

import net.saintmc.anticheat.alert.AlertType;
import net.saintmc.anticheat.alert.impl.AlertImpl;
import net.saintmc.anticheat.check.CheckClass;
import net.saintmc.anticheat.check.CheckController.CheckHandler;
import net.saintmc.anticheat.check.CheckController.CheckType;
import net.saintmc.anticheat.storage.MoveStorage;

public class MovementCheck implements CheckClass {

	public static final double SPRINT_MAX_SPEED = 0.9;
	public static final double MAX_SPEED = 0.7;

	@CheckHandler(checkType = CheckType.MOVEMENT)
	public boolean onGlideCheck(MoveStorage moveStorage) {
		if (moveStorage.getTo().getY() - moveStorage.getFrom().getY() == -0.125D
				&& moveStorage.getTo().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.AIR)) {
			moveStorage.getMember().addAlert(new AlertImpl(AlertType.GLIDE, moveStorage.getMember().getPlayerName()));
			return true;
		}

		return false;
	}

//	@CheckHandler(checkType = CheckType.MOVEMENT)
//	public boolean onSpeedCheck(MoveStorage moveStorage) {
//		if (moveStorage.getMember().getLastHit() + 500 > System.currentTimeMillis())
//			return false;
//
//		if (moveStorage.isSpeed()) {
//
//		} else {
//			if (moveStorage.isSpriting()) {
//				if (moveStorage.getHorizontalDistance() >= SPRINT_MAX_SPEED) {
//					moveStorage.getMember()
//							.addAlert(new AlertImpl(AlertType.SPEED, moveStorage.getMember().getPlayerName()));
//					return true;
//				}
//			} else {
//				if (moveStorage.getHorizontalDistance() >= MAX_SPEED) {
//					moveStorage.getMember()
//							.addAlert(new AlertImpl(AlertType.SPEED, moveStorage.getMember().getPlayerName()));
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}

}
