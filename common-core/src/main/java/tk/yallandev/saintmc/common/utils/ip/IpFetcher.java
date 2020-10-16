package tk.yallandev.saintmc.common.utils.ip;

import java.util.ArrayList;
import java.util.List;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class IpFetcher {

	public static final IpFetcher IP_FETCHER = new IpFetcher();

	private List<Fetcher> fetcherList;
	private int fetcherIndex;

	public IpFetcher() {
		fetcherList = new ArrayList<>();

		fetcherList.add(new IpApiFetcher());
	}

	public static void main(String[] args) {

		try {
			System.out.println(CommonConst.DEFAULT_WEB.doRequest("http://ip-api.com/json/185.37.149.173", Method.GET));
			System.out.println(CommonConst.DEFAULT_WEB.doRequest("http://ip-api.com/json/186.221.89.57", Method.GET));

			IpInfo ipInfo = IP_FETCHER.fetchAddress("185.37.149.173");

			ipInfo.check();

			System.out.println(CommonConst.GSON.toJson(ipInfo));
			System.out.println(CommonConst.GSON.toJson(IP_FETCHER.fetchAddress("186.221.89.57")));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public IpInfo fetchAddress(String ipAddress) throws FetchAddressException {
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

	class IpApiFetcher implements Fetcher {

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
