package tk.yallandev.saintmc.common.backend.data;

import java.util.Collection;
import java.util.UUID;

import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;

public interface StatusData {

	Status loadStatus(UUID uniqueId, StatusType statusType);

	void saveStatus(Status status);

	void updateStatus(Status status, String fieldName);

	Collection<Object> ranking(StatusType statusType, String fieldName);

	void deleteStatus(UUID uniqueId, StatusType status);
}
