package tk.yallandev.saintmc.common.clan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClanHierarchy {
	
	MEMBER("§7§lMEMBRO§f"), RECRUTER("§1§lRECRUTER§1"), ADMIN("§c§lADMIN§c"), OWNER("§4§lOWNER§4");
	
	private String tag;

}
