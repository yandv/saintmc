package tk.yallandev.saintmc.common.data.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.backend.Query;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoQuery;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
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
import tk.yallandev.saintmc.common.profile.Profile;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;

public class ServerDataImpl implements ServerData {

	private RedisDatabase redisDatabase;
	private Query<JsonElement> query;

	public ServerDataImpl(MongoConnection mongoConnection, RedisDatabase redisDatabase) {
		this.query = createDefault(mongoConnection);
		this.redisDatabase = redisDatabase;
	}

	public ServerDataImpl(Query<JsonElement> query, RedisDatabase redisDatabase) {
		this.query = query;
		this.redisDatabase = redisDatabase;
	}

	@Override
	public String getServerId(String ipAddress) {
		try {
			JsonObject found = (JsonObject) query.findOne("address", ipAddress);

			if (found != null) {
				return found.get("hostname").getAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ipAddress;
	}

	@Override
	public ServerType getServerType(String ipAddress) {

		try {
			JsonObject found = (JsonObject) query.findOne("address", ipAddress);

			if (found != null) {
				return ServerType.valueOf(found.get("serverType").getAsString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ServerType.NONE;
	}

	@Override
	public int getTime(String serverId) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Map<String, String> m = jedis.hgetAll("server:" + serverId);

			if (m.containsKey("time"))
				return Integer.valueOf(m.get("time"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	@Override
	public MinigameState getState(String serverId) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Map<String, String> m = jedis.hgetAll("server:" + serverId);

			if (m.containsKey("state"))
				return MinigameState.valueOf(m.get("state").toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MinigameState.NONE;
	}

	@Override
	public String getMap(String serverId) {

		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Map<String, String> m = jedis.hgetAll("server:" + serverId);

			if (m.containsKey("map"))
				return m.get("map");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Unknown";
	}

	@Override
	public Map<String, Map<String, String>> loadServers() {
		Map<String, Map<String, String>> map = new HashMap<>();
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			String[] str = new String[ServerType.values().length];
			for (int i = 0; i < ServerType.values().length; i++) {
				str[i] = "server:type:" + ServerType.values()[i].toString().toLowerCase();
			}
			for (String server : jedis.sunion(str)) {
				Map<String, String> m = jedis.hgetAll("server:" + server);
				m.put("onlineplayers", getPlayerCount(server) + "");
				map.put(server, m);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
		return map;
	}

	@Override
	public Set<UUID> getPlayers(String serverId) {
		Set<UUID> players = new HashSet<>();
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			for (String uuid : jedis.smembers("server:" + serverId + ":players")) {
				UUID uniqueId = UUID.fromString(uuid);
				players.add(uniqueId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return players;
	}

	@Override
	public void startServer(int maxPlayers) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.sadd("server:type:" + CommonGeneral.getInstance().getServerType().toString().toLowerCase(),
					CommonGeneral.getInstance().getServerId());
			Map<String, String> map = new HashMap<>();
			map.put("type", CommonGeneral.getInstance().getServerType().toString().toLowerCase());
			map.put("maxplayers", maxPlayers + "");
			map.put("joinenabled", "true");
			map.put("address", CommonGeneral.getInstance().getServerAddress());
			pipe.hmset("server:" + CommonGeneral.getInstance().getServerId(), map);
			pipe.del("server:" + CommonGeneral.getInstance().getServerId() + ":players");
			pipe.del("server:" + CommonGeneral.getInstance().getServerId() + ":whitelist");
			ProxiedServer server = new ProxiedServer(CommonGeneral.getInstance().getServerId(),
					CommonGeneral.getInstance().getServerType(), new HashSet<>(), new HashSet<>(), maxPlayers, true);
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<StartPayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.START, new StartPayload(CommonGeneral.getInstance().getServerAddress(), server))));
			pipe.sync();
		}
	}

	@Override
	public void updateStatus(MinigameState state, int time) {
		updateStatus(state, "Unknown", time);
	}

	@Override
	public void updateStatus(MinigameState state, String map, int time) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "map", map);
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "time", Integer.toString(time));
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "state", state.toString().toLowerCase());
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<UpdatePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.UPDATE, new UpdatePayload(time, map, state))));
			pipe.sync();
		}
	}

	@Override
	public void setJoinEnabled(boolean bol) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.hset("server:" + CommonGeneral.getInstance().getServerId(), "joinenabled", Boolean.toString(bol));
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<JoinEnablePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.JOIN_ENABLE, new JoinEnablePayload(bol))));
			pipe.sync();
		}
	}

	@Override
	public void stopServer() {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.srem("server:type:" + CommonGeneral.getInstance().getServerType().toString().toLowerCase(),
					CommonGeneral.getInstance().getServerId());
			pipe.del("server:" + CommonGeneral.getInstance().getServerId());
			pipe.del("server:" + CommonGeneral.getInstance().getServerId() + ":players");
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<StopPayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.STOP, new StopPayload(CommonGeneral.getInstance().getServerId()))));
			pipe.sync();
		}
	}

	@Override
	public void joinPlayer(UUID uuid) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.sadd("server:" + CommonGeneral.getInstance().getServerId() + ":players", uuid.toString());
			pipe.publish("server-info",
					CommonConst.GSON
							.toJson(new DataServerMessage<JoinPayload>(CommonGeneral.getInstance().getServerId(),
									CommonGeneral.getInstance().getServerType(), Action.JOIN, new JoinPayload(uuid))));
			pipe.sync();
		}
	}

	@Override
	public void addWhitelist(Profile profile) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.sadd("server:" + CommonGeneral.getInstance().getServerId() + ":whitelist",
					CommonConst.GSON.toJson(profile));
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<WhitelistAddPayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.WHITELIST_ADD, new WhitelistAddPayload(profile))));
			pipe.sync();
		}
	}

	@Override
	public void leavePlayer(UUID uuid) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.srem("server:" + CommonGeneral.getInstance().getServerId() + ":players", uuid.toString());
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<LeavePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.LEAVE, new LeavePayload(uuid))));
			pipe.sync();
		}
	}

	@Override
	public void removeWhitelist(Profile profile) {
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Pipeline pipe = jedis.pipelined();
			pipe.srem("server:" + CommonGeneral.getInstance().getServerId() + ":whitelist",
					CommonConst.GSON.toJson(profile));
			pipe.publish("server-info",
					CommonConst.GSON.toJson(new DataServerMessage<WhitelistRemovePayload>(
							CommonGeneral.getInstance().getServerId(), CommonGeneral.getInstance().getServerType(),
							Action.WHITELIST_REMOVE, new WhitelistRemovePayload(profile))));
			pipe.sync();
		}
	}

	@Override
	public long getPlayerCount(String serverId) {
		long number;
		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			number = jedis.scard("server:" + serverId + ":players");
		}
		return number;
	}

	@Override
	public long getPlayerCount(ServerType serverType) {
		long number = 0l;

		try (Jedis jedis = redisDatabase.getPool().getResource()) {
			Set<String> servers = jedis.smembers("server:type:" + serverType.toString().toLowerCase());
			for (String serverId : servers) {
				number += jedis.scard("server:" + serverId + ":players");
			}
		}

		return number;
	}

	@Override
	public void closeConnection() {
		redisDatabase.close();
	}

	public static Query<JsonElement> createDefault(MongoConnection mongoConnection) {
		return new MongoQuery(mongoConnection, "saintmc-common", "serverId");
	}

}
