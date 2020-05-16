package tk.yallandev.saintmc.common.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.StatusType.Type;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;

public class StatusManager {
	
	private Map<UUID, Map<StatusType, Status>> statusMap;
	
	public StatusManager() {
		statusMap = new HashMap<>();
	}
	
	public Status loadStatus(UUID uuid, StatusType statusType) {
		Map<StatusType, Status> map = statusMap.containsKey(uuid) ? statusMap.get(uuid) : statusMap.computeIfAbsent(uuid, v -> new HashMap<>());
		
		if (map.containsKey(statusType))
			return map.get(statusType);
		
		Status status = CommonGeneral.getInstance().getStatusData().loadStatus(uuid, statusType);
		
		if (status == null) {
			status = statusType.getType() == Type.NORMAL ? new NormalStatus(uuid, statusType) : new GameStatus(uuid, statusType);
			CommonGeneral.getInstance().getStatusData().saveStatus(status);
		}
		
		map.put(statusType, status);
		return status;
	}
	
	public void unloadStatus(UUID uuid, StatusType statusType) {
		if (statusMap.containsKey(uuid))
			statusMap.get(uuid).remove(statusType);
	}
	
	public void unloadStatus(UUID uuid) {
		statusMap.remove(uuid);
	}

}
