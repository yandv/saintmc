package tk.yallandev.saintmc.bungee.networking.redis;

import java.lang.reflect.Field;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.JedisPubSub;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.bungee.bungee.BungeeMember;
import tk.yallandev.saintmc.bungee.event.player.PlayerUpdateFieldEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.Action;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.JoinEnablePayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.JoinPayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.LeavePayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.StartPayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.StopPayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.UpdatePayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.WhitelistAddPayload;
import tk.yallandev.saintmc.common.data.payload.DataServerMessage.WhitelistRemovePayload;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.reflection.Reflection;

public class BungeePubSubHandler extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		if (!(message.startsWith("{") && message.endsWith("}")))
			return;

		JsonObject jsonObject = (JsonObject) JsonParser.parseString(message);

		if (!jsonObject.has("source")
				|| jsonObject.get("source").getAsString().equalsIgnoreCase(CommonGeneral.getInstance().getServerId()))
			return;

		switch (channel) {
		case "clan-field": {
			UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			Clan report = CommonGeneral.getInstance().getClanManager().getClan(uuid);

			if (report == null)
				break;

			try {
				Field field = Reflection.getField(Clan.class, jsonObject.get("field").getAsString());
				field.setAccessible(true);
				Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
				field.set(report, object);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "report-field": {
			UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			Report report = CommonGeneral.getInstance().getReportManager().getReport(uuid);

			if (report == null)
				break;

			try {
				Field field = Reflection.getField(Report.class, jsonObject.get("field").getAsString());
				field.setAccessible(true);
				Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
				field.set(report, object);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case "report-action": {
			UUID playerUuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			String action = jsonObject.get("action").getAsString();

			switch (action) {
			case "remove": {
				if (CommonGeneral.getInstance().getReportManager().getReport(playerUuid) != null)
					CommonGeneral.getInstance().getReportManager().unloadReport(playerUuid);
				break;
			}
			default:
				break;
			}
			break;
		}
		case "account-field": {
			UUID uuid = UUID.fromString(jsonObject.getAsJsonPrimitive("uniqueId").getAsString());
			ProxiedPlayer p = BungeeMain.getPlugin().getProxy().getPlayer(uuid);

			if (p == null)
				return;

			Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

			if (player == null)
				return;

			try {
				Field f = Reflection.getField(Member.class, jsonObject.get("field").getAsString());
				Object oldObject = f.get(player);
				Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), f.getGenericType());
				f.setAccessible(true);
				f.set(player, object);

				ProxyServer.getInstance().getPluginManager()
						.callEvent(new PlayerUpdateFieldEvent((BungeeMember) player, f.getName(), oldObject, object));
				System.out.println(object);
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			break;
		}
		case "server-info": {
			String source = jsonObject.get("source").getAsString();

			ServerType sourceType = ServerType.valueOf(jsonObject.get("serverType").getAsString());
			Action action = Action.valueOf(jsonObject.get("action").getAsString());

			if (sourceType == ServerType.NETWORK)
				break;

			switch (action) {
			case JOIN: {
				DataServerMessage<JoinPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<JoinPayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.joinPlayer(payload.getPayload().getUniqueId());
				break;
			}
			case LEAVE: {
				DataServerMessage<LeavePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<LeavePayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.leavePlayer(payload.getPayload().getUniqueId());
				break;
			}
			case JOIN_ENABLE: {
				DataServerMessage<JoinEnablePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<JoinEnablePayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.setJoinEnabled(payload.getPayload().isEnable());
				break;
			}
			case START: {
				DataServerMessage<StartPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<StartPayload>>() {
						}.getType());
				BungeeMain.getPlugin().getServerManager().addActiveServer(payload.getPayload().getServerAddress(),
						payload.getPayload().getServer().getServerId(), sourceType,
						payload.getPayload().getServer().getMaxPlayers());
				break;
			}
			case STOP: {
				DataServerMessage<StopPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<StopPayload>>() {
						}.getType());

				if (sourceType == ServerType.NETWORK) {
					break;
				}

				BungeeMain.getPlugin().getServerManager().removeActiveServer(payload.getPayload().getServerId());
				break;
			}
			case UPDATE: {
				DataServerMessage<UpdatePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<UpdatePayload>>() {
						}.getType());
				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				if (server instanceof MinigameServer) {
					((MinigameServer) server).setState(payload.getPayload().getState());
					((MinigameServer) server).setTime(payload.getPayload().getTime());
				}
				break;
			}
			case WHITELIST_ADD: {
				DataServerMessage<WhitelistAddPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<WhitelistAddPayload>>() {
						}.getType());

				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.addWhitelist(payload.getPayload().getProfile());
				break;
			}
			case WHITELIST_REMOVE: {
				DataServerMessage<WhitelistRemovePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<WhitelistRemovePayload>>() {
						}.getType());

				ProxiedServer server = BungeeMain.getPlugin().getServerManager().getServer(source);

				if (server == null)
					return;

				server.removeWhitelist(payload.getPayload().getProfile());
				break;
			}
			default:
				break;
			}
			break;
		}
		}
	}

}
