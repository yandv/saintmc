package tk.yallandev.saintmc.common.account.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeStatus;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;

@AllArgsConstructor
@Getter
public enum StatusType {

	PVP("pvp", NormalStatus.class), GLADIATOR("gladiator", NormalStatus.class),
	LOBBY("lobby-combat", NormalStatus.class), SHADOW("shadow", NormalStatus.class),

	LAVA("lava", ChallengeStatus.class), MLG("mlg", ChallengeStatus.class),

	HG("hungergames", GameStatus.class), SW_SOLO("skywars-solo", GameStatus.class),
	SW_TEAM("skywars-team", GameStatus.class), SW_SQUAD("skywars-squad", GameStatus.class);

	private String mongoCollection;
	private Class<? extends Status> statusClass;

}
