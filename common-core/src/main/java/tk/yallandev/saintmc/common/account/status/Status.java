package tk.yallandev.saintmc.common.account.status;

import java.util.UUID;

public interface Status {

	UUID getUniqueId();
	
	void setUniqueId(UUID uniqueId);
	
	StatusType getStatusType();
	
}
