package tk.yallandev.saintmc.common.account.status.types.challenge;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;

@Getter
public class ChallengeStatus implements Status {

	private UUID uniqueId;
	private StatusType statusType;

	private Map<ChallengeType, ChallengeInfo> challengeInfo;

	public ChallengeStatus(ChallengeModel gameModel) {
		this.uniqueId = gameModel.getUniqueId();
		this.statusType = gameModel.getStatusType();
		this.challengeInfo = gameModel.getChallengeInfo();
	}

	public int getAttemps(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType) ? challengeInfo.get(challengeType).getAttemps() : 0;
	}

	public int getWins(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType) ? challengeInfo.get(challengeType).getWins() : 0;
	}

	public int getTime(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType) ? challengeInfo.get(challengeType).getTime() : 0;
	}

	public boolean hasChallengeType(ChallengeType challengeType) {
		return challengeInfo.containsKey(challengeType);
	}

}
