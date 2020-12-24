package tk.yallandev.saintmc.kitpvp.warp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tk.yallandev.saintmc.common.account.status.StatusType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WarpSetting {

	private boolean uniqueChat;
	private boolean damageEnabled = true;
	private boolean pvpEnabled = true;
	private boolean kitEnabled = false;

	private boolean spawnProtection;

	private boolean armorInfinity = true;
	private boolean swordInfinity = true;

	private boolean warpEnabled = true;
	private boolean spawnEnabled = true;

	private StatusType statusType = StatusType.PVP;

}
