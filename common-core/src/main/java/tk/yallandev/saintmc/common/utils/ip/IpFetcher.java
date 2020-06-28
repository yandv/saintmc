package tk.yallandev.saintmc.common.utils.ip;

import java.util.ArrayList;
import java.util.List;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class IpFetcher {

	private static List<Fetcher> fetcherList;
	private static int fetcherIndex;

	static {
		fetcherList = new ArrayList<>();

		fetcherList.add(new IpApiFetcher());
	}

	public static IpInfo fetchAddress(String ipAddress) throws FetchAddressException {
		IpInfo ipInfo = null;

		if (fetcherIndex >= fetcherList.size())
			fetcherIndex = 0;

		try {
			ipInfo = fetcherList.get(fetcherIndex).fetch(ipAddress);
			fetcherIndex++;
		} catch (Exception ex) {
			throw new FetchAddressException();
		}

		return ipInfo;
	}

	interface Fetcher {

		IpInfo fetch(String ipAddress);

	}

	static class IpApiFetcher implements Fetcher {

		@Override
		public IpInfo fetch(String ipAddress) {
			IpInfo ipInfo = null;

			try {
				ipInfo = CommonConst.GSON.fromJson(
						CommonConst.DEFAULT_WEB.doRequest("http://ip-api.com/json/" + ipAddress, Method.GET),
						IpInfo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ipInfo;
		}
	}

}
