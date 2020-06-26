package tk.yallandev.saintmc.bungee.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ProxyServer;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;
import tk.yallandev.saintmc.common.utils.web.http.ApacheWebImpl;

public class StoreController {

	private WebHelper web = new ApacheWebImpl();

	public void check() {
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {

			@Override
			public void run() {
				CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.STORE_API, Method.GET,
						new FutureCallback<JsonElement>() {

							@Override
							public void result(JsonElement result, Throwable error) {
								if (error == null) {
									if (!result.isJsonObject())
										return;

									JsonObject jsonObject = (JsonObject) result;

									if (!jsonObject.has("orders"))
										return;

									List<Order> orderList = new ArrayList<>();

									for (int x = 0; x < jsonObject.get("orders").getAsJsonArray().size(); x++) {
										orderList.add(CommonConst.GSON.fromJson(
												jsonObject.get("orders").getAsJsonArray().get(x), Order.class));
									}

									List<Integer> processedOrders = new ArrayList<>();

									for (Order order : orderList) {
										Member member = CommonGeneral.getInstance().getMemberManager()
												.getMember(order.player);

										if (member == null) {
											MemberModel memberModel = CommonGeneral.getInstance().getPlayerData()
													.loadMember(order.player);

											if (memberModel == null)
												continue;

											member = new MemberVoid(memberModel);
										}

										for (String command : order.commands) {
											ProxyServer.getInstance().getPluginManager()
													.dispatchCommand(ProxyServer.getInstance().getConsole(), command);
										}
										
										if (member.hasDiscord()) {
											
										}
										
										member.sendMessage(" ");
										member.sendMessage("§a§l> §fO seu pedido de numero §a" + order.order_id
												+ "§f acabou de ser executado!");
										member.sendMessage(" ");
										processedOrders.add(order.order_id);
										CommonGeneral.getInstance().getMemberManager()
												.broadcast("§a[DEBUG] WOOCommerce -> O pedido " + order.order_id
														+ " acaba de ser executado!", Group.GERENTE);
									}

									if (processedOrders.isEmpty())
										return;

									sendProcessedOrders(processedOrders);
								}
							}
						});
			}
		});
	}

	private boolean sendProcessedOrders(List<Integer> processedOrders) {
		try {
			JsonElement json = web.doRequest(CommonConst.STORE_API, Method.POST, "{\"processedOrders\":["
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
			return web.doRequest(CommonConst.STORE_API, Method.GET);
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