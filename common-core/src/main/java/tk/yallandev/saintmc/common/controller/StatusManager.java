package tk.yallandev.saintmc.common.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;

public class StatusManager {

	private Map<UUID, Map<StatusType, Status>> statusMap;

	public StatusManager() {
		statusMap = new HashMap<>();
	}

	public Status loadStatus(UUID uuid, StatusType statusType) {
		Map<StatusType, Status> map = statusMap.containsKey(uuid) ? statusMap.get(uuid)
				: statusMap.computeIfAbsent(uuid, v -> new HashMap<>());

		if (map.containsKey(statusType))
			return map.get(statusType);

		Status status = CommonGeneral.getInstance().getStatusData().loadStatus(uuid, statusType);

		if (status == null) {
			try {
				status = statusType.getStatusClass().getConstructor(UUID.class, StatusType.class).newInstance(uuid,
						statusType);
				CommonGeneral.getInstance().getStatusData().saveStatus(status);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		map.put(statusType, status);
		return status;
	}
	public <T> T loadStatus(UUID uuid, StatusType statusType, Class<? extends T> clazz) {
		Map<StatusType, Status> map = statusMap.containsKey(uuid) ? statusMap.get(uuid)
				: statusMap.computeIfAbsent(uuid, v -> new HashMap<>());

		if (map.containsKey(statusType))
			return clazz.cast(map.get(statusType));

		Status status = CommonGeneral.getInstance().getStatusData().loadStatus(uuid, statusType);

		if (status == null) {
			try {
				status = statusType.getStatusClass().getConstructor(UUID.class, StatusType.class).newInstance(uuid,
						statusType);
				CommonGeneral.getInstance().getStatusData().saveStatus(status);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		map.put(statusType, status);
		return clazz.cast(map.get(statusType));
	}

	public void unloadStatus(UUID uuid, StatusType statusType) {
		if (statusMap.containsKey(uuid))
			statusMap.get(uuid).remove(statusType);
	}

	public void unloadStatus(UUID uuid) {
		statusMap.remove(uuid);
	}

}
