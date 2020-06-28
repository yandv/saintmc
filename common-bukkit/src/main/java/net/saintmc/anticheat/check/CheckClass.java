package net.saintmc.anticheat.check;

import org.bukkit.entity.Player;

import net.saintmc.anticheat.account.Member;
import net.saintmc.anticheat.alert.Alert;
import net.saintmc.anticheat.alert.AlertMetadata;
import net.saintmc.anticheat.alert.AlertType;
import net.saintmc.anticheat.alert.impl.AlertImpl;
import net.saintmc.anticheat.controller.MemberController;

public interface CheckClass {

	default void alert(Player player, AlertType alertType, AlertMetadata alertMetadata) {
		alert(player, new AlertImpl(alertType, player.getName()).addMetadata(alertMetadata));
	}

	default void alert(Player player, AlertType alertType) {
		alert(player, new AlertImpl(alertType, player.getName()));
	}

	default void alert(Player player, Alert alert) {
		alert(MemberController.INSTANCE.load(player), alert);
	}

	default void alert(Member member, AlertType alertType) {
		alert(member, new AlertImpl(alertType, member.getPlayerName()));
	}

	default void alert(Member member, Alert alert) {
		member.addAlert(alert);
	}

}
