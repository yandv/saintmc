package tk.yallandev.saintmc.common.backend.data;

import tk.yallandev.saintmc.common.utils.ip.IpInfo;

public interface IpData {

	IpInfo loadIp(String ipAddress);

	void registerIp(IpInfo ipInfo);

	void updateIp(IpInfo ipInfo, String fieldName);

	void deleteIp(String ipAddress);

}
