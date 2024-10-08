package tk.yallandev.saintmc.bukkit.networking.redis;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.JedisPubSub;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.account.PlayerUpdateFieldEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerUpdatedFieldEvent;
import tk.yallandev.saintmc.bukkit.event.report.ReportReceiveEvent;
import tk.yallandev.saintmc.bukkit.event.server.PlayerChangeEvent;
import tk.yallandev.saintmc.bukkit.event.server.ServerPlayerJoinEvent;
import tk.yallandev.saintmc.bukkit.event.server.ServerPlayerLeaveEvent;
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

public class BukkitPubSubHandler extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

		if (channel.equals("server-members")) {
			BukkitMain.getInstance().getServerManager().setTotalMembers(jsonObject.get("totalMembers").getAsInt());
			Bukkit.getPluginManager().callEvent(new PlayerChangeEvent(jsonObject.get("totalMembers").getAsInt()));
			return;
		}

		if (!jsonObject.has("source")
				|| jsonObject.get("source").getAsString().equals(CommonGeneral.getInstance().getServerId()))
			return;

		switch (channel) {
		case "server-info": {
			if (!BukkitMain.getInstance().isServerLog())
				return;

			ServerType sourceType = ServerType.valueOf(jsonObject.get("serverType").getAsString());

			if (sourceType == ServerType.NETWORK)
				return;

			String source = jsonObject.get("source").getAsString();
			Action action = Action.valueOf(jsonObject.get("action").getAsString());

			switch (action) {
			case JOIN: {
				DataServerMessage<JoinPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<JoinPayload>>() {
						}.getType());
				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.joinPlayer(payload.getPayload().getUniqueId());
				Bukkit.getPluginManager().callEvent(new ServerPlayerJoinEvent(payload.getPayload().getUniqueId(),
						server.getServerId(), server.getServerType(), server));
				break;
			}
			case LEAVE: {
				DataServerMessage<LeavePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<LeavePayload>>() {
						}.getType());

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.leavePlayer(payload.getPayload().getUniqueId());
				Bukkit.getPluginManager().callEvent(new ServerPlayerLeaveEvent(payload.getPayload().getUniqueId(),
						server.getServerId(), server.getServerType(), server));
				break;
			}
			case JOIN_ENABLE: {
				DataServerMessage<JoinEnablePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<JoinEnablePayload>>() {
						}.getType());

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.setJoinEnabled(payload.getPayload().isEnable());
				break;
			}
			case START: {
				DataServerMessage<StartPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<StartPayload>>() {
						}.getType());
				BukkitMain.getInstance().getServerManager().addActiveServer(payload.getPayload().getServerAddress(),
						payload.getPayload().getServer().getServerId(), sourceType,
						payload.getPayload().getServer().getMaxPlayers());
				break;
			}
			case STOP: {
				DataServerMessage<StopPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<StopPayload>>() {
						}.getType());

				BukkitMain.getInstance().getServerManager().removeActiveServer(payload.getPayload().getServerId());
				break;
			}
			case UPDATE: {
				DataServerMessage<UpdatePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<UpdatePayload>>() {
						}.getType());
				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				if (server instanceof MinigameServer) {
					((MinigameServer) server).setState(payload.getPayload().getState());
					((MinigameServer) server).setTime(payload.getPayload().getTime());
					((MinigameServer) server).setMap(payload.getPayload().getMap());
				}
				break;
			}
			case WHITELIST_ADD: {
				DataServerMessage<WhitelistAddPayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<WhitelistAddPayload>>() {
						}.getType());

				if (sourceType == ServerType.NETWORK) {
					break;
				}

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

				if (server == null)
					return;

				server.addWhitelist(payload.getPayload().getProfile());
				break;
			}
			case WHITELIST_REMOVE: {
				DataServerMessage<WhitelistRemovePayload> payload = CommonConst.GSON.fromJson(jsonObject,
						new TypeToken<DataServerMessage<WhitelistRemovePayload>>() {
						}.getType());

				if (sourceType == ServerType.NETWORK) {
					break;
				}

				ProxiedServer server = BukkitMain.getInstance().getServerManager().getServer(source);

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

			if (action.equalsIgnoreCase("remove")) {
				if (CommonGeneral.getInstance().getReportManager().getReport(playerUuid) != null)
					CommonGeneral.getInstance().getReportManager().unloadReport(playerUuid);
			} else if (action.equalsIgnoreCase("create")) {
				if (!jsonObject.has("value"))
					break;

				Report report = CommonConst.GSON.fromJson(jsonObject.get("value"), Report.class);

				if (CommonGeneral.getInstance().getReportManager().getReport(playerUuid) == null) {
					CommonGeneral.getInstance().getReportManager().loadReport(report);
					CommonGeneral.getInstance().debug("The report of " + report.getPlayerName() + " has been loaded!");

					Bukkit.getPluginManager().callEvent(new ReportReceiveEvent(report));
				}
			}

			break;
		}
		case "account-field": {
			UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
			Player p = BukkitMain.getInstance().getServer().getPlayer(uuid);
			BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(uuid);

			if (p != null && player != null) {
				try {
					Field field = getField(Member.class, jsonObject.get("field").getAsString());
					Object oldObject = field.get(player);
					Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
					PlayerUpdateFieldEvent event = new PlayerUpdateFieldEvent(p, player, field.getName(), oldObject,
							object);
					Bukkit.getPluginManager().callEvent(event);

					if (!event.isCancelled()) {
						field.set(player, event.getObject());
						PlayerUpdatedFieldEvent event2 = new PlayerUpdatedFieldEvent(p, player, field.getName(),
								oldObject, object);
						Bukkit.getPluginManager().callEvent(event2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		}

	}

	private Field getField(Class<?> clazz, String fieldName) {
		while ((clazz != null) && (clazz != Object.class)) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return null;
	}

}
