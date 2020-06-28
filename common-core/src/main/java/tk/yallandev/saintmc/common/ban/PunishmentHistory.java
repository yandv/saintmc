package tk.yallandev.saintmc.common.ban;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.ban.constructor.Warn;

@Getter
public class PunishmentHistory {
	
	private List<Ban> banList;
	private List<Mute> muteList;
	private List<Warn> warnList;
	
	public PunishmentHistory() {
		banList = new ArrayList<>();
		muteList = new ArrayList<>();
		warnList = new ArrayList<>();
	}
	
	public Ban getActiveBan() {
//		for (Ban ban : banList)
//			if (!ban.hasExpired() && !ban.isUnbanned())
//				return ban;
//		
		return banList.stream().filter(ban -> !ban.hasExpired() && !ban.isUnbanned()).findFirst().orElse(null);
	}
	
	public Mute getActiveMute() {
		for (Mute mute: muteList)
			if (!mute.hasExpired() && !mute.isUnmuted())
				return mute;
		
		return null;
	}

	public void ban(Ban ban) {
		banList.add(ban);
	}
	
	public void mute(Mute mute) {
		muteList.add(mute);
	}
	
	public void warn(Warn warn) {
		warnList.add(warn);
	}
	
}
