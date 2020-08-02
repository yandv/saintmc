package tk.yallandev.saintmc.bungee.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;
import tk.yallandev.saintmc.common.utils.web.WebHelper.Method;

public class StoreController {

	private boolean checking;

	public void check(CommandSender sender) {
		if (checking)
			return;

		checking = true;

		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getPlugin(), new Runnable() {

			@Override
			public void run() {
				try {
					CommonConst.DEFAULT_WEB.doAsyncRequest(CommonConst.STORE_API, Method.GET,
							new FutureCallback<JsonElement>() {

								@Override
								public void result(JsonElement result, Throwable error) {
									if (error == null) {
										if (!result.isJsonObject()) {
											checking = false;
											return;
										}

										JsonObject jsonObject = (JsonObject) result;

										if (!jsonObject.has("orders")) {
											checking = false;
											sender.sendMessage("§cNenhum pedido encontrado!");
											return;
										}

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
												ProxyServer.getInstance().getPluginManager().dispatchCommand(
														ProxyServer.getInstance().getConsole(), command);
											}

											if (member.hasDiscord()) {

											}

											member.sendMessage("§a§l> §fO seu pedido de numero §a" + order.order_id
													+ "§f acabou de ser executado!");
											member.sendMessage(" ");
											processedOrders.add(order.order_id);

											TextComponent textComponent = new MessageBuilder(
													"§aO pedido " + order.order_id + " acaba de ser executado!")
															.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
																	TextComponent.fromLegacyText(
																			Arrays.asList(order.commands).size() == 1
																					? "§7Comando executado: §f"
																							+ order.commands[0]
																					: "§7Comandos executados: §f"
																							+ Joiner.on(',').join(
																									order.commands))))
															.create();

											CommonGeneral.getInstance().getMemberManager().broadcast(textComponent,
													Group.DIRETOR);

											member.setTag(Tag.valueOf(member.getGroup().name()));
										}

										if (processedOrders.isEmpty()) {
											checking = false;
											sender.sendMessage("§cNenhum pedido encontrado!");
											return;
										}

										sendProcessedOrders(processedOrders);
										sender.sendMessage("§aPedidos executados com sucesso!");
									} else {
										error.printStackTrace();
									}

									checking = false;
								}
							});
				} catch (Exception ex) {
					checking = false;
					ex.printStackTrace();
				}
			}
		});
	}

	private boolean sendProcessedOrders(List<Integer> processedOrders) {
		try {
			JsonElement json = CommonConst.DEFAULT_WEB.doRequest(CommonConst.STORE_API, Method.POST,
					"{\"processedOrders\":["
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
			return CommonConst.DEFAULT_WEB.doRequest(CommonConst.STORE_API, Method.GET);
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
