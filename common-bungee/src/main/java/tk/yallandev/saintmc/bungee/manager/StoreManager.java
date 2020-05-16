package tk.yallandev.saintmc.bungee.manager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.web.WebHelper;
import tk.yallandev.saintmc.common.utils.web.http.ApacheWebImpl;

public class StoreManager {
	
	private WebHelper web = new ApacheWebImpl();

	public boolean check() {
		JsonElement json = getPendingOrders();
		
		if (!json.isJsonObject())
			return false;
		
		JsonObject jsonObject = (JsonObject) json;

		if (!jsonObject.has("orders"))
			return false;

		List<Order> orderList = new ArrayList<>();

		for (int x = 0; x < jsonObject.get("orders").getAsJsonArray().size(); x++) {
			orderList.add(CommonConst.GSON.fromJson(jsonObject.get("orders").getAsJsonArray().get(x), Order.class));
		}

		List<Integer> processedOrders = new ArrayList<>();

		for (Order order : orderList) {
			ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(order.player);

			if (proxiedPlayer == null)
				continue;

			Member member = CommonGeneral.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());

			for (String command : order.commands) {
				ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
						command);
			}

			member.sendMessage(" ");
			member.sendMessage("§a§l> §fO seu pedido de numero §a" + order.order_id + "§f acabou de ser executado!");
			member.sendMessage(" ");
			processedOrders.add(order.order_id);
			CommonGeneral.getInstance().debug("WOOCommerce -> The order " + order.order_id + " has been sent!");
		}

		if (processedOrders.isEmpty())
			return false;

		return sendProcessedOrders(processedOrders);
	}

	private boolean sendProcessedOrders(List<Integer> processedOrders) {
		try {
			JsonElement json = web.post(CommonConst.STORE_URL, "{\"processedOrders\":["
					+ processedOrders.stream().map(Object::toString).collect(Collectors.joining(",")) + "]}");
			
			if (!json.isJsonObject())
				return false;
			
			JsonObject bodyResponse = (JsonObject) json;

			if (bodyResponse.get("code") != null) {
				CommonGeneral.getInstance()
						.debug("Received error when trying to send post data:" + bodyResponse.get("code"));
				throw new Exception(bodyResponse.get("message").getAsString());
			}
			
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public JsonElement getPendingOrders() {
		
		try {
			return web.get(CommonConst.STORE_URL);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return new JsonObject();
	}

	public class Order {

		String player;
		int order_id;
		String[] commands;

	}

}
