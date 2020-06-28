package tk.yallandev.saintmc.common.account.medal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedalRarity {

	COMMON("§7Comum"), RARE("§eRaro"), VERY_RARE("§6Muito Raro"), EPIC("§5Épica");

	private String name;

}
